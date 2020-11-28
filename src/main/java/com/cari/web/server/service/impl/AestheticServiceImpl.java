package com.cari.web.server.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.domain.CariPage;
import com.cari.web.server.domain.MediaCreator;
import com.cari.web.server.dto.EditResponse;
import com.cari.web.server.repository.AestheticMediaRepository;
import com.cari.web.server.repository.AestheticRelationshipRepository;
import com.cari.web.server.repository.AestheticRepository;
import com.cari.web.server.repository.AestheticWebsiteRepository;
import com.cari.web.server.repository.MediaCreatorRepository;
import com.cari.web.server.service.AestheticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class AestheticServiceImpl implements AestheticService {

    private static final int MAX_PER_PAGE = 20;

    private static final String FILTER_PAGE = "page";
    private static final String FILTER_SORT_FIELD = "sortField";
    private static final String FILTER_ASC = "asc";
    private static final String FILTER_KEYWORD = "keyword";
    private static final String FILTER_START_YEAR = "startYear";
    private static final String FILTER_END_YEAR = "endYear";

    // @formatter:off
    private static final String SORT_BY_START_YEAR =
        "case " +
        "  when start_year ~ '^\\d+$'         then start_year::integer " +
        "  when lower(start_year) = 'present' then date_part('year', CURRENT_DATE)::integer " +
        "  else regexp_replace((regexp_split_to_array(start_year, '\\s+(?=\\S*$)'))[2], 's$', '')::integer " +
        "       + ( " +
        "           case lower((regexp_split_to_array(start_year, '\\s+(?=\\S*$)'))[1]) " +
        "             when 'very early' then 1 " +
        "             when 'early'      then 3 " +
        "             when 'mid'        then 5 " +
        "             when 'late'       then 7 " +
        "             when 'very late'  then 9 " +
        "           end " +
        "         ) " +
        "end ";

    private static final String SORT_BY_END_YEAR =
        "case " +
        "  when end_year ~ '^\\d+$'         then end_year::integer " +
        "  when lower(end_year) = 'present' then date_part('year', CURRENT_DATE)::integer " +
        "  else regexp_replace((regexp_split_to_array(end_year, '\\s+(?=\\S*$)'))[2], 's$', '')::integer " +
        "       + ( " +
        "           case lower((regexp_split_to_array(end_year, '\\s+(?=\\S*$)'))[1]) " +
        "             when 'very early' then 1 " +
        "             when 'early'      then 3 " +
        "             when 'mid'        then 5 " +
        "             when 'late'       then 7 " +
        "             when 'very late'  then 9 " +
        "           end " +
        "         ) " +
        "end ";
    // @formatter:on

    private static final Map<String, String> SORT_FIELDS =
            Map.of("name", "name", "startYear", SORT_BY_START_YEAR, "endYear", SORT_BY_END_YEAR);

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
        Optional<Integer> startYear = validateAndGetInt(filters, FILTER_START_YEAR);
        Optional<Integer> endYear = validateAndGetInt(filters, FILTER_END_YEAR);

        List<String> filterClauses = new ArrayList<String>();

        if (keyword != null) {
            filterClauses.add(
                    "name ilike '%' || :nameKeyword || '%' or description ilike '%' || :descriptionKeyword || '%'");

            params.addValue("nameKeyword", keyword);
            params.addValue("descriptionKeyword", keyword);
        }

        if (startYear.isPresent()) {
            filterClauses.add("start_year >= :startYear");
            params.addValue("startYear", startYear.get().intValue());
        }

        if (endYear.isPresent()) {
            filterClauses.add("coalesce(end_year, date_part('year', CURRENT_DATE)) <= :endYear");
            params.addValue("endYear", endYear.get().intValue());
        }

        if (!filterClauses.isEmpty()) {
            String filterString =
                    new StringBuffer("where ").append(filterClauses.stream().map(f -> "(" + f + ")")
                            .collect(Collectors.joining(" and "))).append(" ").toString();

            queryBuilder.append(filterString);
        }

        /* ORDER BY */

        Sort sort = validateAndGetSort(filters);

        if (sort.isSorted()) {
            String orderByParts = sort.toList().stream().map(sortOrder -> SORT_FIELDS
                    .get(sortOrder.getProperty())
                    + (sortOrder.getDirection().equals(Sort.Direction.ASC) ? " asc " : " desc "))
                    .collect(Collectors.joining(", "));

            queryBuilder.append("order by ").append(orderByParts);
        }

        /* LIMIT and OFFSET */

        Optional<Integer> pageNumOptional = validateAndGetIntNonNegative(filters, FILTER_PAGE);
        int pageNum = pageNumOptional.orElse(0);

        queryBuilder.append("limit :limit offset :offset");
        params.addValue("limit", MAX_PER_PAGE);
        params.addValue("offset", pageNum * MAX_PER_PAGE);

        return CariPage.getPage(dbHandle, queryBuilder.toString(), params, pageNum, MAX_PER_PAGE,
                sort, Aesthetic::fromResultSet);
    }

    @Override
    public Aesthetic findForPage(String urlSlug) {
        return aestheticRepository.findForPage(urlSlug);
    }

    @Override
    public Aesthetic findForEdit(int aesthetic) {
        return aestheticRepository.findForEdit(aesthetic);
    }

    @Override
    public Aesthetic find(int aesthetic) {
        return aestheticRepository.simpleFind(aesthetic);
    }

    @Override
    public List<Aesthetic> findNames(Optional<String> query) {
        return aestheticRepository.findNames(query.orElse(""));
    }

    @Override
    public EditResponse createOrUpdate(Aesthetic aesthetic) {
        int pkAesthetic = aesthetic.getAesthetic();

        // remove aesthetic websites not in payload

        aesthetic.getWebsites().forEach(website -> {
            aestheticWebsiteRepository.createOrUpdate(pkAesthetic, website.getUrl(),
                    website.getWebsiteType().getWebsiteType());
        });

        // remove relationships not in payload

        aesthetic.getSimilarAesthetics().forEach(similarAesthetic -> {
            int pkSimilarAesthetic = similarAesthetic.getAesthetic();

            aestheticRelationshipRepository.createOrUpdate(pkAesthetic, pkSimilarAesthetic,
                    similarAesthetic.getDescription(), similarAesthetic.getReverseDescription());
        });

        // remove media not in payload

        aesthetic.getMedia().forEach(media -> {
            MediaCreator mediaCreator = media.getMediaCreator();
            Integer pkMediaCreator = mediaCreator.getMediaCreator();

            if (pkMediaCreator == null) {
                pkMediaCreator = mediaCreatorRepository.getOrCreate(mediaCreator.getName());
            }

            aestheticMediaRepository.createOrUpdate(pkAesthetic, media.getUrl(),
                    media.getPreviewImageUrl(), media.getLabel(), media.getDescription(),
                    pkMediaCreator, media.getYear());
        });

        return EditResponse.success();
    }

    private Sort validateAndGetSort(Map<String, String> filters) {
        String sortField = filters.get(FILTER_SORT_FIELD);

        if (sortField == null) {
            return Sort.by(Sort.Order.asc("startYear"), Sort.Order.asc("endYear"));
        } else if (!SORT_FIELDS.containsKey(sortField)) {
            StringBuilder errorBuilder = new StringBuilder("`").append(FILTER_SORT_FIELD)
                    .append("` must be one of the following values: ").append(SORT_FIELDS.keySet()
                            .stream().map(f -> "\"" + f + "\"").collect(Collectors.joining(", ")))
                    .append(".");

            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errorBuilder.toString());
        }

        Boolean[] ascValues = new Boolean[] {Boolean.TRUE, Boolean.FALSE};
        String ascString = filters.getOrDefault(FILTER_ASC, "true");

        if (ascString != null && Arrays.stream(ascValues)
                .noneMatch(b -> ascString.equalsIgnoreCase(b.toString()))) {
            StringBuilder errorBuilder = new StringBuilder("`").append(FILTER_ASC)
                    .append("` must be one of the following values: ")
                    .append(Arrays.stream(ascValues).map(b -> "\"" + b.toString() + "\"")
                            .collect(Collectors.joining(", ")))
                    .append(".");

            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errorBuilder.toString());
        }

        boolean asc = Boolean.parseBoolean(ascString);
        return Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
    }

    private Optional<Integer> validateAndGetInt(Map<String, String> filters, String key) {
        String value = filters.get(key);

        if (!StringUtils.isEmpty(value)) {
            int intValue;

            try {
                intValue = Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                        "`" + key + "` must be an integer");
            }

            return Optional.of(intValue);
        }

        return Optional.empty();
    }

    private Optional<Integer> validateAndGetIntNonNegative(Map<String, String> filters,
            String key) {
        String value = filters.get(key);

        if (!StringUtils.isEmpty(value)) {
            int intValue;

            try {
                intValue = Integer.parseInt(value);

                if (intValue < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                        "`" + key + "` must be a non-negative integer");
            }

            return Optional.of(intValue);
        }

        return Optional.empty();
    }
}
