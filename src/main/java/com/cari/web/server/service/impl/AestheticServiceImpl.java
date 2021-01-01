package com.cari.web.server.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import com.cari.web.server.domain.CariFieldError;
import com.cari.web.server.domain.SimilarAesthetic;
import com.cari.web.server.domain.db.Aesthetic;
import com.cari.web.server.domain.db.AestheticMedia;
import com.cari.web.server.domain.db.AestheticRelationship;
import com.cari.web.server.domain.db.AestheticWebsite;
import com.cari.web.server.domain.db.CariFile;
import com.cari.web.server.domain.db.FileType;
import com.cari.web.server.dto.FileOperationResult;
import com.cari.web.server.dto.request.AestheticEditRequest;
import com.cari.web.server.dto.request.AestheticMediaEditRequest;
import com.cari.web.server.dto.response.CariPage;
import com.cari.web.server.dto.response.CariResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.repository.AestheticMediaRepository;
import com.cari.web.server.repository.AestheticRelationshipRepository;
import com.cari.web.server.repository.AestheticRepository;
import com.cari.web.server.repository.AestheticWebsiteRepository;
import com.cari.web.server.repository.MediaCreatorRepository;
import com.cari.web.server.service.AestheticService;
import com.cari.web.server.service.FileService;
import com.cari.web.server.service.ImageService;
import com.cari.web.server.util.ImageProcessor;
import com.cari.web.server.util.ImageValidator;
import com.cari.web.server.util.db.QueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AestheticServiceImpl implements AestheticService {

    private static final int MAX_PER_PAGE = 20;

    private static final String FILTER_KEYWORD = "keyword";
    private static final String FILTER_START_YEAR = "startYear";
    private static final String FILTER_END_YEAR = "endYear";

    private static final String FIELD_MEDIA = "media";

    private static final int MEDIA_FILE_SIZE_THUMBNAIL = 200;
    private static final int MEDIA_FILE_SIZE_PREVIEW = 500;

    // @formatter:off
    private static final Map<String, String> SORT_FIELDS = Map.of(
        "name", "a.name",
        FILTER_START_YEAR, "fn_get_approximate_start_year(a.aesthetic)",
        FILTER_END_YEAR, "fn_get_approximate_end_year(a.aesthetic)"
    );
    // @formatter:on

    private static final String MEDIA_IMAGE_SIZE_MESSAGE =
            new StringBuilder("Image must be at least ").append(MEDIA_FILE_SIZE_PREVIEW)
                    .append(" pixels by ").append(MEDIA_FILE_SIZE_PREVIEW).append(" pixels.")
                    .toString();

    @Autowired
    private AestheticRepository aestheticRepository;

    @Autowired
    private AestheticWebsiteRepository aestheticWebsiteRepository;

    @Autowired
    private AestheticMediaRepository aestheticMediaRepository;

    @Autowired
    private AestheticRelationshipRepository aestheticRelationshipRepository;

    @Autowired
    private MediaCreatorRepository mediaCreatorRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private DataSource dbHandle;

    @Override
    public Page<Aesthetic> findForList(Map<String, String> filters) {
        String[] columns = new String[] {"count(a.*) over ()", "a.*",
                "ess.label || ' ' || es.year || 's' as start_year",
                "ees.label || ' ' || ee.year || 's' as end_year"};

        StringBuilder queryBuilder = new StringBuilder("select ")
                .append(Arrays.stream(columns).collect(Collectors.joining(", ")))
                .append(" from tb_aesthetic a ")
                .append("left join tb_era es on a.start_era = es.era ")
                .append("left join tb_era_specifier ess on es.era_specifier = ess.era_specifier ")
                .append("left join tb_era ee on a.end_era = ee.era ")
                .append("left join tb_era_specifier ees on ee.era_specifier = ees.era_specifier");

        MapSqlParameterSource params = new MapSqlParameterSource();

        /* WHERE */

        String keyword = filters.get(FILTER_KEYWORD);
        Optional<Integer> startYear = QueryUtils.validateAndGetInt(filters, FILTER_START_YEAR);
        Optional<Integer> endYear = QueryUtils.validateAndGetInt(filters, FILTER_END_YEAR);

        List<String> filterClauses = new ArrayList<String>();

        if (keyword != null) {
            filterClauses.add(
                    "a.name ilike '%' || :keyword || '%' or a.description ilike '%' || :keyword || '%'");

            params.addValue("keyword", keyword);
        }

        if (startYear.isPresent()) {
            filterClauses.add("abs(fn_get_approximate_start_year(a.aesthetic) - :startYear) <= 3");
            params.addValue("startYear", startYear.get().intValue());
        }

        if (endYear.isPresent()) {
            filterClauses.add("abs(fn_get_approximate_end_year(a.aesthetic) - :endYear) <= 3");
            params.addValue("endYear", endYear.get().intValue());
        }

        queryBuilder.append(QueryUtils.toWhereClause(filterClauses));

        /* ORDER BY */

        Sort sort = QueryUtils.validateAndGetSort(filters, SORT_FIELDS,
                () -> Sort.by(Sort.Order.asc("name")));

        queryBuilder.append(QueryUtils.toOrderByClause(sort, SORT_FIELDS));

        /* LIMIT and OFFSET */

        Optional<Integer> pageNumOptional =
                QueryUtils.validateAndGetIntNonNegative(filters, QueryUtils.FILTER_PAGE);

        int pageNum = pageNumOptional.orElse(0);

        queryBuilder.append("limit :limit offset :offset");
        params.addValue("limit", MAX_PER_PAGE);
        params.addValue("offset", pageNum * MAX_PER_PAGE);

        return CariPage.getPage(dbHandle, queryBuilder.toString(), params, pageNum, MAX_PER_PAGE,
                sort, Aesthetic::fromResultSet);
    }

    @Override
    public Optional<Aesthetic> findForPage(String urlSlug) {
        return aestheticRepository.findForPage(urlSlug);
    }

    @Override
    public Optional<Aesthetic> findForEdit(int aesthetic) {
        return aestheticRepository.findForEdit(aesthetic);
    }

    @Override
    public Optional<Aesthetic> find(int aesthetic) {
        return aestheticRepository.findById(aesthetic);
    }

    @Override
    public List<Aesthetic> findNames(Optional<String> query) {
        return aestheticRepository.findNames(query.orElse(""));
    }

    @Override
    @Transactional
    public CariResponse createOrUpdate(AestheticEditRequest aestheticEditRequest) {
        Optional<Integer> pkAestheticOptional =
                Optional.ofNullable(aestheticEditRequest.getAesthetic());

        String name = aestheticEditRequest.getName();
        String symbol = aestheticEditRequest.getSymbol();
        String urlSlug = name.toLowerCase().strip().replaceAll("[^a-zA-Z0-9-\\s]", "")
                .replaceAll("\\s+", "-");

        List<AestheticMediaEditRequest> mediaObjects = aestheticEditRequest.getMediaObjects();
        int mediaEditRequestsSize = mediaObjects.size();

        List<CariFieldError> fieldErrors = new ArrayList<>(2 + mediaEditRequestsSize);

        /* Validate name and URL slug */

        Optional<Aesthetic> existingAesthetic =
                aestheticRepository.findByNameOrUrlSlug(name, urlSlug);

        if (existingAesthetic.isPresent() && (pkAestheticOptional.isEmpty()
                || existingAesthetic.get().getAesthetic() != pkAestheticOptional.get())) {
            fieldErrors.add(new CariFieldError("name", "Name is already in use."));
        }

        /* Validate symbol */

        if (symbol != null) {
            existingAesthetic = aestheticRepository.findBySymbol(symbol);

            if (existingAesthetic.isPresent() && (pkAestheticOptional.isEmpty()
                    || existingAesthetic.get().getAesthetic() != pkAestheticOptional.get())) {
                fieldErrors.add(new CariFieldError("symbol", "Symbol is already in use."));
            }
        }

        /* Validate media */

        ImageValidator minSizeValidator =
                bf -> imageService.isImageMinimumSize(bf, MEDIA_FILE_SIZE_PREVIEW)
                        ? Optional.empty()
                        : Optional.of(MEDIA_IMAGE_SIZE_MESSAGE);

        for (int i = 0; i < mediaEditRequestsSize; i++) {
            AestheticMediaEditRequest media = mediaObjects.get(i);
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

        /* Create or update tb_aesthetic */

        // @formatter:off
        Aesthetic.AestheticBuilder aestheticBuilder = Aesthetic.builder()
            .name(name)
            .urlSlug(urlSlug)
            .symbol(symbol)
            .startEra(aestheticEditRequest.getStartEra())
            .endEra(aestheticEditRequest.getEndEra())
            .description(aestheticEditRequest.getDescription())
            .mediaSourceUrl(aestheticEditRequest.getMediaSourceUrl());
        // @formatter:on

        if (pkAestheticOptional.isPresent()) {
            aestheticBuilder.aesthetic(pkAestheticOptional.get());
        }

        Aesthetic aesthetic = aestheticRepository.createOrUpdate(aestheticBuilder.build());
        int pkAesthetic = aesthetic.getAesthetic();

        /* Create or update tb_aesthetic_media */

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

            for (AestheticMediaEditRequest media : mediaObjects) {
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

        /* Create or update tb_aesthetic_website */

        List<AestheticWebsite> websiteObjects = aestheticEditRequest.getWebsiteObjects();

        if (websiteObjects.isEmpty()) {
            aestheticWebsiteRepository.deleteByAesthetic(pkAesthetic);
        } else {
            List<AestheticWebsite> websites = aestheticWebsiteRepository
                    .createOrUpdateForAesthetic(pkAesthetic, websiteObjects);

            aestheticWebsiteRepository.deleteByAestheticExcept(pkAesthetic, websites.stream()
                    .map(AestheticWebsite::getAestheticWebsite).collect(Collectors.toList()));
        }

        /* Create or update tb_aesthetic_relationship */

        List<SimilarAesthetic> similarAestheticObjects =
                aestheticEditRequest.getSimilarAestheticObjects();

        if (similarAestheticObjects.isEmpty()) {
            aestheticRelationshipRepository.deleteByAesthetic(pkAesthetic);
        } else {
            List<AestheticRelationship> payloadAestheticRelationships =
                    similarAestheticObjects
                            .stream().flatMap(similarAesthetic -> similarAesthetic
                                    .toAestheticRelationships().stream())
                            .collect(Collectors.toList());

            List<AestheticRelationship> aestheticRelationships = aestheticRelationshipRepository
                    .createOrUpdateForAesthetic(pkAesthetic, payloadAestheticRelationships);

            aestheticRelationshipRepository.deleteByAestheticExcept(pkAesthetic,
                    aestheticRelationships.stream()
                            .map(AestheticRelationship::getAestheticRelationship)
                            .collect(Collectors.toList()));
        }

        CariResponse res = CariResponse.success();
        res.setUpdatedData(Map.of("urlSlug", urlSlug));
        return res;
    }
}
