package com.cari.web.server.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.cari.web.server.dto.response.S3PutResponse;
import com.cari.web.server.service.S3Service;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class S3ServiceImpl implements S3Service {

    @Value("${aws.s3.bucket.name}")
    private String s3BucketName;

    @Autowired
    private AmazonS3 s3;

    public S3PutResponse upload(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType.equals(MediaType.APPLICATION_OCTET_STREAM_VALUE)) {
            Tika tika = new Tika();

            try (FileInputStream in = (FileInputStream) file.getInputStream()) {
                contentType = tika.detect(in);
            } catch (IOException ex) {
                return S3PutResponse.failure(ex.getLocalizedMessage());
            }
        }

        MimeType mimeType;

        try {
            mimeType = MimeTypes.getDefaultMimeTypes().forName(contentType);
        } catch (MimeTypeException ex) {
            return S3PutResponse.failure(ex.getLocalizedMessage());
        }

        UUID s3KeyUuid = UUID.randomUUID();
        String s3Key = s3KeyUuid.toString().replace("-", "") + mimeType.getExtension();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(mimeType.getName());

        S3PutResponse res;

        try (FileInputStream in = (FileInputStream) file.getInputStream()) {
            PutObjectResult s3Res = s3.putObject(s3BucketName, s3Key, in, metadata);
            res = S3PutResponse.success(s3Res, s3Key);
        } catch (SdkClientException | IOException ex) {
            res = S3PutResponse.failure(ex.getLocalizedMessage());
        }

        return res;
    }
}
