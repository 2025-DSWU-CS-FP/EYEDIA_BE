package com.eyedia.eyedia.config.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Manager {

    private final AmazonS3 amazonS3;
    private final S3Config s3Config;

    public String uploadFile(String keyName, MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType("image/jpg");

        try {
            amazonS3.putObject(new PutObjectRequest(
                    s3Config.getBucket(), keyName, file.getInputStream(), objectMetadata));
        } catch (IOException e) {
            log.error("❌ S3 파일 업로드 실패", e);
            throw new RuntimeException("S3 파일 업로드 중 오류 발생", e);
        }

        return amazonS3.getUrl(s3Config.getBucket(), keyName).toString();
    }

    // 공백 → 언더스코어 치환
    private String sanitize(String value) {
        return value.replaceAll("\\s+", "_");
    }

    // UUID 짧게 생성
    private String shortUuid() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    // 객체 이미지 S3 Key 생성
    public String generateObjectKeyName(String exhibitionTitle, String paintingTitle) {
        String path = sanitize(exhibitionTitle) + "/" + sanitize(paintingTitle);
        return path + "/" + shortUuid() + ".jpg";
    }

    // 작품 이미지 S3 Key 생성
    public String generatePaintingKeyName(String exhibitionTitle, String paintingTitle) {
        String path = sanitize(exhibitionTitle) + "/" + sanitize(paintingTitle);
        return path + "/" + sanitize(paintingTitle) + ".jpg";
    }

    // 전시 포스터 S3 Key 생성
    public String generateExhibitionKeyName(String exhibitionTitle) {
        String path = sanitize(exhibitionTitle);
        return path + "/" + path + "_" + shortUuid() + ".jpg";
    }
}