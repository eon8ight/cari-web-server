package com.cari.web.server.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.cari.web.server.domain.CariFieldError;
import com.cari.web.server.domain.db.AestheticMedia;
import com.cari.web.server.domain.db.CariFile;
import com.cari.web.server.domain.db.FileType;
import com.cari.web.server.dto.FileOperationResult;
import com.cari.web.server.dto.request.AestheticMediaEditData;
import com.cari.web.server.dto.request.AestheticMediaEditRequest;
import com.cari.web.server.dto.response.CariResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.repository.AestheticMediaRepository;
import com.cari.web.server.repository.MediaCreatorRepository;
import com.cari.web.server.service.AestheticMediaService;
import com.cari.web.server.service.FileService;
import com.cari.web.server.service.ImageService;
import com.cari.web.server.util.ImageProcessor;
import com.cari.web.server.util.ImageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AestheticMediaServiceImpl implements AestheticMediaService {

    private static final String FIELD_MEDIA = "media";

    private static final int MEDIA_FILE_SIZE_THUMBNAIL = 200;
    private static final int MEDIA_FILE_SIZE_PREVIEW = 500;

    private static final String MEDIA_IMAGE_SIZE_MESSAGE =
            new StringBuilder("Image must be at least ").append(MEDIA_FILE_SIZE_PREVIEW)
                    .append(" pixels by ").append(MEDIA_FILE_SIZE_PREVIEW).append(" pixels.")
                    .toString();

    @Autowired
    private FileService fileService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private AestheticMediaRepository aestheticMediaRepository;

    @Autowired
    private MediaCreatorRepository mediaCreatorRepository;

    @Override
    public CariResponse validateCreateOrUpdateMedia(
            AestheticMediaEditRequest aestheticMediaEditRequest) {
        List<AestheticMediaEditData> mediaObjects = aestheticMediaEditRequest.getMediaObjects();
        int mediaEditRequestsSize = mediaObjects.size();

        List<CariFieldError> fieldErrors = new ArrayList<>(mediaEditRequestsSize);

        ImageValidator minSizeValidator =
                bf -> imageService.isImageMinimumSize(bf, MEDIA_FILE_SIZE_PREVIEW)
                        ? Optional.empty()
                        : Optional.of(MEDIA_IMAGE_SIZE_MESSAGE);

        for (int i = 0; i < mediaEditRequestsSize; i++) {
            AestheticMediaEditData media = mediaObjects.get(i);
            MultipartFile fileObject = media.getFileObject();

            if (fileObject != null) {
                try {
                    File fileObjectTmp = fileService.copyToTmpFile(fileObject);
                    media.setCopiedFileObject(fileObjectTmp);

                    FileOperationResult validateRes = fileService.validateImage(fileObjectTmp,
                            Arrays.asList(minSizeValidator));

                    if (validateRes.getStatus().equals(RequestStatus.FAILURE)) {
                        fieldErrors
                                .add(new CariFieldError(FIELD_MEDIA, validateRes.getMessage(), i));
                    }
                } catch (IOException ex) {
                    return CariResponse.failure(ex.getLocalizedMessage());
                }
            }
        }

        if (!fieldErrors.isEmpty()) {
            return CariResponse.failure(fieldErrors);
        }

        return CariResponse.success();
    }

    @Override
    public CariResponse createOrUpdate(AestheticMediaEditRequest aestheticMediaEditRequest) {
        int pkAesthetic = aestheticMediaEditRequest.getAesthetic();

        List<AestheticMediaEditData> mediaObjects = aestheticMediaEditRequest.getMediaObjects();
        int mediaEditRequestsSize = mediaObjects.size();

        List<CariFile> filesToDelete;

        if (mediaObjects.isEmpty()) {
            filesToDelete = aestheticMediaRepository.findFilesByAesthetic(pkAesthetic);
            aestheticMediaRepository.deleteByAesthetic(pkAesthetic);
        } else {
            ImageProcessor cropAndResizeThumbnail = bf -> {
                BufferedImage cropped = imageService.cropToSquare(bf);
                return imageService.resizeImage(cropped, MEDIA_FILE_SIZE_THUMBNAIL);
            };

            List<AestheticMedia> payloadMediaObjects = new ArrayList<>(mediaEditRequestsSize);
            int mediaFile, mediaThumbnailFile, mediaPreviewFile;

            for (AestheticMediaEditData media : mediaObjects) {
                Integer pkMediaCreator = media.getMediaCreator();

                if (pkMediaCreator == null) {
                    pkMediaCreator =
                            mediaCreatorRepository.getOrCreate(media.getMediaCreatorName());
                }

                File tmpFile = media.getCopiedFileObject();

                if (tmpFile != null) {
                    FileOperationResult mediaFileRes =
                            fileService.uploadAndSave(tmpFile, FileType.FILE_TYPE_IMAGE);

                    if (mediaFileRes.getStatus().equals(RequestStatus.FAILURE)) {
                        return CariResponse.failure(mediaFileRes.getMessage());
                    }

                    FileOperationResult mediaThumbnailFileRes = fileService
                            .processImageAndUploadAndSave(tmpFile, cropAndResizeThumbnail);

                    if (mediaThumbnailFileRes.getStatus().equals(RequestStatus.FAILURE)) {
                        fileService.delete(mediaFileRes.getDbFile().get());
                        return CariResponse.failure(mediaThumbnailFileRes.getMessage());
                    }

                    FileOperationResult mediaPreviewFileRes =
                            fileService.processImageAndUploadAndSave(tmpFile,
                                    bf -> imageService.resizeImage(bf, MEDIA_FILE_SIZE_PREVIEW));

                    if (mediaPreviewFileRes.getStatus().equals(RequestStatus.FAILURE)) {
                        fileService.delete(mediaFileRes.getDbFile().get());
                        fileService.delete(mediaThumbnailFileRes.getDbFile().get());
                        return CariResponse.failure(mediaPreviewFileRes.getMessage());
                    }

                    mediaFile = mediaFileRes.getDbFile().get().getFile();
                    mediaThumbnailFile = mediaThumbnailFileRes.getDbFile().get().getFile();
                    mediaPreviewFile = mediaPreviewFileRes.getDbFile().get().getFile();
                } else {
                    mediaFile = media.getMediaFile();
                    mediaThumbnailFile = media.getMediaThumbnailFile();
                    mediaPreviewFile = media.getMediaPreviewFile();
                }

                // @formatter:off
                AestheticMedia aestheticMedia = AestheticMedia.builder()
                    .label(media.getLabel())
                    .description(media.getDescription())
                    .mediaCreator(pkMediaCreator)
                    .year(media.getYear())
                    .mediaFile(mediaFile)
                    .mediaThumbnailFile(mediaThumbnailFile)
                    .mediaPreviewFile(mediaPreviewFile)
                    .build();
                // @formatter:on

                payloadMediaObjects.add(aestheticMedia);
            }

            List<AestheticMedia> aestheticMedia = aestheticMediaRepository
                    .createOrUpdateForAesthetic(pkAesthetic, payloadMediaObjects);

            List<Integer> pkAestheticMedia = aestheticMedia.stream()
                    .map(AestheticMedia::getAestheticMedia).collect(Collectors.toList());

            filesToDelete = aestheticMediaRepository.findUnusedAestheticMediaFiles(pkAesthetic,
                    pkAestheticMedia);

            aestheticMediaRepository.deleteByAestheticExcept(pkAesthetic, pkAestheticMedia);
        }

        if (!filesToDelete.isEmpty()) {
            fileService.delete(filesToDelete);
        }

        CariResponse res = CariResponse.success();
        return res;
    }

    @Override
    public CariResponse validateAndCreateOrUpdate(
            AestheticMediaEditRequest aestheticMediaEditRequest) {
        CariResponse validateResponse = validateCreateOrUpdateMedia(aestheticMediaEditRequest);

        if (validateResponse.getStatus().equals(RequestStatus.FAILURE)) {
            return validateResponse;
        }

        return createOrUpdate(aestheticMediaEditRequest);
    }
}
