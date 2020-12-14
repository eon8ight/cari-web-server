package com.cari.web.server.service;

import java.io.File;
import com.cari.web.server.dto.FileUploadResult;

public interface FileService {

    FileUploadResult upload(File file, int pkFileType);
}
