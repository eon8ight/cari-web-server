package com.cari.web.server.service.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import com.cari.web.server.domain.Aesthetic;
import com.cari.web.server.domain.CariPage;
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
            Map.of("name", "name", "startYear", "start_year");

    @Autowired
    private AestheticRepository repository;

    @Autowired
    private DataSource dbHandle;

    @Override
    public Page<Aesthetic> findAll(Map<String, String> filters) {
        StringBuilder queryBuilder = new StringBuilder("select * from tb_aesthetic where true ");
        StringBuilder countQueryBuilder =
                new StringBuilder("select count(*) from tb_aesthetic where true ");

        MapSqlParameterSource params = new MapSqlParameterSource();

        String keyword = filters.get(FILTER_KEYWORD);
        String startYearString = filters.get(FILTER_START_YEAR);
        String endYearString = filters.get(FILTER_END_YEAR);

        if (keyword != null) {
            String filter =
                    "and (name ilike '%' || :nameKeyword || '%' or description ilike '%' || :descriptionKeyword || '%') ";

            queryBuilder.append(filter);
            countQueryBuilder.append(filter);

            params.addValue("nameKeyword", keyword);
            params.addValue("descriptionKeyword", keyword);
        }

        if (!StringUtils.isEmpty(startYearString)) {
            int startYear = Integer.parseInt(startYearString);
            String filter = "and start_year >= :startYear ";

            queryBuilder.append(filter);
            countQueryBuilder.append(filter);

            params.addValue("startYear", startYear);
        }

        if (!StringUtils.isEmpty(endYearString)) {
            int endYear = Integer.parseInt(endYearString);
            String filter = "and coalesce(end_year, date_part('year', CURRENT_DATE)) <= :endYear ";

            queryBuilder.append(filter);
            countQueryBuilder.append(filter);

            params.addValue("endYear", endYear);
        }

        Sort sort = validateAndGetSort(filters);

        if (sort.isSorted()) {
            String sortField = filters.get(FILTER_SORT_FIELD);
            Sort.Order sortOrder = sort.getOrderFor(sortField);

            queryBuilder.append("order by ").append(SORT_FIELDS.get(sortField)).append(
                    sortOrder.getDirection().equals(Sort.Direction.ASC) ? " asc " : " desc ");
        }

        int pageNum;

        try {
            pageNum = Integer.parseInt(filters.getOrDefault(FILTER_PAGE, "0"));
        } catch (NumberFormatException ex) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    "`" + FILTER_PAGE + "` must be a non-negative number.");
        }

        queryBuilder.append("limit :limit offset :offset");
        params.addValue("limit", MAX_PER_PAGE);
        params.addValue("offset", pageNum * MAX_PER_PAGE);

        return CariPage.getPage(dbHandle, queryBuilder.toString(), countQueryBuilder.toString(),
                params, pageNum, MAX_PER_PAGE, sort, Aesthetic::fromResultSet);
    }

    @Override
    public Aesthetic findByUrlSlug(String urlSlug) {
        return repository.findByUrlSlug(urlSlug);
    }

    private Sort validateAndGetSort(Map<String, String> filters) {
        String sortField = filters.get(FILTER_SORT_FIELD);

        if (sortField == null) {
            return Sort.unsorted();
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
}
