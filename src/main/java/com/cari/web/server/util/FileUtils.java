package com.cari.web.server.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.springframework.web.multipart.MultipartFile;

public final class FileUtils {

    private static final String TMP_FILE_PREFIX = "cari-";
    private static final String MULTIPART_PREFIX = "multipart-";
    private static final String CLONED_PREFIX = "cloned-";
    private static final String TMP_FILE_SUFFIX = ".tmp";

    public static File transferToTmp(MultipartFile multipartFile) throws IOException {
        File tmpFile = File.createTempFile(TMP_FILE_PREFIX + MULTIPART_PREFIX, TMP_FILE_SUFFIX);
        multipartFile.transferTo(tmpFile);
        return tmpFile;
    }

    public static File cloneFile(File file) throws IOException {
        File tmpFile = File.createTempFile(TMP_FILE_PREFIX + CLONED_PREFIX, TMP_FILE_SUFFIX);
        Files.copy(file.toPath(), tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return tmpFile;
    }
}
