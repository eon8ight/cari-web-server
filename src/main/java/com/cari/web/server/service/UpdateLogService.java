package com.cari.web.server.service;

import java.util.Optional;
import com.cari.web.server.domain.UpdateLogRollup;
import org.springframework.data.domain.Page;

public interface UpdateLogService {

    Page<UpdateLogRollup> findForList(Optional<Integer> limit, Optional<Integer> pageNumOptional);
}
