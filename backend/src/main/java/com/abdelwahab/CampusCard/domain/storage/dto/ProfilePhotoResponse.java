package com.abdelwahab.CampusCard.domain.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePhotoResponse {
    private String photoUrl;
    private String message;
}
