package com.patientpal.backend.image.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.net.URL;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresignedUrlService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloudfront.domain}")
    private String cloudFrontDomain;

    private final AmazonS3 amazonS3;

    public String getPresignedUrl(String prefix, String fileName) {
        String onlyOneFileName = onlyOneFileName(fileName);

        if (!prefix.isEmpty()) {
            onlyOneFileName = prefix + "/" + onlyOneFileName;
        }
        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(bucket, onlyOneFileName);
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString();
    }

    public String getCloudFrontUrl(String prefix, String wholeUrl) {
        if (wholeUrl == null) return null;
        String urlWithoutParams = wholeUrl.split("\\?")[0];
        String fileName = urlWithoutParams.substring(urlWithoutParams.lastIndexOf('/') + 1);
        if (!prefix.isEmpty()) {
            fileName = prefix + "/" + fileName;
        }
        return cloudFrontDomain + "/" + fileName + "?h=50";
    }

    private String onlyOneFileName(String filename) {
        return UUID.randomUUID().toString() + filename;
    }

    private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String bucket, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getPresignedUrlExpiration());

        return generatePresignedUrlRequest;
    }

    private Date getPresignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);

        return expiration;
    }
}
