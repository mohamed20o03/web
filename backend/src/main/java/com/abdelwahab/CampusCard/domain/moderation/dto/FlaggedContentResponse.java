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
public class FlaggedContentResponse {
    private Integer id;
    private Integer userId;
    private String userEmail;
    private String userName;
    private String content;
    private LocalDateTime flaggedAt;
}

