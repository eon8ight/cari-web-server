package com.cari.web.server.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import com.cari.web.server.domain.db.Aesthetic;
import com.cari.web.server.domain.db.MediaCreator;
import com.cari.web.server.dto.response.CariPage;
import com.cari.web.server.dto.response.EditResponse;
import com.cari.web.server.repository.AestheticMediaRepository;
import com.cari.web.server.repository.AestheticRelationshipRepository;
import com.cari.web.server.repository.AestheticRepository;
import com.cari.web.server.repository.AestheticWebsiteRepository;
import com.cari.web.server.repository.MediaCreatorRepository;
import com.cari.web.server.service.AestheticService;
import com.cari.web.server.util.QueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AestheticServiceImpl implements AestheticService {

    private static final int MAX_PER_PAGE = 20;

    private static final String FILTER_KEYWORD = "keyword";
    private static final String FILTER_START_YEAR = "startYear";
    private static final String FILTER_END_YEAR = "endYear";

    private static final Map<String, String> SORT_FIELDS =
            Map.of("name", "name", FILTER_START_YEAR, "fn_get_approximate_start_year(aesthetic)",
                    FILTER_END_YEAR, "fn_get_approximate_end_year(aesthetic)");

    @Autowired
    private AestheticRepository aestheticRepository;

    @Autowired
    private AestheticWebsiteRepository aestheticWebsiteRepository;

    @Autowired
    private AestheticMediaRepository aestheticMediaRepository;

    @Autowired
    private AestheticRelationshipRepository aestheticRelationshipRepository;

    @Autowired
    private MediaCreatorRepository mediaCreatorRepository;

    @Autowired
    private DataSource dbHandle;

    @Override
    public Page<Aesthetic> findForList(Map<String, String> filters) {
        StringBuilder queryBuilder =
                new StringBuilder("select count(*) over (), * from tb_aesthetic ");

        MapSqlParameterSource params = new MapSqlParameterSource();

        /* WHERE */

        String keyword = filters.get(FILTER_KEYWORD);
        Optional<Integer> startYear = QueryUtils.validateAndGetInt(filters, FILTER_START_YEAR);
        Optional<Integer> endYear = QueryUtils.validateAndGetInt(filters, FILTER_END_YEAR);

        List<String> filterClauses = new ArrayList<String>();

        if (keyword != null) {
            filterClauses.add(
                    "name ilike '%' || :keyword || '%' or description ilike '%' || :keyword || '%'");

            params.addValue("keyword", keyword);
        }

        if (startYear.isPresent()) {
            filterClauses.add("abs(fn_get_approximate_start_year(aesthetic) - :startYear) <= 3");
            params.addValue("startYear", startYear.get().intValue());
        }

        if (endYear.isPresent()) {
            filterClauses.add("abs(fn_get_approximate_end_year(aesthetic) - :endYear) <= 3");
            params.addValue("endYear", endYear.get().intValue());
        }

        queryBuilder.append(QueryUtils.toWhereClause(filterClauses));

        /* ORDER BY */

        Sort sort = QueryUtils.validateAndGetSort(filters, SORT_FIELDS,
                () -> Sort.by(Sort.Order.asc("startYear").nullsLast(),
                        Sort.Order.asc("endYear").nullsLast()));

        queryBuilder.append(QueryUtils.toOrderByClause(sort, SORT_FIELDS));

        /* LIMIT and OFFSET */

        Optional<Integer> pageNumOptional =
                QueryUtils.validateAndGetIntNonNegative(filters, QueryUtils.FILTER_PAGE);

        int pageNum = pageNumOptional.orElse(0);

        queryBuilder.append("limit :limit offset :offset");
        params.addValue("limit", MAX_PER_PAGE);
        params.addValue("offset", pageNum * MAX_PER_PAGE);

        return CariPage.getPage(dbHandle, queryBuilder.toString(), params, pageNum, MAX_PER_PAGE,
                sort, Aesthetic::fromResultSet);
    }

    @Override
    public Optional<Aesthetic> findForPage(String urlSlug) {
        return aestheticRepository.findForPage(urlSlug);
    }

    @Override
    public Optional<Aesthetic> findForEdit(int aesthetic) {
        return aestheticRepository.findForEdit(aesthetic);
    }

    @Override
    public Optional<Aesthetic> find(int aesthetic) {
        return aestheticRepository.simpleFind(aesthetic);
    }

    @Override
    public List<Aesthetic> findNames(Optional<String> query) {
        return aestheticRepository.findNames(query.orElse(""));
    }

    @Override
    @Transactional
    public EditResponse createOrUpdate(Aesthetic aesthetic) {
        /*
         * TODO: 1. Validate name not in use 2. Validate symbol not in use
         */

        String urlSlug = aesthetic.getName().toLowerCase().strip()
                .replaceAll("[^a-zA-Z0-9-\\s]", "").replaceAll("\\s+", "-");

        aesthetic.setUrlSlug(urlSlug);
        int pkAesthetic = aesthetic.getAesthetic();

        List<Integer> pkWebsites = aesthetic.getWebsites().stream()
                .map(website -> aestheticWebsiteRepository.createOrUpdate(pkAesthetic,
                        website.getUrl(), website.getWebsiteType().getWebsiteType()))
                .collect(Collectors.toList());

        if (pkWebsites.isEmpty()) {
            aestheticWebsiteRepository.deleteByAesthetic(pkAesthetic);
        } else {
            aestheticWebsiteRepository.deleteByAestheticExcept(pkAesthetic, pkWebsites);
        }

        List<Integer> pkAestheticRelationships = aesthetic.getSimilarAesthetics().stream()
                .map(similarAesthetic -> aestheticRelationshipRepository.createOrUpdate(pkAesthetic,
                        similarAesthetic.getAesthetic(), similarAesthetic.getDescription(),
                        similarAesthetic.getReverseDescription()))
                .flatMap(List::stream).collect(Collectors.toList());

        if (pkAestheticRelationships.isEmpty()) {
            aestheticRelationshipRepository.deleteByAesthetic(pkAesthetic);
        } else {
            aestheticRelationshipRepository.deleteByAestheticExcept(pkAesthetic,
                    pkAestheticRelationships);
        }

        List<Integer> pkMedia = aesthetic.getMedia().stream().map(media -> {
            MediaCreator mediaCreator = media.getMediaCreator();
            Integer pkMediaCreator = mediaCreator.getMediaCreator();

            if (pkMediaCreator == null) {
                pkMediaCreator = mediaCreatorRepository.getOrCreate(mediaCreator.getName());
            }

            return aestheticMediaRepository.createOrUpdate(pkAesthetic, media.getUrl(),
                    media.getPreviewImageUrl(), media.getLabel(), media.getDescription(),
                    pkMediaCreator, media.getYear());
        }).collect(Collectors.toList());

        if (pkMedia.isEmpty()) {
            aestheticMediaRepository.deleteByAesthetic(pkAesthetic);
        } else {
            aestheticMediaRepository.deleteByAestheticExcept(pkAesthetic, pkMedia);
        }

        aestheticRepository.save(aesthetic);
        return EditResponse.success();
    }
}
