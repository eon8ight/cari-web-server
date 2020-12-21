package com.cari.web.server.service.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import com.cari.web.server.domain.CariFieldError;
import com.cari.web.server.domain.db.CariFile;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.dto.FileOperationResult;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.request.EntityEditRequest;
import com.cari.web.server.dto.response.CariPage;
import com.cari.web.server.dto.response.CariResponse;
import com.cari.web.server.dto.response.UserInviteResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.repository.EntityRepository;
import com.cari.web.server.service.FileService;
import com.cari.web.server.service.ImageService;
import com.cari.web.server.service.SendgridService;
import com.cari.web.server.service.UserService;
import com.cari.web.server.util.ImageProcessor;
import com.cari.web.server.util.ImageValidator;
import com.cari.web.server.util.db.EntityWithJoinDataMapper;
import com.cari.web.server.util.db.QueryUtils;
import com.sendgrid.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserServiceImpl implements UserService {

    private static final int MAX_PER_PAGE = 20;
    private static final int PROFILE_PICTURE_SIZE = 150;

    private static final String FILTER_INVITER = "inviter";
    private static final String FILTER_ROLE = "role";
    private static final String FILTER_INCLUDE_PROFILE_IMAGE = "includeProfileImage";
    private static final String FILTER_INCLUDE_FAVORITE_AESTHETIC = "includeFavoriteAesthetic";

    private static final String FIELD_PROFILE_IMAGE = "profileImage";

    private static final String PROFILE_IMAGE_SIZE_MESSAGE =
            new StringBuilder("Image must be at least ").append(PROFILE_PICTURE_SIZE)
                    .append(" pixels by ").append(PROFILE_PICTURE_SIZE).append(" pixels.")
                    .toString();

    // @formatter:off
    private static final Map<String, String> SORT_FIELDS = Map.of(
        "emailAddress", "e.email_address",
        "invited", "e.invited",
        "registered", "e.registered",
        "confirmed", "e.confirmed",
        "username", "e.username",
        "lastName", "e.last_name",
        "firstName", "e.first_name"
    );
    // @formatter:on

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SendgridService sendgridService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private DataSource dbHandle;

    private Entity getSessionEntity() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        return (Entity) authentication.getPrincipal();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Entity> entity = entityRepository.findByUsernameOrEmailAddress(username);

        if (entity.isEmpty()) {
            throw new UsernameNotFoundException(
                    "User with username or email address \"" + username + "\" does not exist");
        }

        return entity.get();
    }

    @Override
    public Optional<Entity> find(int pkEntity) {
        return entityRepository.findByPk(pkEntity);
    }

    @Override
    public CariResponse register(int pkEntity, ClientRequestEntity clientRequestEntity) {
        Optional<Entity> entityOptional = entityRepository.findById(pkEntity);

        if (entityOptional.isEmpty()) {
            return CariResponse.failure("Token contains an invalid entity PK!");
        }

        Entity entity = entityOptional.get();
        String username = clientRequestEntity.getUsername();

        Optional<Entity> entityWithUsername = entityRepository.findByUsername(username);

        if (entityWithUsername.isPresent()) {
            return CariResponse.failure("Username is already in use.");
        }

        entity.setUsername(username);
        entity.setPasswordHash(passwordEncoder.encode(clientRequestEntity.getPassword()));
        entity.setRegistered(Timestamp.from(Instant.now()));

        entity = entityRepository.save(entity);

        Response response = sendgridService.sendConfirmAccountEmail(entity);

        return response.getStatusCode() >= 400 ? CariResponse.failure(response.getBody())
                : CariResponse.success();
    }

    @Override
    public CariResponse confirm(int pkEntity) {
        Optional<Entity> entityOptional = entityRepository.findById(pkEntity);

        if (entityOptional.isEmpty()) {
            return CariResponse.failure("Entity with PK " + pkEntity + " does not exist!");
        }

        Entity entity = entityOptional.get();
        entity.setConfirmed(Timestamp.from(Instant.now()));
        entityRepository.save(entity);

        return CariResponse.success();
    }

    @Override
    public CariResponse resetPassword(int pkEntity, String password) {
        Optional<Entity> entityOptional = entityRepository.findById(pkEntity);

        if (entityOptional.isEmpty()) {
            return CariResponse.failure("Entity with PK " + pkEntity + " does not exist!");
        }

        Entity entity = entityOptional.get();
        entity.setPasswordHash(passwordEncoder.encode(password));
        entityRepository.save(entity);

        return CariResponse.success();
    }

    @Override
    public Page<Entity> findForList(Map<String, String> filters) {
        StringBuilder queryBuilder = new StringBuilder("select ");

        List<String> columns = new ArrayList<>(Arrays.asList("count(*) over ()", "e.entity",
                "e.email_address", "e.username", "e.password_hash", "e.invited", "e.confirmed",
                "e.registered", "e.inviter", "e.first_name", "e.last_name", "e.biography",
                "e.title", "e.profile_image_file", "e.favorite_aesthetic"));

        List<String> joins = new ArrayList<>(2);

        if (filters.get(FILTER_INCLUDE_PROFILE_IMAGE) != null) {
            columns.add("to_jsonb(f.*) as profile_image");
            joins.add("left join tb_file f on e.profile_image_file = f.file");
        }

        if (filters.get(FILTER_INCLUDE_FAVORITE_AESTHETIC) != null) {
            columns.add("to_jsonb(a.*) as favorite_aesthetic_data");
            joins.add("left join tb_aesthetic a on e.favorite_aesthetic = a.aesthetic");
        }

        queryBuilder.append(columns.stream().collect(Collectors.joining(", ")))
                .append(" from tb_entity e ");

        MapSqlParameterSource params = new MapSqlParameterSource();

        /* WHERE */

        String inviter = filters.get(FILTER_INVITER);
        List<String> filterClauses = new ArrayList<String>();

        if (inviter != null) {
            filterClauses.add("e.inviter = :inviter\\:\\:integer");
            params.addValue("inviter", inviter);
        }

        String role = filters.get(FILTER_ROLE);

        if (role != null) {
            filterClauses.add("er.role = :role\\:\\:integer");
            params.addValue("role", role);
            joins.add("join tb_entity_role er on e.entity = er.entity");
        }

        queryBuilder.append(joins.stream().collect(Collectors.joining(" ")));
        queryBuilder.append(QueryUtils.toWhereClause(filterClauses));

        /* ORDER BY */

        Sort sort = QueryUtils.validateAndGetSort(filters, SORT_FIELDS,
                () -> Sort.by(Sort.Order.asc("username")));

        queryBuilder.append(QueryUtils.toOrderByClause(sort, SORT_FIELDS));

        /* LIMIT and OFFSET */

        Optional<Integer> pageNumOptional =
                QueryUtils.validateAndGetIntNonNegative(filters, QueryUtils.FILTER_PAGE);

        int pageNum = pageNumOptional.orElse(0);

        queryBuilder.append("limit :limit offset :offset");
        params.addValue("limit", MAX_PER_PAGE);
        params.addValue("offset", pageNum * MAX_PER_PAGE);

        return CariPage.getPage(dbHandle, queryBuilder.toString(), params, pageNum, MAX_PER_PAGE,
                sort, new EntityWithJoinDataMapper());
    }

    @Override
    public UserInviteResponse invite(ClientRequestEntity clientRequestEntity) {
        Entity principal = getSessionEntity();

        int pkInviter = Integer.parseInt(principal.getUsername());
        Optional<Entity> inviterOptional = entityRepository.findById(pkInviter);

        if (inviterOptional.isEmpty()) {
            return UserInviteResponse.failure("Entity with PK " + pkInviter + " does no exist!");
        }

        Entity inviter = inviterOptional.get();

        String emailAddress = clientRequestEntity.getEmailAddress();
        Optional<Entity> entityWithEmailAddress = entityRepository.findByEmailAddress(emailAddress);

        if (entityWithEmailAddress.isPresent()) {
            return UserInviteResponse
                    .failure("An invitation has already been sent to this email address.");
        }

        // @formatter:off
        Entity entity = Entity.builder()
            .emailAddress(emailAddress)
            .inviter(pkInviter)
            .invited(Timestamp.from(Instant.now()))
            .build();
        // @formatter:on

        entityRepository.save(entity);

        Response response = sendgridService.sendInviteEmail(inviter, entity);

        return response.getStatusCode() >= 400 ? UserInviteResponse.failure(response.getBody())
                : UserInviteResponse.success(entity);
    }

    @Override
    public Entity findForEdit() {
        return getSessionEntity();
    }

    @Override
    public CariResponse edit(EntityEditRequest editRequestEntity) {
        Entity principal = getSessionEntity();

        String newUsername = editRequestEntity.getUsername();
        String newEmailAddress = editRequestEntity.getEmailAddress();
        String newPassword = editRequestEntity.getPassword();
        String newFirstName = editRequestEntity.getFirstName();
        String newLastName = editRequestEntity.getLastName();
        String newBiography = editRequestEntity.getBiography();
        String newTitle = editRequestEntity.getTitle();
        MultipartFile newProfileImage = editRequestEntity.getProfileImage();
        Integer newFavoriteAesthetic = editRequestEntity.getFavoriteAesthetic();

        List<CariFieldError> fieldErrors = new ArrayList<>(3);
        Map<String, Object> updatedData = new HashMap<>(1);

        if (newUsername != null) {
            Optional<Entity> entityWithUsername = entityRepository.findByUsername(newUsername);

            if (entityWithUsername.isPresent()) {
                if (entityWithUsername.get().getEntity() != principal.getEntity()) {
                    fieldErrors.add(new CariFieldError("username", "Username is already in use."));
                }
            }

            principal.setUsername(newUsername);
        }

        if (newEmailAddress != null) {
            Optional<Entity> entityWithEmailAddress =
                    entityRepository.findByEmailAddress(newEmailAddress);

            if (entityWithEmailAddress.isPresent()) {
                if (entityWithEmailAddress.get().getEntity() != principal.getEntity()) {
                    fieldErrors.add(
                            new CariFieldError("emailAddress", "Email address is already in use."));
                }
            } else {
                principal.setEmailAddress(newEmailAddress);
            }
        }

        if (newProfileImage != null) {
            ImageValidator squareValidator = bf -> imageService.isImageSquare(bf) ? Optional.empty()
                    : Optional.of("Image must be a square.");

            ImageValidator minSizeValidator =
                    bf -> imageService.isImageMinimumSize(bf, PROFILE_PICTURE_SIZE)
                            ? Optional.empty()
                            : Optional.of(PROFILE_IMAGE_SIZE_MESSAGE);

            ImageProcessor resizer = bf -> imageService.resizeImage(bf, PROFILE_PICTURE_SIZE);

            try {
                File profileImageTmp = fileService.copyToTmpFile(newProfileImage);

                FileOperationResult profileImageRes = fileService.processImageAndUploadAndSave(
                        profileImageTmp, resizer, Arrays.asList(squareValidator, minSizeValidator));

                if (profileImageRes.getStatus().equals(RequestStatus.FAILURE)) {
                    fieldErrors.add(
                            new CariFieldError(FIELD_PROFILE_IMAGE, profileImageRes.getMessage()));
                }

                CariFile dbProfileImage = profileImageRes.getDbFile().get();

                principal.setProfileImageFile(dbProfileImage.getFile());
                updatedData.put("profileImageUrl", dbProfileImage.getUrl());
            } catch (IOException ex) {
                fieldErrors.add(new CariFieldError(FIELD_PROFILE_IMAGE, ex.getMessage()));
            }
        }

        if (!fieldErrors.isEmpty()) {
            return CariResponse.failure(fieldErrors);
        }

        if (newPassword != null) {
            principal.setPasswordHash(passwordEncoder.encode(newPassword));
        }

        if (newFirstName != null) {
            principal.setFirstName(newFirstName);
        }

        if (newLastName != null) {
            principal.setLastName(newLastName);
        }

        if (newBiography != null) {
            principal.setBiography(newBiography);
        }

        if (newTitle != null) {
            principal.setTitle(newTitle);
        }

        if (newFavoriteAesthetic != null) {
            principal.setFavoriteAesthetic(newFavoriteAesthetic);
        }

        entityRepository.save(principal);

        CariResponse res = CariResponse.success();
        res.setUpdatedData(updatedData);

        return res;
    }
}
