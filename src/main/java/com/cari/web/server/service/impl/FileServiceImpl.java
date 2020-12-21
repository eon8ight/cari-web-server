package com.cari.web.server.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.imageio.ImageIO;
import com.cari.web.server.domain.db.CariFile;
import com.cari.web.server.domain.db.FileType;
import com.cari.web.server.dto.FileOperationResult;
import com.cari.web.server.dto.response.S3PutResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.repository.FileRepository;
import com.cari.web.server.service.FileService;
import com.cari.web.server.service.S3Service;
import com.cari.web.server.util.FileUtils;
import com.cari.web.server.util.ImageProcessor;
import com.cari.web.server.util.ImageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private FileRepository fileRepository;

    @Override
    public FileOperationResult upload(File file) {
        S3PutResponse s3Res = s3Service.upload(file);

        if (s3Res.getStatus().equals(RequestStatus.FAILURE)) {
            return FileOperationResult.failure(s3Res.getMessage());
        }

        return FileOperationResult.success(s3Res.getKey());
    }

    @Override
    public CariFile save(String key, int pkFileType) {
        String url = s3Service.getUrlPrefix() + key;

        // @formatter:off
        CariFile dbFile = CariFile.builder()
            .fileType(pkFileType)
            .url(url)
            .build();
        // @formatter:on

        dbFile = fileRepository.save(dbFile);
        return dbFile;
    }

    @Override
    public File copyToTmpFile(MultipartFile multipartFile) throws IOException {
        return FileUtils.transferToTmp(multipartFile);
    }

    @Override
    public FileOperationResult processImage(File file, ImageProcessor processor,
            List<ImageValidator> validators) {
        try {
            File tmpFile = FileUtils.cloneFile(file);
            BufferedImage bufferedImage = ImageIO.read(tmpFile);

            for (Function<BufferedImage, Optional<String>> validator : validators) {
                Optional<String> errorMessage = validator.apply(bufferedImage);

                if (errorMessage.isPresent()) {
                    return FileOperationResult.failure(errorMessage.get());
                }
            }

            BufferedImage processedImage = processor.apply(bufferedImage);
            boolean wroteImage = ImageIO.write(processedImage, "png", tmpFile);

            if (wroteImage) {
                return FileOperationResult.success(tmpFile);
            }

            return FileOperationResult.failure("Failed to write processed image.");
        } catch (IOException ex) {
            return FileOperationResult.failure(ex.getLocalizedMessage());
        }
    }

    @Override
    public void delete(CariFile file) {
        String key = file.getUrl().replace(s3Service.getUrlPrefix(), "");
        s3Service.delete(key);
    }

    @Override
    public FileOperationResult processImageAndUploadAndSave(File file, ImageProcessor processor,
            List<ImageValidator> validators) {
        FileOperationResult processRes = processImage(file, processor, validators);

        if (processRes.getStatus().equals(RequestStatus.FAILURE)) {
            return processRes;
        }

        FileOperationResult uploadRes = upload(file);

        if (uploadRes.getStatus().equals(RequestStatus.FAILURE)) {
            return uploadRes;
        }

        CariFile dbFile = save(uploadRes.getS3Key().get(), FileType.FILE_TYPE_IMAGE);
        return FileOperationResult.success(dbFile);
    }
}
