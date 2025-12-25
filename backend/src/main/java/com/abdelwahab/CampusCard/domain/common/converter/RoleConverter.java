package com.abdelwahab.CampusCard.domain.common.converter;

import com.abdelwahab.CampusCard.domain.user.model.User;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<User.Role, String> {

    @Override
    public String convertToDatabaseColumn(User.Role role) {
        if (role == null) {
            return null;
        }
        return role.getValue();
    }

    @Override
    public User.Role convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (User.Role role : User.Role.values()) {
            if (role.getValue().equals(dbData)) {
                return role;
            }
        }
        
        throw new IllegalArgumentException("Unknown role: " + dbData);
    }
}
