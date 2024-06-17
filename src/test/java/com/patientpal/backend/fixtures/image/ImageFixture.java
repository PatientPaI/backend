package com.patientpal.backend.fixtures.image;

public class ImageFixture {

    public static final String BUCKET = "test-bucket";
    public static final String LOCATION = "us-east-1";
    public static final String FILE_NAME = "test-file.txt";
    public static final String PREFIX = "test-prefix";
    public static final String USE_ONLY_ONE_FILE_NAME = "unique-test-file.txt";
    public static final String PRESIGNED_URL = "https://" + BUCKET + ".s3." + LOCATION + ".amazonaws.com/" + PREFIX + "/" + USE_ONLY_ONE_FILE_NAME;
}
