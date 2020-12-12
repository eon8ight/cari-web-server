package com.cari.web.server.service;

import com.cari.web.server.domain.db.File;
import com.cari.web.server.exception.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    File upload(MultipartFile file, int pkFileType) throws FileUploadException;
}
