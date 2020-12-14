package com.cari.web.server.dto;

import com.cari.web.server.domain.db.CariFile;
import com.cari.web.server.enums.RequestStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResult {

    private RequestStatus status;

    private String message;

    private CariFile file;
}
