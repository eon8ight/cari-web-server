package com.cari.web.server.service.impl;

import java.io.File;
import com.cari.web.server.domain.db.CariFile;
import com.cari.web.server.dto.FileUploadResult;
import com.cari.web.server.dto.response.S3PutResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.repository.FileRepository;
import com.cari.web.server.service.FileService;
import com.cari.web.server.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {

    @Value("${aws.s3.bucket.name}")
    private String s3BucketName;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private FileRepository fileRepository;

    public FileUploadResult upload(File file, int pkFileType) {
        S3PutResponse s3Res = s3Service.upload(file);
        String key = s3Res.getKey();

        if (s3Res.getStatus().equals(RequestStatus.FAILURE)) {
            // @formatter:off
            return FileUploadResult.builder()
                .status(RequestStatus.FAILURE)
                .message(s3Res.getMessage())
                .build();
            // @formatter:on
        }

        String url = new StringBuilder("https://").append(s3BucketName).append(".s3.amazonaws.com/")
                .append(key).toString();

        // @formatter:off
        CariFile dbFile = CariFile.builder()
            .fileType(pkFileType)
            .url(url)
            .build();
        // @formatter:on

        dbFile = fileRepository.save(dbFile);

        // @formatter:off
        return FileUploadResult.builder()
            .status(RequestStatus.SUCCESS)
            .file(dbFile)
            .build();
        // @formatter:on
    }
}
