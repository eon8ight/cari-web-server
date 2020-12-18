package com.cari.web.server.service;

import java.io.File;
import java.util.List;
import com.cari.web.server.domain.db.CariFile;
import com.cari.web.server.dto.FileUploadResult;
import com.cari.web.server.exception.FileProcessingException;
import com.cari.web.server.util.ImageProcessor;
import com.cari.web.server.util.ImageValidator;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileUploadResult upload(File file, int pkFileType);

    CariFile processAndSaveImage(MultipartFile multipartFile, ImageProcessor processor,
            List<ImageValidator> validators) throws FileProcessingException;
}
