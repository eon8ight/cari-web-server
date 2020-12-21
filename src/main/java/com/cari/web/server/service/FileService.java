package com.cari.web.server.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.cari.web.server.domain.db.CariFile;
import com.cari.web.server.dto.FileOperationResult;
import com.cari.web.server.util.ImageProcessor;
import com.cari.web.server.util.ImageValidator;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileOperationResult upload(File file);

    CariFile save(String key, int pkFileType);

    File copyToTmpFile(MultipartFile multipartFile) throws IOException;

    void delete(CariFile file);

    FileOperationResult processImage(File multipartFile, ImageProcessor processor,
            List<ImageValidator> validators);

    FileOperationResult processImageAndUploadAndSave(File multipartFile, ImageProcessor processor,
            List<ImageValidator> validators);
}
