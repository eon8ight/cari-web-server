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
import com.cari.web.server.domain.Media;
import com.cari.web.server.domain.Website;
import com.cari.web.server.repository.AestheticRepository;
import com.cari.web.server.service.IAestheticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class AestheticService implements IAestheticService {

    private static final int MAX_PER_PAGE = 20;

    private static final String FILTER_PAGE = "page";
    private static final String FILTER_SORT_FIELD = "sortField";
    private static final String FILTER_ASC = "asc";
    private static final String FILTER_KEYWORD = "keyword";
    private static final String FILTER_START_YEAR = "startYear";
    private static final String FILTER_END_YEAR = "endYear";

    private static final Map<String, String> SORT_FIELDS =
            Map.of("name", "name", "startYear", "start_year", "endYear", "end_year");

    @Autowired
    private AestheticRepository repository;

    @Autowired
    private DataSource dbHandle;

    @Override
    public Page<Aesthetic> findAll(Map<String, String> filters) {
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
    public Aesthetic findByUrlSlug(String urlSlug) {
        Aesthetic aesthetic = repository.findByUrlSlug(urlSlug);

        List<Media> media = aesthetic.getMedia();
        List<Website> websites = aesthetic.getWebsites();
        List<Aesthetic> similarAesthetics = aesthetic.getSimilarAesthetics();

        if (media != null && media.size() == 1 && media.get(0) == null) {
            aesthetic.setMedia(null);
        }

        if (websites != null && websites.size() == 1 && websites.get(0) == null) {
            aesthetic.setWebsites(null);
        }

        if (similarAesthetics != null && similarAesthetics.size() == 1
                && similarAesthetics.get(0) == null) {
            aesthetic.setSimilarAesthetics(null);
        }

        return aesthetic;
    }

    @Override
    public Aesthetic find(int aesthetic) {
        return repository.findById(aesthetic).orElse(null);
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
