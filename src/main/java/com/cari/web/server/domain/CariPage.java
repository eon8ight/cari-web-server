package com.cari.web.server.domain;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class CariPage<T> implements Page<T> {
    private List<T> data;

    private int pageNum;

    private int totalCount;

    private int maxPerPage;

    private int offset;

    private Sort sort;

    public CariPage(List<T> data, int pageNum, int totalCount, int maxPerPage, Sort sort) {
        this.data = data;
        this.pageNum = pageNum;
        this.totalCount = totalCount;
        this.maxPerPage = maxPerPage;
        this.sort = sort;
        this.offset = pageNum * maxPerPage;
    }

    @Override
    public int getNumber() {
        return pageNum;
    }

    @Override
    public int getSize() {
        return maxPerPage;
    }

    @Override
    public int getNumberOfElements() {
        return data.size();
    }

    @Override
    public List<T> getContent() {
        return data;
    }

    @Override
    public boolean hasContent() {
        return !data.isEmpty();
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public boolean isFirst() {
        return pageNum == 0;
    }

    @Override
    public boolean isLast() {
        return offset + data.size() <= totalCount;
    }

    @Override
    public boolean hasNext() {
        return offset + data.size() > totalCount;
    }

    @Override
    public boolean hasPrevious() {
        return pageNum > 0;
    }

    @Override
    public Pageable nextPageable() {
        return PageRequest.of(pageNum + 1, maxPerPage, sort);
    }

    @Override
    public Pageable previousPageable() {
        return PageRequest.of(pageNum - 1, maxPerPage, sort);
    }

    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    @Override
    public int getTotalPages() {
        return (int) Math.ceil(totalCount / maxPerPage);
    }

    @Override
    public long getTotalElements() {
        return totalCount;
    }

    @Override
    public <U> Page<U> map(Function<? super T, ? extends U> converter) {
        List<U> transformedData = data.stream().map(converter).collect(Collectors.toList());
        return new CariPage<U>(transformedData, pageNum, totalCount, maxPerPage, sort);
    }

    public static <T> CariPage<T> getPage(DataSource dbHandle, String query, SqlParameterSource params,
            int pageNum, int maxPerPage, Sort sort, RowMapper<T> rowMapper) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dbHandle);

        List<T> data = template.query(query, params, rowMapper);

        int totalCount =
                template.query(query, params, (ResultSetExtractor<Integer>) (resultSet -> {
                    if(resultSet.next()) {
                        return resultSet.getInt("count");
                    }

                    return 0;
                }));

        return new CariPage<T>(data, pageNum, totalCount, maxPerPage, sort);
    }
}
