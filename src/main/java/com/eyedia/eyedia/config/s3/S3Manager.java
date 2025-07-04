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

@Slf4j @Component
@RequiredArgsConstructor
public class S3Manager {
    private final AmazonS3 amazonS3;
    private final S3Config s3Config;

    public String uploadFile(String keyName, MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(
                    new PutObjectRequest(s3Config.getBucket(), keyName, file.getInputStream(), objectMetadata));
        } catch (IOException e) {
            log.error("S3 파일 업로드 실패 : {}", (Object) e.getStackTrace());
        }
        return amazonS3.getUrl(s3Config.getBucket(), keyName).toString();
    }

    // 객체 사진 저장은 다음 url을 통해 저장
    public String generateObjectKeyName(String exhibitionTitle, String paintingTitle) {
        final String uuid = UUID.randomUUID().toString();
        return exhibitionTitle + '/' + paintingTitle + '/'  + uuid;
    }

    // 작품 사진 저장은 다음 url을 통해 저장
    public String generatePaintingKeyName(String exhibitionTitle, String paintingTitle) {
        final String uuid = UUID.randomUUID().toString();
        return exhibitionTitle + '/' + paintingTitle + '/' + paintingTitle + '_' + uuid;
    }

    // 전시회 포스터 저장은 다음 url을 통해 저장
    public String generateExhibitionKeyName(String exhibitionTitle) {
        final String uuid = UUID.randomUUID().toString();
        return exhibitionTitle + '/' + exhibitionTitle + '_' + uuid;
    }
}
