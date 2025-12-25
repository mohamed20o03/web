package com.abdelwahab.CampusCard.domain.moderation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddBannedWordRequest {
    
    @NotBlank(message = "Word is required")
    @Size(min = 1, max = 100, message = "Word must be between 1 and 100 characters")
    private String word;
}

