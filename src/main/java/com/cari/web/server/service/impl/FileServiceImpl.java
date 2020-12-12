package com.cari.web.server.service.impl;

import com.cari.web.server.domain.db.File;
import com.cari.web.server.dto.response.S3PutResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.exception.FileUploadException;
import com.cari.web.server.repository.FileRepository;
import com.cari.web.server.service.FileService;
import com.cari.web.server.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

    @Value("${aws.s3.bucket.name}")
    private String s3BucketName;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private FileRepository fileRepository;

    public File upload(MultipartFile file, int pkFileType) throws FileUploadException {
        S3PutResponse s3Res = s3Service.upload(file);
        String key = s3Res.getKey();

        if (s3Res.getStatus().equals(RequestStatus.FAILURE)) {
            throw new FileUploadException(s3Res.getMessage(), key);
        }

        String url = new StringBuilder("https://").append(s3BucketName).append(".s3.amazonaws.com/")
                .append(key).toString();

        // @formatter:off
        File dbFile = File.builder()
            .fileType(pkFileType)
            .url(url)
            .build();
        // @formatter:on

        return fileRepository.save(dbFile);
    }
}
