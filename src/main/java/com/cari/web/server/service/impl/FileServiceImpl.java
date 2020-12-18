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
import com.cari.web.server.dto.FileUploadResult;
import com.cari.web.server.dto.response.S3PutResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.exception.FileProcessingException;
import com.cari.web.server.repository.FileRepository;
import com.cari.web.server.service.FileService;
import com.cari.web.server.service.S3Service;
import com.cari.web.server.util.FileUtils;
import com.cari.web.server.util.ImageProcessor;
import com.cari.web.server.util.ImageValidator;
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

    @Override
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

    @Override
    public final CariFile processAndSaveImage(MultipartFile multipartFile, ImageProcessor processor,
            List<ImageValidator> validators)
            throws FileProcessingException {
        try {
            File tmpImageFile = FileUtils.transferToTmp(multipartFile);
            BufferedImage bufferedImage = ImageIO.read(tmpImageFile);

            for (Function<BufferedImage, Optional<String>> validator : validators) {
                Optional<String> errorMessage = validator.apply(bufferedImage);

                if (errorMessage.isPresent()) {
                    throw new FileProcessingException(errorMessage.get());
                }
            }

            BufferedImage processedImage = processor.apply(bufferedImage);
            boolean wroteImage = ImageIO.write(processedImage, "png", tmpImageFile);

            if (wroteImage) {
                FileUploadResult uploadResult = upload(tmpImageFile, FileType.FILE_TYPE_IMAGE);

                if (uploadResult.getStatus().equals(RequestStatus.SUCCESS)) {
                    CariFile dbProfileImage = uploadResult.getFile();
                    return dbProfileImage;
                }

                throw new FileProcessingException(uploadResult.getMessage());
            }

            throw new FileProcessingException("Failed to write processed image.");
        } catch (IOException ex) {
            throw new FileProcessingException(ex.getLocalizedMessage());
        }
    }
}
