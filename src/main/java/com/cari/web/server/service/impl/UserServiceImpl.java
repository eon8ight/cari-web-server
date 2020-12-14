package com.cari.web.server.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.sql.DataSource;
import com.cari.web.server.domain.db.CariFile;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.domain.db.FileType;
import com.cari.web.server.dto.FileUploadResult;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.CariPage;
import com.cari.web.server.dto.response.CariResponse;
import com.cari.web.server.dto.response.UserInviteResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.repository.EntityRepository;
import com.cari.web.server.service.FileService;
import com.cari.web.server.service.ImageService;
import com.cari.web.server.service.SendgridService;
import com.cari.web.server.service.UserService;
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
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserServiceImpl implements UserService {

    private static final int MAX_PER_PAGE = 20;
    private static final int PROFILE_PICTURE_SIZE = 150;

    private static final String FILTER_INVITER = "inviter";
    private static final String FIELD_PROFILE_IMAGE = "profileImage";

    // @formatter:off
    private static final Map<String, String> SORT_FIELDS = Map.of(
        "emailAddress", "email_address",
        "invited", "invited",
        "registered", "registered",
        "confirmed", "confirmed",
        "username", "username"
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
        StringBuilder queryBuilder =
                new StringBuilder("select count(*) over (), * from tb_entity ");

        MapSqlParameterSource params = new MapSqlParameterSource();

        /* WHERE */

        String inviter = filters.get(FILTER_INVITER);
        List<String> filterClauses = new ArrayList<String>();

        if (inviter != null) {
            filterClauses.add("inviter = :inviter\\:\\:integer");
            params.addValue("inviter", inviter);
        }

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
                sort, Entity::fromResultSet);
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
    public CariResponse edit(ClientRequestEntity clientRequestEntity) {
        Entity principal = getSessionEntity();

        String newUsername = clientRequestEntity.getUsername();
        String newEmailAddress = clientRequestEntity.getEmailAddress();
        String newPassword = clientRequestEntity.getPassword();
        String newFirstName = clientRequestEntity.getFirstName();
        String newLastName = clientRequestEntity.getLastName();
        String newBiography = clientRequestEntity.getBiography();
        String newTitle = clientRequestEntity.getTitle();
        MultipartFile newProfileImage = clientRequestEntity.getProfileImage();
        Integer newFavoriteAesthetic = clientRequestEntity.getFavoriteAesthetic();

        List<FieldError> fieldErrors = new ArrayList<>(3);
        Map<String, Object> updatedData = new HashMap<>(1);

        if (newUsername != null) {
            Optional<Entity> entityWithUsername = entityRepository.findByUsername(newUsername);

            if (entityWithUsername.isPresent()) {
                if (entityWithUsername.get().getEntity() != principal.getEntity()) {
                    fieldErrors.add(
                            new FieldError("username", "username", "Username is already in use."));
                }
            }

            principal.setUsername(newUsername);
        }

        if (newEmailAddress != null) {
            Optional<Entity> entityWithEmailAddress =
                    entityRepository.findByEmailAddress(newEmailAddress);

            if (entityWithEmailAddress.isPresent()) {
                if (entityWithEmailAddress.get().getEntity() != principal.getEntity()) {
                    fieldErrors.add(new FieldError("emailAddress", "emailAddress",
                            "Email address is already in use."));
                }
            } else {
                principal.setEmailAddress(newEmailAddress);
            }
        }

        if (newProfileImage != null) {
            try {
                File tmpProfileImage = File.createTempFile("cari-", ".tmp");
                newProfileImage.transferTo(tmpProfileImage);

                BufferedImage profileImage = ImageIO.read(tmpProfileImage);

                if (!imageService.isImageSquare(profileImage)) {
                    fieldErrors.add(new FieldError(FIELD_PROFILE_IMAGE, FIELD_PROFILE_IMAGE,
                            "Image must be a square."));
                } else if (!imageService.isImageMinimumSize(profileImage, PROFILE_PICTURE_SIZE)) {
                    String errorMessage = new StringBuilder("Image must be at least ")
                            .append(PROFILE_PICTURE_SIZE).append(" pixels by ")
                            .append(PROFILE_PICTURE_SIZE).append(" pixels.").toString();

                    fieldErrors.add(
                            new FieldError(FIELD_PROFILE_IMAGE, FIELD_PROFILE_IMAGE, errorMessage));
                } else {
                    profileImage = imageService.resizeImage(profileImage, PROFILE_PICTURE_SIZE);
                    boolean wroteImage = ImageIO.write(profileImage, "png", tmpProfileImage);

                    if (wroteImage) {
                        FileUploadResult uploadResult =
                                fileService.upload(tmpProfileImage, FileType.FILE_TYPE_IMAGE);

                        if (uploadResult.getStatus().equals(RequestStatus.SUCCESS)) {
                            CariFile dbProfileImage = uploadResult.getFile();
                            principal.setProfileImageFile(dbProfileImage.getFile());
                            updatedData.put("profileImageUrl", dbProfileImage.getUrl());
                        } else {
                            fieldErrors.add(new FieldError(FIELD_PROFILE_IMAGE, FIELD_PROFILE_IMAGE,
                                    uploadResult.getMessage()));
                        }
                    } else {
                        fieldErrors.add(new FieldError(FIELD_PROFILE_IMAGE, FIELD_PROFILE_IMAGE,
                                "Failed to resize image."));
                    }
                }
            } catch (IOException ex) {
                fieldErrors.add(new FieldError(FIELD_PROFILE_IMAGE, FIELD_PROFILE_IMAGE,
                        ex.getLocalizedMessage()));
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
