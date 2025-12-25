package com.abdelwahab.CampusCard.domain.common.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

// Makes this annotation appear in generated JavaDocs
@Documented

// Tells Bean Validation that this is a custom constraint
// and links it to the validator class that contains the logic
@Constraint(
    validatedBy = com.abdelwahab.CampusCard.domain.common.validation.validator.PsuEmailValidator.class
)

// Specifies where this annotation can be used
// Here: only on class fields (e.g. DTO fields)
@Target(ElementType.FIELD)

// Keeps this annotation available at runtime
// so Spring can read it using reflection
@Retention(RetentionPolicy.RUNTIME)

// Declares a custom validation annotation
public @interface PsuEmail {

    // Default error message when validation fails
    String message() default "Email must end with @eng.psu.edu.eg";

    // Validation groups (used for different validation scenarios)
    // Usually not needed in most projects
    Class<?>[] groups() default {};

    // Additional metadata for advanced use cases
    // Rarely used, but required by Bean Validation specification
    Class<? extends Payload>[] payload() default {};
}
