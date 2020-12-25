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

    File copyToTmpFile(MultipartFile multipartFile) throws IOException;

    FileOperationResult upload(File file);

    CariFile save(String key, int pkFileType);

    void delete(CariFile file);

    void delete(List<Integer> pkFiles);

    FileOperationResult validateImage(File file, List<ImageValidator> validators);

    FileOperationResult processImage(File file, ImageProcessor processor);

    /* COMPOSITE OPERATIONS */

    FileOperationResult uploadAndSave(File file, int pkFileType);

    FileOperationResult processImageAndUploadAndSave(File file, ImageProcessor processor);

    FileOperationResult validateAndProcessImageAndUploadAndSave(File file, ImageProcessor processor,
            List<ImageValidator> validators);
}
