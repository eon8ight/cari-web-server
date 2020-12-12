package com.cari.web.server.service;

import com.cari.web.server.dto.response.S3PutResponse;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    S3PutResponse upload(MultipartFile file);
}
