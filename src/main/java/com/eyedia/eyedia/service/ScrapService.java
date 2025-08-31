package com.eyedia.eyedia.service;

import com.eyedia.eyedia.domain.Painting;
import com.eyedia.eyedia.dto.ScrapRequestDto;
import com.eyedia.eyedia.domain.Scrap;
import com.eyedia.eyedia.repository.PaintingRepository;
import com.eyedia.eyedia.repository.ScrapRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.eyedia.eyedia.dto.ScrapResponseDto;

import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final PaintingRepository paintingRepository;

    public Scrap saveScrap(ScrapRequestDto dto, Long userId) {
        Scrap scrap = Scrap.builder()
                .userId(userId)
                .paintingId(dto.getPaintingId())
                .date(LocalDate.parse(dto.getDate()))
                .excerpt(dto.getExcerpt())
                .location(dto.getLocation())
                .artist(dto.getArtist())
                .createdAt(LocalDate.now())
                .build();

        return scrapRepository.save(scrap);
    }

    public List<ScrapResponseDto> getScrapList() {
        return scrapRepository.findAll().stream()
                .map(scrap -> ScrapResponseDto.builder()
                        .id(scrap.getId())
                        .userId(scrap.getUserId())
                        .paintingId(scrap.getPaintingId())
                        .date(scrap.getDate().toString())
                        .excerpt(scrap.getExcerpt())
                        .location(scrap.getLocation())
                        .artist(scrap.getArtist())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ScrapResponseDto> getScrapListByUserId(Long userId) {
        List<Scrap> scraps = scrapRepository.findByUserId(userId);

        return scraps.stream()
                .map(scrap -> {
                    // 🔍 paintingId로 imageUrl 조회
                    String imageUrl = paintingRepository.findById(scrap.getPaintingId())
                            .map(Painting::getImageUrl)
                            .orElse(null); // 또는 기본 이미지 URL

                    return ScrapResponseDto.builder()
                            .id(scrap.getId())
                            .userId(scrap.getUserId())
                            .paintingId(scrap.getPaintingId())
                            .date(scrap.getDate().toString())
                            .excerpt(scrap.getExcerpt())
                            .location(scrap.getLocation())
                            .artist(scrap.getArtist())
                            .imageUrl(imageUrl)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<ScrapResponseDto> getScrapListByUserAndLocation(Long userId, String location) {
        List<Scrap> scraps = scrapRepository.findByUserIdAndLocation(userId, location);

        return scraps.stream()
                .map(scrap -> {
                    String imageUrl = paintingRepository.findById(scrap.getPaintingId())
                            .map(Painting::getImageUrl)
                            .orElse(null);

                    return ScrapResponseDto.builder()
                            .id(scrap.getId())
                            .userId(scrap.getUserId())
                            .paintingId(scrap.getPaintingId())
                            .date(scrap.getDate().toString())
                            .excerpt(scrap.getExcerpt())
                            .location(scrap.getLocation())
                            .artist(scrap.getArtist())
                            .imageUrl(imageUrl)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Page<ScrapResponseDto> getScrapsByUser(Long userId, Pageable pageable) {
        Page<Scrap> scraps = scrapRepository.findByUserId(userId, pageable);

        return scraps.map(scrap -> {
            String imageUrl = paintingRepository.findById(scrap.getPaintingId())
                    .map(Painting::getImageUrl)
                    .orElse(null);
            String title = paintingRepository.findById(scrap.getPaintingId())
                    .map(Painting::getTitle)
                    .orElse(null);

            return ScrapResponseDto.builder()
                    .id(scrap.getId())
                    .userId(scrap.getUserId())
                    .paintingId(scrap.getPaintingId())
                    .date(scrap.getDate().toString()) // LocalDate → String
                    .excerpt(scrap.getExcerpt())
                    .location(scrap.getLocation())
                    .artist(scrap.getArtist())
                    .imageUrl(imageUrl)
                    .title(title)
                    .build();
        });
    }




}