package com.cari.web.server.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import com.cari.web.server.domain.CariFieldError;
import com.cari.web.server.domain.SimilarAesthetic;
import com.cari.web.server.domain.db.Aesthetic;
import com.cari.web.server.domain.db.AestheticRelationship;
import com.cari.web.server.domain.db.AestheticWebsite;
import com.cari.web.server.domain.db.CariFile;
import com.cari.web.server.domain.db.EventType;
import com.cari.web.server.domain.db.TableName;
import com.cari.web.server.domain.db.UpdatableField;
import com.cari.web.server.domain.db.UpdateLog;
import com.cari.web.server.domain.db.UpdateLogEntry;
import com.cari.web.server.dto.DatabaseUpsertResult;
import com.cari.web.server.dto.FileOperationResult;
import com.cari.web.server.dto.request.AestheticEditRequest;
import com.cari.web.server.dto.request.AestheticMediaEditRequest;
import com.cari.web.server.dto.response.CariPage;
import com.cari.web.server.dto.response.CariResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.repository.AestheticRelationshipRepository;
import com.cari.web.server.repository.AestheticRepository;
import com.cari.web.server.repository.AestheticWebsiteRepository;
import com.cari.web.server.repository.UpdateLogEntryRepository;
import com.cari.web.server.repository.UpdateLogRepository;
import com.cari.web.server.service.AestheticMediaService;
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
    private static final int DISPLAY_IMAGE_SIZE = 200;

    private static final String FILTER_KEYWORD = "keyword";
    private static final String FILTER_START_YEAR = "startYear";
    private static final String FILTER_END_YEAR = "endYear";

    private static final String DISPLAY_IMAGE_SIZE_MESSAGE =
            new StringBuilder("Image must be at least ").append(DISPLAY_IMAGE_SIZE)
                    .append(" pixels by ").append(DISPLAY_IMAGE_SIZE).append(" pixels.").toString();

    // @formatter:off
    private static final Map<String, String> SORT_FIELDS = Map.of(
        "name", "a.name",
        FILTER_START_YEAR, "fn_get_approximate_start_year(a.aesthetic)",
        FILTER_END_YEAR, "fn_get_approximate_end_year(a.aesthetic)"
    );
    // @formatter:on

    @Autowired
    private AestheticRepository aestheticRepository;

    @Autowired
    private AestheticWebsiteRepository aestheticWebsiteRepository;

    @Autowired
    private AestheticRelationshipRepository aestheticRelationshipRepository;

    @Autowired
    private UpdateLogRepository updateLogRepository;

    @Autowired
    private UpdateLogEntryRepository updateLogEntryRepository;

    @Autowired
    private AestheticMediaService aestheticMediaService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private DataSource dbHandle;

    @Override
    public Page<Aesthetic> findForList(Map<String, String> filters) {
        String[] columns = new String[] {"count(a.*) over ()", "a.*", "f.url as display_image_url",
                "ess.label || ' ' || es.year || 's' as start_year",
                "ees.label || ' ' || ee.year || 's' as end_year"};

        StringBuilder queryBuilder = new StringBuilder("select ")
                .append(Arrays.stream(columns).collect(Collectors.joining(", ")))
                .append(" from tb_aesthetic a ")
                .append("left join tb_file f on a.display_image_file = f.file ")
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
        filterClauses.add("(a.is_draft is null or a.is_draft is false)");

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
    public List<Aesthetic> findDraft() {
        return aestheticRepository.findDraft();
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

        List<CariFieldError> fieldErrors = new ArrayList<>(2);

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

        /* Validate display image */

        MultipartFile displayImage = aestheticEditRequest.getDisplayImage();
        Optional<CariFile> displayImageFile = Optional.empty();

        if (displayImage != null) {
            ImageProcessor cropAndResize = bf -> {
                BufferedImage cropped = imageService.cropToSquare(bf);
                return imageService.resizeImage(cropped, DISPLAY_IMAGE_SIZE);
            };

            ImageValidator minSizeValidator =
                    bf -> imageService.isImageMinimumSize(bf, DISPLAY_IMAGE_SIZE) ? Optional.empty()
                            : Optional.of(DISPLAY_IMAGE_SIZE_MESSAGE);

            try {
                File fileObjectTmp = fileService.copyToTmpFile(displayImage);

                FileOperationResult uploadResult =
                        fileService.validateAndProcessImageAndUploadAndSave(fileObjectTmp,
                                cropAndResize, Arrays.asList(minSizeValidator));

                if (uploadResult.getStatus().equals(RequestStatus.FAILURE)) {
                    fieldErrors.add(new CariFieldError("displayImage", uploadResult.getMessage()));
                } else {
                    displayImageFile = uploadResult.getDbFile();
                }
            } catch (IOException ex) {
                return CariResponse.failure(ex.getLocalizedMessage());
            }
        }

        /* Validate media */
        AestheticMediaEditRequest aestheticMediaEditRequest =
                aestheticEditRequest.getMediaObjects();

        CariResponse mediaValidateResponse =
                aestheticMediaService.validateCreateOrUpdateMedia(aestheticMediaEditRequest);

        if (mediaValidateResponse.getStatus().equals(RequestStatus.FAILURE)) {
            fieldErrors.addAll(mediaValidateResponse.getFieldErrors());
        }

        /* End validation */

        if (!fieldErrors.isEmpty()) {
            CariResponse failureResponse = CariResponse.failure(fieldErrors);
            failureResponse.setMessage(mediaValidateResponse.getMessage());
            return failureResponse;
        }

        /* Create or update tb_aesthetic */

        Integer startEra = aestheticEditRequest.getStartEra();
        Integer endEra = aestheticEditRequest.getEndEra();
        String description = aestheticEditRequest.getDescription();
        String mediaSourceUrl = aestheticEditRequest.getMediaSourceUrl();

        Integer pkDisplayImageFile = displayImageFile.isPresent() ? displayImageFile.get().getFile()
                : aestheticEditRequest.getDisplayImageFile();

        Aesthetic.AestheticBuilder aestheticBuilder = Aesthetic.builder().name(name)
                .urlSlug(urlSlug).symbol(symbol).startEra(startEra).endEra(endEra)
                .description(description).mediaSourceUrl(mediaSourceUrl)
                .displayImageFile(pkDisplayImageFile).isDraft(aestheticEditRequest.getIsDraft());

        UpdateLog updateLog = UpdateLog.builder().tableName(TableName.TB_AESTHETIC).build();
        List<UpdateLogEntry> updateLogEntries = new LinkedList<>();

        if (pkAestheticOptional.isPresent()) {
            updateLog.setEventType(EventType.UPDATED);
            aestheticBuilder.aesthetic(pkAestheticOptional.get());

            Aesthetic existingAestheticObj =
                    aestheticRepository.findById(pkAestheticOptional.get()).get();

            String oldName = existingAestheticObj.getName();
            Integer oldStartEra = existingAestheticObj.getStartEra();
            Integer oldEndEra = existingAestheticObj.getEndEra();
            String oldDescription = existingAestheticObj.getDescription();
            String oldMediaSourceUrl = existingAestheticObj.getMediaSourceUrl();
            Integer oldDisplayImageFile = existingAestheticObj.getDisplayImageFile();

            if (!oldName.equals(name)) {
                UpdateLogEntry nameUpdateLogEntry =
                        UpdateLogEntry.builder().updatableField(UpdatableField.AESTHETIC_NAME)
                                .oldValue(oldName).newValue(name).build();

                updateLogEntries.add(nameUpdateLogEntry);
            }

            if (!Objects.equals(oldStartEra, startEra)) {
                updateLogEntries.add(UpdateLogEntry.builder()
                        .updatableField(UpdatableField.AESTHETIC_START_ERA).build());
            }

            if (!Objects.equals(oldEndEra, endEra)) {
                updateLogEntries.add(UpdateLogEntry.builder()
                        .updatableField(UpdatableField.AESTHETIC_END_ERA).build());
            }

            if (!oldDescription.equals(description)) {
                updateLogEntries.add(UpdateLogEntry.builder()
                        .updatableField(UpdatableField.AESTHETIC_DESCRIPTION).build());
            }

            if (!Objects.equals(oldMediaSourceUrl, mediaSourceUrl)) {
                updateLogEntries.add(UpdateLogEntry.builder()
                        .updatableField(UpdatableField.AESTHETIC_ARENA_GALLERY_URL).build());
            }

            if (!Objects.equals(oldDisplayImageFile, pkDisplayImageFile)) {
                updateLogEntries.add(UpdateLogEntry.builder()
                        .updatableField(UpdatableField.AESTHETIC_THUMBNAIL).build());
            }
        } else {
            updateLog.setEventType(EventType.CREATED);

            UpdateLogEntry aestheticCreatedEntry = UpdateLogEntry.builder()
                    .updatableField(UpdatableField.AESTHETIC_AESTHETIC).build();

            updateLogEntries.add(aestheticCreatedEntry);
        }

        Aesthetic aesthetic = aestheticRepository.createOrUpdate(aestheticBuilder.build());
        int pkAesthetic = aesthetic.getAesthetic();

        /* Create or update tb_aesthetic_media */

        aestheticMediaEditRequest.setAesthetic(pkAesthetic);

        CariResponse mediaCreateOrUpdateResponse =
                aestheticMediaService.createOrUpdate(aestheticMediaEditRequest);

        if (mediaCreateOrUpdateResponse.getStatus().equals(RequestStatus.FAILURE)) {
            return mediaCreateOrUpdateResponse;
        }

        if ((boolean) mediaCreateOrUpdateResponse.getUpdatedData().get("didChange")
                && updateLog.getEventType() != EventType.CREATED) {
            updateLogEntries.add(UpdateLogEntry.builder()
                    .updatableField(UpdatableField.AESTHETIC_MEDIA).build());
        }

        /* Create or update tb_aesthetic_website */

        List<AestheticWebsite> websiteObjects = aestheticEditRequest.getWebsiteObjects();
        boolean websitesChanged;

        if (websiteObjects.isEmpty()) {
            int deleted = aestheticWebsiteRepository.deleteByAesthetic(pkAesthetic);
            websitesChanged = deleted > 0;
        } else {
            DatabaseUpsertResult<AestheticWebsite> websites = aestheticWebsiteRepository
                    .createOrUpdateForAesthetic(pkAesthetic, websiteObjects);

            int deleted = aestheticWebsiteRepository.deleteByAestheticExcept(pkAesthetic,
                    websites.getUpsertResultSet().stream()
                            .map(AestheticWebsite::getAestheticWebsite)
                            .collect(Collectors.toList()));

            websitesChanged = websites.isChanged() || deleted > 0;
        }

        if (websitesChanged && updateLog.getEventType() != EventType.CREATED) {
            updateLogEntries.add(UpdateLogEntry.builder()
                    .updatableField(UpdatableField.AESTHETIC_WEBSITES).build());
        }

        /* Create or update tb_aesthetic_relationship */

        List<SimilarAesthetic> similarAestheticObjects =
                aestheticEditRequest.getSimilarAestheticObjects();

        boolean similarAestheticsChanged;

        if (similarAestheticObjects.isEmpty()) {
            int deleted = aestheticRelationshipRepository.deleteByAesthetic(pkAesthetic);
            similarAestheticsChanged = deleted > 0;
        } else {
            List<AestheticRelationship> payloadAestheticRelationships =
                    similarAestheticObjects
                            .stream().flatMap(similarAesthetic -> similarAesthetic
                                    .toAestheticRelationships().stream())
                            .collect(Collectors.toList());

            DatabaseUpsertResult<AestheticRelationship> aestheticRelationships =
                    aestheticRelationshipRepository.createOrUpdateForAesthetic(pkAesthetic,
                            payloadAestheticRelationships);

            int deleted = aestheticRelationshipRepository.deleteByAestheticExcept(pkAesthetic,
                    aestheticRelationships.getUpsertResultSet().stream()
                            .map(AestheticRelationship::getAestheticRelationship)
                            .collect(Collectors.toList()));

            similarAestheticsChanged = aestheticRelationships.isChanged() || deleted > 0;
        }

        if (similarAestheticsChanged && updateLog.getEventType() != EventType.CREATED) {
            updateLogEntries.add(UpdateLogEntry.builder()
                    .updatableField(UpdatableField.AESTHETIC_RELATED_AESTHETICS).build());
        }

        /* Update the update log */

        if (!aesthetic.getIsDraft()) {
            updateLog.setPkVal(pkAesthetic);
            updateLog = updateLogRepository.save(updateLog);
            int pkUpdateLog = updateLog.getUpdateLog();

            updateLogEntries.forEach(entry -> entry.setUpdateLog(pkUpdateLog));
            updateLogEntryRepository.saveAll(updateLogEntries);
        }

        /* End creates and updates */

        CariResponse res = CariResponse.success();
        res.setUpdatedData(Map.of("urlSlug", urlSlug));
        return res;
    }
}
