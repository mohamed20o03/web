package com.abdelwahab.CampusCard.domain.common.converter;

import com.abdelwahab.CampusCard.domain.user.model.User;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<User.Status, String> {

    @Override
    public String convertToDatabaseColumn(User.Status status) {
        if (status == null) {
            return null;
        }
        return status.getValue();
    }

    @Override
    public User.Status convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (User.Status status : User.Status.values()) {
            if (status.getValue().equals(dbData)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Unknown status: " + dbData);
    }
}
