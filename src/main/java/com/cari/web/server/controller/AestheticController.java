package com.cari.web.server.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.cari.web.server.domain.db.Aesthetic;
import com.cari.web.server.dto.request.AestheticEditRequest;
import com.cari.web.server.dto.response.CariResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.service.AestheticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AestheticController {

    @Autowired
    private AestheticService aestheticService;

    @GetMapping("/aesthetic/findForList")
    public Page<Aesthetic> findForList(@RequestParam Map<String, String> filters) {
        return aestheticService.findForList(filters);
    }

    @GetMapping("/aesthetic/findForPage/{urlSlug}")
    public ResponseEntity<Aesthetic> findForPage(@PathVariable String urlSlug) {
        Optional<Aesthetic> aestheticOptional = aestheticService.findForPage(urlSlug);

        if (aestheticOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().body(aestheticOptional.get());
    }

    @GetMapping("/aesthetic/findForEdit/{aesthetic}")
    public ResponseEntity<Aesthetic> findForEdit(@PathVariable int aesthetic) {
        Optional<Aesthetic> rval = aestheticService.findForEdit(aesthetic);

        if (rval.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().body(rval.get());
    }

    @GetMapping("/aesthetic/names")
    public List<Aesthetic> findNames(@RequestParam Optional<String> query) {
        return aestheticService.findNames(query);
    }

    @GetMapping("/aesthetic/findDraft")
    public List<Aesthetic> findDraft() {
        return aestheticService.findDraft();
    }

    @RequestMapping(path = "/aesthetic/edit", method = RequestMethod.POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CariResponse> edit(
            @ModelAttribute AestheticEditRequest aestheticEditRequest) {
        CariResponse response = aestheticService.createOrUpdate(aestheticEditRequest);

        ResponseEntity.BodyBuilder responseBuilder =
                response.getStatus().equals(RequestStatus.SUCCESS) ? ResponseEntity.ok()
                        : ResponseEntity.badRequest();

        return responseBuilder.body(response);
    }
}
