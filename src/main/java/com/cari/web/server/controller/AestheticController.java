package com.cari.web.server.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.cari.web.server.domain.db.Aesthetic;
import com.cari.web.server.dto.response.EditResponse;
import com.cari.web.server.dto.response.arena.ArenaApiResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.service.AestheticService;
import com.cari.web.server.service.ArenaApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AestheticController {

    @Autowired
    private AestheticService aestheticService;

    @Autowired
    private ArenaApiService arenaApiService;

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

        Aesthetic aesthetic = aestheticOptional.get();
        String mediaSourceUrl = aesthetic.getMediaSourceUrl();

        if (mediaSourceUrl != null) {
            Optional<ArenaApiResponse> galleryContent =
                    arenaApiService.findBlocksForPagination(mediaSourceUrl, 1);

            if (galleryContent.isPresent()) {
                aesthetic.setGalleryContent(galleryContent.get());
            }
        }

        return ResponseEntity.ok().body(aesthetic);
    }

    @GetMapping("/aesthetic/findForEdit/{aesthetic}")
    public ResponseEntity<Aesthetic> findForEdit(@PathVariable int aesthetic) {
        Optional<Aesthetic> rval = aestheticService.findForEdit(aesthetic);

        if (rval.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().body(rval.get());
    }

    @GetMapping("/aesthetic/findGalleryContent/{aesthetic}")
    public ResponseEntity<ArenaApiResponse> findGalleryContent(@PathVariable int aesthetic,
            @RequestParam int page) {
        Optional<Aesthetic> aestheticOptional = aestheticService.find(aesthetic);

        if (aestheticOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Aesthetic aestheticObj = aestheticOptional.get();
        String mediaSourceUrlOptional = aestheticObj.getMediaSourceUrl();

        if (mediaSourceUrlOptional == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Optional<ArenaApiResponse> arenaApiResponse =
                arenaApiService.findBlocksForPagination(mediaSourceUrlOptional, page);

        if (arenaApiResponse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok().body(arenaApiResponse.get());
    }

    @GetMapping("/aesthetic/names")
    public List<Aesthetic> findNames(@RequestParam Optional<String> query) {
        return aestheticService.findNames(query);
    }

    @PutMapping("/aesthetic/edit")
    public ResponseEntity<EditResponse> edit(@RequestBody Aesthetic aesthetic) {
        EditResponse response = aestheticService.createOrUpdate(aesthetic);

        ResponseEntity.BodyBuilder responseBuilder =
                response.getStatus().equals(RequestStatus.SUCCESS) ? ResponseEntity.ok()
                        : ResponseEntity.badRequest();

        return responseBuilder.body(response);
    }
}
