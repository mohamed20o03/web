package com.abdelwahab.CampusCard.domain.common.converter;

import com.abdelwahab.CampusCard.domain.profile.model.Profile;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class VisibilityConverter implements AttributeConverter<Profile.Visibility, String> {

    @Override
    public String convertToDatabaseColumn(Profile.Visibility visibility) {
        if (visibility == null) {
            return null;
        }
        return visibility.getValue();
    }

    @Override
    public Profile.Visibility convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        return switch (dbData.toLowerCase()) {
            case "public" -> Profile.Visibility.PUBLIC;
            case "students_only" -> Profile.Visibility.STUDENTS_ONLY;
            case "private" -> Profile.Visibility.PRIVATE;
            default -> throw new IllegalArgumentException("Unknown visibility value: " + dbData);
        };
    }
}
