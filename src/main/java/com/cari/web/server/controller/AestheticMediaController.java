package com.cari.web.server.controller;

import com.cari.web.server.dto.request.AestheticMediaEditRequest;
import com.cari.web.server.dto.response.CariResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.service.AestheticMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AestheticMediaController {

    @Autowired
    private AestheticMediaService aestheticMediaService;

    @RequestMapping(path = "/aestheticMedia/edit", method = RequestMethod.POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CariResponse> edit(
            @ModelAttribute AestheticMediaEditRequest aestheticMediaEditRequest) {
        CariResponse response = aestheticMediaService.validateAndCreateOrUpdate(aestheticMediaEditRequest);

        ResponseEntity.BodyBuilder responseBuilder =
                response.getStatus().equals(RequestStatus.SUCCESS) ? ResponseEntity.ok()
                        : ResponseEntity.badRequest();

        return responseBuilder.body(response);
    }
}
