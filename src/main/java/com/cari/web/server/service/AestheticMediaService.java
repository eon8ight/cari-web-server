package com.cari.web.server.service;

import com.cari.web.server.dto.request.AestheticMediaEditRequest;
import com.cari.web.server.dto.response.CariResponse;

public interface AestheticMediaService {

    CariResponse validateCreateOrUpdateMedia(AestheticMediaEditRequest aestheticEditRequest);

    CariResponse createOrUpdate(AestheticMediaEditRequest aestheticEditRequest);

    CariResponse validateAndCreateOrUpdate(AestheticMediaEditRequest aestheticEditRequest);
}
