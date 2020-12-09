package com.cari.web.server.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.cari.web.server.config.JwtProvider;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.domain.db.MessageTemplate;
import com.cari.web.server.repository.MessageTemplateRepository;
import com.cari.web.server.service.SendgridService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.OpenTrackingSetting;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.sendgrid.helpers.mail.objects.TrackingSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

@Service
public class SendgridServiceImpl implements SendgridService {

    @Value("${base.url}")
    private String baseUrl;

    @Value("${sendgrid.api.key}")
    private String sendgridApiKey;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MessageTemplateRepository messageTemplateRepository;

    private String getUrl(String path, Optional<Map<String, String>> parameters) {
        String url = String.format("%s%s", baseUrl, path);

        if (parameters.isPresent()) {
            url += "?" + parameters.get().entrySet().stream()
                    .map(entry -> entry.getKey() + '=' + entry.getValue())
                    .collect(Collectors.joining("&"));
        }

        return url;
    }

    private Response sendEmail(Entity toEntity, int pkMessageTemplate,
            Map<String, String> templateParams) {
        Optional<MessageTemplate> messageTemplateOptional =
                messageTemplateRepository.findById(pkMessageTemplate);

        if (messageTemplateOptional.isEmpty()) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Message template with PK " + pkMessageTemplate + " does not exist!");
        }

        MessageTemplate messageTemplate = messageTemplateOptional.get();
        String username = toEntity.getUsername();

        Email from = new Email("no-reply@c-a-r-i.org", "CARIbot 2.0");
        Email to = new Email(toEntity.getEmailAddress(), username);

        Personalization personalization = new Personalization();
        personalization.addTo(to);

        templateParams.entrySet().stream().forEach(entry -> {
            personalization.addDynamicTemplateData(entry.getKey(), entry.getValue());
        });

        OpenTrackingSetting openTrackingSetting = new OpenTrackingSetting();
        openTrackingSetting.setEnable(true);

        TrackingSettings trackingSettings = new TrackingSettings();
        trackingSettings.setOpenTrackingSetting(openTrackingSetting);

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.addPersonalization(personalization);
        mail.setTemplateId(messageTemplate.getExtId());
        mail.setTrackingSettings(trackingSettings);

        SendGrid sendgrid = new SendGrid(sendgridApiKey);
        Request sendgridRequest = new Request();
        Response sendgridResponse;

        try {
            sendgridRequest.setMethod(Method.POST);
            sendgridRequest.setEndpoint("mail/send");
            sendgridRequest.setBody(mail.build());

            sendgridResponse = sendgrid.api(sendgridRequest);
        } catch (IOException ex) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ex.getLocalizedMessage());
        }

        return sendgridResponse;
    }

    @Override
    public Response sendConfirmAccountEmail(Entity toEntity) {
        String jwt = jwtProvider.createConfirmToken(toEntity);
        String confirmUrl = getUrl("/user/confirm", Optional.of(Map.of("token", jwt)));

        Map<String, String> templateParams =
                Map.of("username", toEntity.getUsername(), "confirmUrl", confirmUrl);

        return sendEmail(toEntity, MessageTemplate.CONFIRM_ACCOUNT, templateParams);
    }

    @Override
    public Response sendForgotPasswordEmail(Entity toEntity) {
        String jwt = jwtProvider.createResetPasswordToken(toEntity);

        String confirmUrl = getUrl("/user/resetPassword", Optional.of(Map.of("token", jwt)));

        Map<String, String> templateParams =
                Map.of("username", toEntity.getUsername(), "resetPasswordUrl", confirmUrl);

        return sendEmail(toEntity, MessageTemplate.RESET_PASSWORD, templateParams);
    }

    @Override
    public Response sendInviteEmail(Entity fromEntity, Entity toEntity) {
        String jwt = jwtProvider.createInviteToken(fromEntity, toEntity);

        String registerUrl = getUrl("/user/register", Optional.of(Map.of("token", jwt)));

        Map<String, String> templateParams =
                Map.of("inviter", fromEntity.getUsername(), "registerUrl", registerUrl);

        return sendEmail(toEntity, MessageTemplate.INVITE, templateParams);
    }
}
