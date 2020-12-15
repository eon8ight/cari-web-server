package com.cari.web.server.util.db;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

public final class QueryUtils {

    public static final String FILTER_PAGE = "page";
    public static final String FILTER_SORT_FIELD = "sortField";
    public static final String FILTER_ASC = "asc";

    public static final String WHERE = " where ";
    public static final String AND = " and ";

    public static final String ORDER_BY = " order by ";
    public static final String ORDER_BY_ASC = " asc ";
    public static final String ORDER_BY_DESC = " desc ";

    public static final String NULLS_FIRST = " nulls first ";
    public static final String NULLS_LAST = " nulls last ";

    private static final String[] SORT_DIRECTION_VALUES =
            new String[] {Boolean.TRUE.toString(), Boolean.FALSE.toString()};

    public static Sort validateAndGetSort(Map<String, String> filters,
            Map<String, String> sortFields, Supplier<Sort> defaultSort) {
        String sortFieldString = filters.get(QueryUtils.FILTER_SORT_FIELD);

        if (sortFieldString == null) {
            return defaultSort.get();
        }

        String[] sortFieldsArray = sortFieldString.split(",");

        boolean allFieldsValid =
                Arrays.stream(sortFieldsArray).allMatch(field -> sortFields.containsKey(field));

        if (!allFieldsValid) {
            StringBuilder errorBuilder = new StringBuilder("`").append(FILTER_SORT_FIELD)
                    .append("` can only be a subset of the following values: ")
                    .append(sortFields.keySet().stream().map(f -> "\"" + f + "\"")
                            .collect(Collectors.joining(", ")))
                    .append(".");

            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errorBuilder.toString());
        }

        String ascString = filters.getOrDefault(FILTER_ASC, Boolean.TRUE.toString());

        if (Arrays.stream(SORT_DIRECTION_VALUES).noneMatch(b -> ascString.equalsIgnoreCase(b))) {
            StringBuilder errorBuilder = new StringBuilder("`").append(FILTER_ASC)
                    .append("` must be one of the following values: ")
                    .append(Arrays.stream(SORT_DIRECTION_VALUES).map(b -> "\"" + b + "\"")
                            .collect(Collectors.joining(", ")))
                    .append(".");

            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, errorBuilder.toString());
        }

        boolean asc = Boolean.parseBoolean(ascString);
        return Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, sortFieldsArray);
    }

    public static Optional<Integer> validateAndGetInt(Map<String, String> filters, String key) {
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

    public static Optional<Integer> validateAndGetIntNonNegative(Map<String, String> filters,
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

    public static String toWhereClause(List<String> filterClauses) {
        return filterClauses
                .isEmpty()
                        ? ""
                        : new StringBuffer(WHERE).append(filterClauses.stream()
                                .map(f -> " ( " + f + " ) ").collect(Collectors.joining(AND)))
                                .toString();
    }

    public static String toOrderByClause(Sort sort, Map<String, String> sortFields) {
        if (!sort.isSorted()) {
            return "";
        }

        return new StringBuilder(ORDER_BY)
                .append(sort.toList().stream()
                        .map(sortOrder -> sortFields.get(sortOrder.getProperty())
                                + (sortOrder.getDirection().equals(Sort.Direction.ASC)
                                        ? ORDER_BY_ASC
                                        : ORDER_BY_DESC)
                                + (sortOrder.getNullHandling().equals(Sort.NullHandling.NULLS_FIRST)
                                        ? NULLS_FIRST
                                        : NULLS_LAST))
                        .collect(Collectors.joining(" , ")))
                .toString();
    }
}
