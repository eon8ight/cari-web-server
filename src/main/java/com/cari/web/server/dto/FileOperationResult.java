package com.cari.web.server.dto;

import java.io.File;
import java.util.Optional;
import com.cari.web.server.domain.db.CariFile;
import com.cari.web.server.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileOperationResult {

    private RequestStatus status;

    private String message;

    @Builder.Default
    private Optional<File> file = Optional.empty();

    @Builder.Default
    private Optional<String> s3Key = Optional.empty();

    @Builder.Default
    private Optional<CariFile> dbFile = Optional.empty();

    public static FileOperationResult success(File file) {
        // @formatter:off
        return FileOperationResult.builder()
            .status(RequestStatus.SUCCESS)
            .file(Optional.of(file))
            .build();
        // @formatter:on
    }

    public static FileOperationResult success(CariFile dbFile) {
        // @formatter:off
        return FileOperationResult.builder()
            .status(RequestStatus.SUCCESS)
            .dbFile(Optional.of(dbFile))
            .build();
        // @formatter:on
    }

    public static FileOperationResult success(String s3Key) {
        // @formatter:off
        return FileOperationResult.builder()
            .status(RequestStatus.SUCCESS)
            .s3Key(Optional.of(s3Key))
            .build();
        // @formatter:on
    }

    public static FileOperationResult failure(String message) {
        // @formatter:off
        return FileOperationResult.builder()
            .status(RequestStatus.FAILURE)
            .message(message)
            .build();
        // @formatter:on
    }
}
