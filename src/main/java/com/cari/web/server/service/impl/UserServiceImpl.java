package com.cari.web.server.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.AuthResponse;
import com.cari.web.server.dto.response.CariPage;
import com.cari.web.server.repository.EntityRepository;
import com.cari.web.server.service.SendgridService;
import com.cari.web.server.service.UserService;
import com.cari.web.server.util.QueryUtils;
import com.sendgrid.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final int MAX_PER_PAGE = 20;

    private static final String FILTER_INVITER = "inviter";

    private static final Map<String, String> SORT_FIELDS =
            Map.of("emailAddress", "email_address", "invited", "invited", "registered",
                    "registered", "confirmed", "confirmed", "username", "username");

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SendgridService sendgridService;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private DataSource dbHandle;

    @Override
    public AuthResponse register(int pkEntity, ClientRequestEntity clientRequestEntity) {
        Optional<Entity> entityOptional = entityRepository.findById(pkEntity);

        if (entityOptional.isEmpty()) {
            return AuthResponse.failure("Token contains an invalid entity PK!");
        }

        Entity entity = entityOptional.get();
        String username = clientRequestEntity.getUsername();

        Optional<Entity> entityWithUsername = entityRepository.findByUsername(username);

        if (entityWithUsername.isPresent()) {
            return AuthResponse.failure("Username is already in use.");
        }

        entity.setUsername(username);
        entity.setPasswordHash(passwordEncoder.encode(clientRequestEntity.getPassword()));
        entity.setRegistered(Timestamp.from(Instant.now()));

        entity = entityRepository.save(entity);

        Response response = sendgridService.sendConfirmAccountEmail(entity);

        return response.getStatusCode() >= 400 ? AuthResponse.failure(response.getBody())
                : AuthResponse.success();
    }

    @Override
    public AuthResponse confirm(int pkEntity) {
        Optional<Entity> entityOptional = entityRepository.findById(pkEntity);

        if (entityOptional.isEmpty()) {
            return AuthResponse.failure("Entity with PK " + pkEntity + " does not exist!");
        }

        Entity entity = entityOptional.get();
        entity.setConfirmed(Timestamp.from(Instant.now()));
        entityRepository.save(entity);

        return AuthResponse.success();
    }

    @Override
    public AuthResponse resetPassword(int pkEntity, String password) {
        Optional<Entity> entityOptional = entityRepository.findById(pkEntity);

        if (entityOptional.isEmpty()) {
            return AuthResponse.failure("Entity with PK " + pkEntity + " does not exist!");
        }

        Entity entity = entityOptional.get();
        entity.setPasswordHash(passwordEncoder.encode(password));
        entityRepository.save(entity);

        return AuthResponse.success();
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
    public AuthResponse invite(ClientRequestEntity clientRequestEntity) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        int pkInviter = Integer.parseInt(principal.getUsername());
        Optional<Entity> inviterOptional = entityRepository.findById(pkInviter);

        if (inviterOptional.isEmpty()) {
            return AuthResponse.failure("Entity with PK " + pkInviter + " does no exist!");
        }

        Entity inviter = inviterOptional.get();

        String emailAddress = clientRequestEntity.getEmailAddress();
        Optional<Entity> entityWithEmailAddress = entityRepository.findByEmailAddress(emailAddress);

        if (entityWithEmailAddress.isPresent()) {
            return AuthResponse
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

        return response.getStatusCode() >= 400 ? AuthResponse.failure(response.getBody())
                : AuthResponse.success();
    }
}
