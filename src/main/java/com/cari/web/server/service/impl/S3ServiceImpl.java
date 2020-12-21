package com.cari.web.server.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.cari.web.server.dto.response.S3PutResponse;
import com.cari.web.server.service.S3Service;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3ServiceImpl implements S3Service {

    @Value("${aws.s3.bucket.name}")
    private String s3BucketName;

    @Autowired
    private AmazonS3 s3;

    @Override
    public String getUrlPrefix() {
        return "https://" + s3BucketName + ".s3.amazonaws.com/";
    }

    public S3PutResponse upload(File file) {
        Tika tika = new Tika();
        String contentType;

        try {
            contentType = tika.detect(file);
        } catch (IOException ex) {
            return S3PutResponse.failure(ex.getLocalizedMessage());
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
        metadata.setContentLength(file.length());
        metadata.setContentType(mimeType.getName());

        PutObjectRequest s3Req = new PutObjectRequest(s3BucketName, s3Key, file);
        s3Req.setMetadata(metadata);

        PutObjectResult s3Res = s3.putObject(s3Req);
        return S3PutResponse.success(s3Res, s3Key);
    }

    @Override
    public void delete(String key) {
        s3.deleteObject(s3BucketName, key);
    }
}
