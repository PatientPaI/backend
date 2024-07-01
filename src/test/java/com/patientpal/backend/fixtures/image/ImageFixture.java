package com.patientpal.backend.fixtures.image;

public class ImageFixture {

    public static final String BUCKET = "patientpal-s3";
    public static final String LOCATION = "ap-northeast-2";
    public static final String FILE_NAME = "test-file.jpg";
    public static final String PROFILE_PREFIX = "profiles";
    public static final String PRESIGNED_URL = "https://" + BUCKET + ".s3." + LOCATION + ".amazonaws.com/" + PROFILE_PREFIX + "/" + FILE_NAME;
    public static final String CLOUD_FRONT_URL = "https://cloudfront" + "/" + PROFILE_PREFIX + "/" + FILE_NAME;
}
