package com.cari.web.server.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import com.cari.web.server.domain.CariFieldError;
import com.cari.web.server.domain.SimilarAesthetic;
import com.cari.web.server.domain.db.Aesthetic;
import com.cari.web.server.domain.db.AestheticWebsite;
import com.cari.web.server.domain.db.CariFile;
import com.cari.web.server.dto.request.AestheticEditRequest;
import com.cari.web.server.dto.request.AestheticMediaEditRequest;
import com.cari.web.server.dto.response.CariPage;
import com.cari.web.server.dto.response.CariResponse;
import com.cari.web.server.exception.FileProcessingException;
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
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static final int MEDIA_FILE_SIZE_THUMBNAIL = 200;

    // @formatter:off
    private static final Map<String, String> SORT_FIELDS = Map.of(
        "name", "name",
        FILTER_START_YEAR, "fn_get_approximate_start_year(aesthetic)",
        FILTER_END_YEAR, "fn_get_approximate_end_year(aesthetic)"
    );
    // @formatter:on

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
        StringBuilder queryBuilder =
                new StringBuilder("select count(*) over (), * from tb_aesthetic ");

        MapSqlParameterSource params = new MapSqlParameterSource();

        /* WHERE */

        String keyword = filters.get(FILTER_KEYWORD);
        Optional<Integer> startYear = QueryUtils.validateAndGetInt(filters, FILTER_START_YEAR);
        Optional<Integer> endYear = QueryUtils.validateAndGetInt(filters, FILTER_END_YEAR);

        List<String> filterClauses = new ArrayList<String>();

        if (keyword != null) {
            filterClauses.add(
                    "name ilike '%' || :keyword || '%' or description ilike '%' || :keyword || '%'");

            params.addValue("keyword", keyword);
        }

        if (startYear.isPresent()) {
            filterClauses.add("abs(fn_get_approximate_start_year(aesthetic) - :startYear) <= 3");
            params.addValue("startYear", startYear.get().intValue());
        }

        if (endYear.isPresent()) {
            filterClauses.add("abs(fn_get_approximate_end_year(aesthetic) - :endYear) <= 3");
            params.addValue("endYear", endYear.get().intValue());
        }

        queryBuilder.append(QueryUtils.toWhereClause(filterClauses));

        /* ORDER BY */

        Sort sort = QueryUtils.validateAndGetSort(filters, SORT_FIELDS,
                () -> Sort.by(Sort.Order.asc("startYear").nullsLast(),
                        Sort.Order.asc("endYear").nullsLast()));

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
        ObjectMapper objectMapper = new ObjectMapper();

        int pkAesthetic = aestheticEditRequest.getAesthetic();

        String name = aestheticEditRequest.getName();
        String symbol = aestheticEditRequest.getSymbol();

        List<Map<Object, Object>> mediaMap = aestheticEditRequest.getMedia();
        int mediaEditRequestsSize = mediaMap.size();

        String urlSlug = name.toLowerCase().strip().replaceAll("[^a-zA-Z0-9-\\s]", "")
                .replaceAll("\\s+", "-");

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

        List<Integer> pkMedia = new ArrayList<>(mediaEditRequestsSize);

        for (int i = 0; i < mediaEditRequestsSize; i++) {
            AestheticMediaEditRequest media = AestheticMediaEditRequest.fromMap(mediaMap.get(i));
            Integer pkMediaCreator = media.getMediaCreator();

            if (pkMediaCreator == null) {
                pkMediaCreator = mediaCreatorRepository.getOrCreate(media.getMediaCreatorName());
            }

            String label = media.getLabel();
            String description = media.getDescription();
            int year = media.getYear();

            MultipartFile fileObject = media.getFileObject();

            int pkAestheticMedia = 0;

            if (fileObject != null) {
                try {
                    ImageValidator minSizeValidator =
                            bf -> imageService.isImageMinimumSize(bf, MEDIA_FILE_SIZE_THUMBNAIL)
                                    ? Optional.empty()
                                    : Optional.of(new StringBuilder("Image must be at least ")
                                            .append(MEDIA_FILE_SIZE_THUMBNAIL).append(" pixels by ")
                                            .append(MEDIA_FILE_SIZE_THUMBNAIL).append(" pixels.")
                                            .toString());

                    ImageProcessor thumbnailResizer =
                            bf -> imageService.resizeImage(bf, MEDIA_FILE_SIZE_THUMBNAIL);

                    ImageProcessor previewResizer = bf -> imageService.resizeImage(bf, 500);

                    CariFile mediaFile = fileService.processAndSaveImage(fileObject, bf -> bf,
                            Arrays.asList(minSizeValidator));

                    CariFile mediaThumbnailFile = fileService.processAndSaveImage(fileObject,
                            thumbnailResizer, Collections.emptyList());

                    CariFile mediaPreviewFile = fileService.processAndSaveImage(fileObject,
                            previewResizer, Collections.emptyList());

                    pkAestheticMedia = aestheticMediaRepository.createOrUpdate(pkAesthetic,
                            mediaFile.getFile(), mediaThumbnailFile.getFile(),
                            mediaPreviewFile.getFile(), label, description, pkMediaCreator, year);

                } catch (FileProcessingException ex) {
                    fieldErrors.add(new CariFieldError("media", ex.getMessage(), i));
                    break;
                }
            } else {
                pkAestheticMedia = aestheticMediaRepository.updateExceptFiles(label, description,
                        pkMediaCreator, year);
            }

            pkMedia.add(pkAestheticMedia);
        }

        if (!fieldErrors.isEmpty()) {
            return CariResponse.failure(fieldErrors);
        }

        // @formatter:off
        Aesthetic aesthetic = Aesthetic.builder()
            .aesthetic(pkAesthetic)
            .name(name)
            .urlSlug(urlSlug)
            .symbol(symbol)
            .startYear(aestheticEditRequest.getStartYear())
            .endYear(aestheticEditRequest.getEndYear())
            .description(aestheticEditRequest.getDescription())
            .mediaSourceUrl(aestheticEditRequest.getMediaSourceUrl())
            .build();
        // @formatter:on

        List<Integer> pkWebsites = aestheticEditRequest.getWebsites().stream().map(map -> {
            AestheticWebsite website = objectMapper.convertValue(map, AestheticWebsite.class);

            return aestheticWebsiteRepository.createOrUpdate(pkAesthetic, website.getUrl(),
                    website.getWebsiteType());
        }).collect(Collectors.toList());

        if (pkWebsites.isEmpty()) {
            aestheticWebsiteRepository.deleteByAesthetic(pkAesthetic);
        } else {
            aestheticWebsiteRepository.deleteByAestheticExcept(pkAesthetic, pkWebsites);
        }

        List<Integer> pkAestheticRelationships =
                aestheticEditRequest.getSimilarAesthetics().stream().map(map -> {
                    SimilarAesthetic similarAesthetic =
                            objectMapper.convertValue(map, SimilarAesthetic.class);

                    return aestheticRelationshipRepository.createOrUpdate(pkAesthetic,
                            similarAesthetic.getAesthetic(), similarAesthetic.getDescription(),
                            similarAesthetic.getReverseDescription());
                }).flatMap(List::stream).collect(Collectors.toList());

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
