package com.cari.web.server.service.impl;

import java.util.Optional;
import javax.sql.DataSource;
import com.cari.web.server.domain.UpdateLogRollup;
import com.cari.web.server.dto.response.CariPage;
import com.cari.web.server.service.UpdateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

@Service
public class UpdateLogServiceImpl implements UpdateLogService {

    private static final int MAX_DAYS_PER_PAGE = 20;

    private static final String FIND_FOR_LIST_QUERY = """
            select count(*) over () as count,
                   *
              from fn_get_update_log_entries( :offset, :limit )""";

    @Autowired
    private DataSource dbHandle;

    @Override
    public Page<UpdateLogRollup> findForList(Optional<Integer> limit,
            Optional<Integer> pageNumOptional) {
        int pageNum = pageNumOptional.orElse(0);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("limit", limit.orElse(MAX_DAYS_PER_PAGE));
        params.addValue("offset", pageNum * MAX_DAYS_PER_PAGE);

        Sort sort = Sort.by(Sort.Order.asc("created"));

        return CariPage.getPage(dbHandle, FIND_FOR_LIST_QUERY, params, pageNum, MAX_DAYS_PER_PAGE,
                sort, UpdateLogRollup::fromResultSet);
    }
}
