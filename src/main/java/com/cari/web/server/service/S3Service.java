package com.cari.web.server.service;

import java.io.File;
import com.cari.web.server.dto.response.S3PutResponse;

public interface S3Service {

    S3PutResponse upload(File file);
}
