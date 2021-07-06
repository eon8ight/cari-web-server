package com.cari.web.server.controller;

import java.util.Optional;
import com.cari.web.server.domain.UpdateLogRollup;
import com.cari.web.server.service.UpdateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UpdateLogController {

    @Autowired
    private UpdateLogService updateLogService;

    @GetMapping("/updates")
    public Page<UpdateLogRollup> findForList(@RequestParam Optional<Integer> limit,
            @RequestParam Optional<Integer> offset) {
        return updateLogService.findForList(limit, offset);
    }
}
