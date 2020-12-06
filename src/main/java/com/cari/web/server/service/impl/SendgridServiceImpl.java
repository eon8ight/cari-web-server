package com.cari.web.server.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import com.cari.web.server.config.JwtProvider;
import com.cari.web.server.domain.Entity;
import com.cari.web.server.domain.MessageTemplate;
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

    private static final String ENVIRONMENT_PRODUCTION = "production";

    @Value("${sendgrid.api.key}")
    private String sendgridApiKey;

    @Value("${env:" + ENVIRONMENT_PRODUCTION + "}")
    private String environment;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MessageTemplateRepository messageTemplateRepository;

    private String getUrl(HttpServletRequest request, String path, Map<String, String> parameters) {
        String host = request.getServerName();

        if (!environment.equals(ENVIRONMENT_PRODUCTION)) {
            host += ":" + request.getServerPort();
        }

        String url = String.format("%s://%s%s", request.getScheme(), host, path);

        if (parameters != null) {
            url += "?" + parameters.entrySet().stream()
                    .map(entry -> entry.getKey() + '=' + entry.getValue())
                    .collect(Collectors.joining("&"));
        }

        return url;
    }

    @Override
    public Response sendConfirmAccountEmail(HttpServletRequest request, Entity toEntity,
            int pkMessageTemplate) {
        Optional<MessageTemplate> messageTemplateOptional =
                messageTemplateRepository.findById(pkMessageTemplate);

        if (messageTemplateOptional.isEmpty()) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Message template with PK " + pkMessageTemplate + " does not exist!");
        }

        MessageTemplate messageTemplate = messageTemplateOptional.get();
        String username = toEntity.getUsername();

        String jwt = jwtProvider.createConfirmToken(toEntity, null);
        String confirmUrl = getUrl(request, "/user/confirm", Map.of("token", jwt));

        Email from = new Email("no-reply@c-a-r-i.org", "CARIbot 2.0");
        Email to = new Email(toEntity.getEmailAddress(), username);

        Personalization personalization = new Personalization();
        personalization.addTo(to);
        personalization.addDynamicTemplateData("username", username);
        personalization.addDynamicTemplateData("confirmUrl", confirmUrl);

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
}
