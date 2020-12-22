package com.cari.web.server.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.sql.DataSource;
import com.cari.web.server.domain.CariFieldError;
import com.cari.web.server.domain.db.Aesthetic;
import com.cari.web.server.domain.db.AestheticMedia;
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

    // @formatter:off
    private static final Map<String, String> SORT_FIELDS = Map.of(
        "name", "a.name",
        FILTER_START_YEAR, "fn_get_approximate_start_year(a.aesthetic)",
        FILTER_END_YEAR, "fn_get_approximate_end_year(a.aesthetic)"
    );
    // @formatter:on

    private static final String MEDIA_IMAGE_SIZE_MESSAGE =
            new StringBuilder("Image must be at least ").append(MEDIA_FILE_SIZE_THUMBNAIL)
                    .append(" pixels by ").append(MEDIA_FILE_SIZE_THUMBNAIL).append(" pixels.")
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
        int pkAesthetic = aestheticEditRequest.getAesthetic();

        String name = aestheticEditRequest.getName();
        String symbol = aestheticEditRequest.getSymbol();

        String urlSlug = name.toLowerCase().strip().replaceAll("[^a-zA-Z0-9-\\s]", "")
                .replaceAll("\\s+", "-");

        List<AestheticMediaEditRequest> mediaMap = aestheticEditRequest.getMediaObjects();
        int mediaEditRequestsSize = mediaMap.size();

        List<CariFieldError> fieldErrors = new ArrayList<>(2 + mediaEditRequestsSize);

        Optional<Aesthetic> existingAesthetic =
                aestheticRepository.findByNameOrUrlSlug(name, urlSlug);

        if (existingAesthetic.isPresent()
                && existingAesthetic.get().getAesthetic() != pkAesthetic) {
            fieldErrors.add(new CariFieldError("name", "Name is already in use."));
        }

        existingAesthetic = aestheticRepository.findBySymbol(symbol);

        if (existingAesthetic.isPresent()
                && existingAesthetic.get().getAesthetic() != pkAesthetic) {
            fieldErrors.add(new CariFieldError("symbol", "Symbol is already in use."));
        }

        if (!fieldErrors.isEmpty()) {
            return CariResponse.failure(fieldErrors);
        }

        ImageValidator minSizeValidator =
                bf -> imageService.isImageMinimumSize(bf, MEDIA_FILE_SIZE_THUMBNAIL)
                        ? Optional.empty()
                        : Optional.of(MEDIA_IMAGE_SIZE_MESSAGE);

        List<Integer> pkMedia = IntStream.range(0, mediaEditRequestsSize).mapToObj(idx -> {
            AestheticMediaEditRequest media = mediaMap.get(idx);
            Integer pkMediaCreator = media.getMediaCreator();

            if (pkMediaCreator == null) {
                pkMediaCreator = mediaCreatorRepository.getOrCreate(media.getMediaCreatorName());
            }

            MultipartFile fileObject = media.getFileObject();

            // @formatter:off
            AestheticMedia.AestheticMediaBuilder mediaBuilder = AestheticMedia.builder()
                .aesthetic(pkAesthetic)
                .label(media.getLabel())
                .description(media.getDescription())
                .mediaCreator(pkMediaCreator)
                .year(media.getYear());
            // @formatter:on

            if (fileObject != null) {
                try {
                    File fileObjectTmp = fileService.copyToTmpFile(fileObject);

                    FileOperationResult mediaFileRes = fileService.processImageAndUploadAndSave(
                            fileObjectTmp, bf -> bf, Arrays.asList(minSizeValidator));

                    if (mediaFileRes.getStatus().equals(RequestStatus.FAILURE)) {
                        fieldErrors.add(
                                new CariFieldError(FIELD_MEDIA, mediaFileRes.getMessage(), idx));

                        return null;
                    }

                    FileOperationResult mediaThumbnailFileRes =
                            fileService.processImageAndUploadAndSave(fileObjectTmp,
                                    bf -> imageService.resizeImage(bf, MEDIA_FILE_SIZE_THUMBNAIL),
                                    Collections.emptyList());

                    if (mediaThumbnailFileRes.getStatus().equals(RequestStatus.FAILURE)) {
                        fieldErrors.add(new CariFieldError(FIELD_MEDIA,
                                mediaThumbnailFileRes.getMessage(), idx));

                        fileService.delete(mediaFileRes.getDbFile().get());
                        return null;
                    }

                    FileOperationResult mediaPreviewFileRes =
                            fileService.processImageAndUploadAndSave(fileObjectTmp,
                                    bf -> imageService.resizeImage(bf, 500),
                                    Collections.emptyList());

                    if (mediaPreviewFileRes.getStatus().equals(RequestStatus.FAILURE)) {
                        fieldErrors.add(new CariFieldError(FIELD_MEDIA,
                                mediaPreviewFileRes.getMessage(), idx));

                        fileService.delete(mediaFileRes.getDbFile().get());
                        fileService.delete(mediaThumbnailFileRes.getDbFile().get());
                        return null;
                    }

                    // @formatter:off
                    mediaBuilder
                        .mediaFile(mediaFileRes.getDbFile().get().getFile())
                        .mediaThumbnailFile(mediaThumbnailFileRes.getDbFile().get().getFile())
                        .mediaPreviewFile(mediaPreviewFileRes.getDbFile().get().getFile());
                    // @formatter:on

                    return aestheticMediaRepository.createOrUpdate(mediaBuilder.build());
                } catch (IOException ex) {
                    fieldErrors.add(new CariFieldError(FIELD_MEDIA, ex.getLocalizedMessage(), idx));
                    return null;
                }
            } else {
                return aestheticMediaRepository.updateExceptFiles(mediaBuilder.build());
            }
        }).collect(Collectors.toList());

        if (!fieldErrors.isEmpty()) {
            return CariResponse.failure(fieldErrors);
        }

        // @formatter:off
        Aesthetic aesthetic = Aesthetic.builder()
            .aesthetic(pkAesthetic)
            .name(name)
            .urlSlug(urlSlug)
            .symbol(symbol)
            .startEra(aestheticEditRequest.getStartEra())
            .endEra(aestheticEditRequest.getEndEra())
            .description(aestheticEditRequest.getDescription())
            .mediaSourceUrl(aestheticEditRequest.getMediaSourceUrl())
            .build();
        // @formatter:on

        List<Integer> pkWebsites = aestheticEditRequest.getWebsiteObjects().stream()
                .map(website -> aestheticWebsiteRepository.createOrUpdate(pkAesthetic, website))
                .collect(Collectors.toList());

        if (pkWebsites.isEmpty()) {
            aestheticWebsiteRepository.deleteByAesthetic(pkAesthetic);
        } else {
            aestheticWebsiteRepository.deleteByAestheticExcept(pkAesthetic, pkWebsites);
        }

        List<Integer> pkAestheticRelationships = aestheticEditRequest.getSimilarAestheticObjects()
                .stream().map(similarAesthetic -> aestheticRelationshipRepository
                        .createOrUpdate(pkAesthetic, similarAesthetic))
                .flatMap(List::stream).collect(Collectors.toList());

        if (pkAestheticRelationships.isEmpty()) {
            aestheticRelationshipRepository.deleteByAesthetic(pkAesthetic);
        } else {
            aestheticRelationshipRepository.deleteByAestheticExcept(pkAesthetic,
                    pkAestheticRelationships);
        }

        if (pkMedia.isEmpty()) {
            aestheticMediaRepository.deleteByAesthetic(pkAesthetic);
        } else {
            aestheticMediaRepository.deleteByAestheticExcept(pkAesthetic, pkMedia);
        }

        aestheticRepository.save(aesthetic);
        return CariResponse.success();
    }
}
