package com.cari.web.server.util;

import java.io.File;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public final class FileUtils {

    private static final String TMP_FILE_PREFIX = "cari-";
    private static final String TMP_FILE_SUFFIX = ".tmp";

    public static File transferToTmp(MultipartFile multipartFile) throws IOException {
        File tmpFile = File.createTempFile(TMP_FILE_PREFIX, TMP_FILE_SUFFIX);
        multipartFile.transferTo(tmpFile);
        return tmpFile;
    }
}
