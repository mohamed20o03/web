package com.abdelwahab.CampusCard.domain.moderation.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannedWordResponse {
    private Integer id;
    private String word;
    private LocalDateTime addedAt;
}

