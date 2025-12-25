#!/bin/bash

# CampusCard Backend Restructuring Script
# This script reorganizes the flat package structure into domain-driven design

set -e  # Exit on any error

BASE_DIR="/home/eima40x4c/Projects/web/CampusCard/src/main/java/com/abdelwahab/CampusCard"
cd "$BASE_DIR"

echo "Starting backend restructuring to domain-driven architecture..."

# Function to move file and update package declaration
move_and_update() {
    local src=$1
    local dest=$2
    local old_package=$3
    local new_package=$4
    
    if [ -f "$src" ]; then
        echo "Moving $src to $dest"
        cp "$src" "$dest"
        
        # Update package declaration in the new file
        sed -i "s|package $old_package;|package $new_package;|g" "$dest"
        
        # Mark original for deletion (we'll do this after updating all imports)
        echo "$src" >> /tmp/files_to_delete.txt
    fi
}

# Clear the deletion list
> /tmp/files_to_delete.txt

echo "Step 1: Moving AUTH domain files..."
# Auth controllers
move_and_update "controller/LoginController.java" "domain/auth/controller/LoginController.java" \
    "com.abdelwahab.CampusCard.controller" "com.abdelwahab.CampusCard.domain.auth.controller"

move_and_update "controller/SignUpController.java" "domain/auth/controller/SignUpController.java" \
    "com.abdelwahab.CampusCard.controller" "com.abdelwahab.CampusCard.domain.auth.controller"

# Auth services
move_and_update "service/LoginService.java" "domain/auth/service/LoginService.java" \
    "com.abdelwahab.CampusCard.service" "com.abdelwahab.CampusCard.domain.auth.service"

move_and_update "service/SignUpService.java" "domain/auth/service/SignUpService.java" \
    "com.abdelwahab.CampusCard.service" "com.abdelwahab.CampusCard.domain.auth.service"

move_and_update "service/EmailService.java" "domain/auth/service/EmailService.java" \
    "com.abdelwahab.CampusCard.service" "com.abdelwahab.CampusCard.domain.auth.service"

# Auth DTOs
move_and_update "dto/LoginRequest.java" "domain/auth/dto/LoginRequest.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.auth.dto"

move_and_update "dto/LoginResponse.java" "domain/auth/dto/LoginResponse.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.auth.dto"

move_and_update "dto/SignUpRequest.java" "domain/auth/dto/SignUpRequest.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.auth.dto"

move_and_update "dto/SignUpResponse.java" "domain/auth/dto/SignUpResponse.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.auth.dto"

move_and_update "dto/EmailVerificationRequest.java" "domain/auth/dto/EmailVerificationRequest.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.auth.dto"

echo "Step 2: Moving USER domain files..."
# User model and repository
move_and_update "model/User.java" "domain/user/model/User.java" \
    "com.abdelwahab.CampusCard.model" "com.abdelwahab.CampusCard.domain.user.model"

move_and_update "repository/UserRepository.java" "domain/user/repository/UserRepository.java" \
    "com.abdelwahab.CampusCard.repository" "com.abdelwahab.CampusCard.domain.user.repository"

echo "Step 3: Moving PROFILE domain files..."
# Profile controller
move_and_update "controller/ProfileController.java" "domain/profile/controller/ProfileController.java" \
    "com.abdelwahab.CampusCard.controller" "com.abdelwahab.CampusCard.domain.profile.controller"

# Profile service
move_and_update "service/ProfileService.java" "domain/profile/service/ProfileService.java" \
    "com.abdelwahab.CampusCard.service" "com.abdelwahab.CampusCard.domain.profile.service"

# Profile model and repository
move_and_update "model/Profile.java" "domain/profile/model/Profile.java" \
    "com.abdelwahab.CampusCard.model" "com.abdelwahab.CampusCard.domain.profile.model"

move_and_update "repository/ProfileRepository.java" "domain/profile/repository/ProfileRepository.java" \
    "com.abdelwahab.CampusCard.repository" "com.abdelwahab.CampusCard.domain.profile.repository"

# Profile DTOs
move_and_update "dto/ProfileResponse.java" "domain/profile/dto/ProfileResponse.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.profile.dto"

move_and_update "dto/UpdateProfileRequest.java" "domain/profile/dto/UpdateProfileRequest.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.profile.dto"

move_and_update "dto/UpdateVisibilityRequest.java" "domain/profile/dto/UpdateVisibilityRequest.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.profile.dto"

echo "Step 4: Moving ADMIN domain files..."
# Admin controller
move_and_update "controller/AdminController.java" "domain/admin/controller/AdminController.java" \
    "com.abdelwahab.CampusCard.controller" "com.abdelwahab.CampusCard.domain.admin.controller"

# Admin service
move_and_update "service/AdminService.java" "domain/admin/service/AdminService.java" \
    "com.abdelwahab.CampusCard.service" "com.abdelwahab.CampusCard.domain.admin.service"

# Admin DTOs
move_and_update "dto/AdminDashboardStats.java" "domain/admin/dto/AdminDashboardStats.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.admin.dto"

move_and_update "dto/UserApprovalResponse.java" "domain/admin/dto/UserApprovalResponse.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.admin.dto"

move_and_update "dto/ApprovalDecisionRequest.java" "domain/admin/dto/ApprovalDecisionRequest.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.admin.dto"

move_and_update "dto/ChangeRoleRequest.java" "domain/admin/dto/ChangeRoleRequest.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.admin.dto"

echo "Step 5: Moving MODERATION domain files..."
# Moderation service
move_and_update "service/ContentModerationService.java" "domain/moderation/service/ContentModerationService.java" \
    "com.abdelwahab.CampusCard.service" "com.abdelwahab.CampusCard.domain.moderation.service"

# Moderation models
move_and_update "model/BannedWord.java" "domain/moderation/model/BannedWord.java" \
    "com.abdelwahab.CampusCard.model" "com.abdelwahab.CampusCard.domain.moderation.model"

move_and_update "model/FlaggedContent.java" "domain/moderation/model/FlaggedContent.java" \
    "com.abdelwahab.CampusCard.model" "com.abdelwahab.CampusCard.domain.moderation.model"

# Moderation repositories
move_and_update "repository/BannedWordRepository.java" "domain/moderation/repository/BannedWordRepository.java" \
    "com.abdelwahab.CampusCard.repository" "com.abdelwahab.CampusCard.domain.moderation.repository"

move_and_update "repository/FlaggedContentRepository.java" "domain/moderation/repository/FlaggedContentRepository.java" \
    "com.abdelwahab.CampusCard.repository" "com.abdelwahab.CampusCard.domain.moderation.repository"

# Moderation DTOs
move_and_update "dto/BannedWordResponse.java" "domain/moderation/dto/BannedWordResponse.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.moderation.dto"

move_and_update "dto/AddBannedWordRequest.java" "domain/moderation/dto/AddBannedWordRequest.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.moderation.dto"

move_and_update "dto/FlaggedContentResponse.java" "domain/moderation/dto/FlaggedContentResponse.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.moderation.dto"

echo "Step 6: Moving STORAGE domain files..."
# Storage service
move_and_update "service/MinioService.java" "domain/storage/service/MinioService.java" \
    "com.abdelwahab.CampusCard.service" "com.abdelwahab.CampusCard.domain.storage.service"

# Storage DTOs
move_and_update "dto/ProfilePhotoResponse.java" "domain/storage/dto/ProfilePhotoResponse.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.storage.dto"

move_and_update "dto/NationalIdScanResponse.java" "domain/storage/dto/NationalIdScanResponse.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.storage.dto"

echo "Step 7: Moving ACADEMIC domain files..."
# Academic models
move_and_update "model/Faculty.java" "domain/academic/model/Faculty.java" \
    "com.abdelwahab.CampusCard.model" "com.abdelwahab.CampusCard.domain.academic.model"

move_and_update "model/Department.java" "domain/academic/model/Department.java" \
    "com.abdelwahab.CampusCard.model" "com.abdelwahab.CampusCard.domain.academic.model"

# Academic repositories
move_and_update "repository/FacultyRepository.java" "domain/academic/repository/FacultyRepository.java" \
    "com.abdelwahab.CampusCard.repository" "com.abdelwahab.CampusCard.domain.academic.repository"

move_and_update "repository/DepartmentRepository.java" "domain/academic/repository/DepartmentRepository.java" \
    "com.abdelwahab.CampusCard.repository" "com.abdelwahab.CampusCard.domain.academic.repository"

# Academic DTOs
move_and_update "dto/FacultyResponse.java" "domain/academic/dto/FacultyResponse.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.academic.dto"

move_and_update "dto/DepartmentResponse.java" "domain/academic/dto/DepartmentResponse.java" \
    "com.abdelwahab.CampusCard.dto" "com.abdelwahab.CampusCard.domain.academic.dto"

echo "Step 8: Moving PUBLIC controller..."
# Public controller (could go in a 'public' domain or stay in root)
move_and_update "controller/PublicController.java" "domain/profile/controller/PublicController.java" \
    "com.abdelwahab.CampusCard.controller" "com.abdelwahab.CampusCard.domain.profile.controller"

move_and_update "service/PublicService.java" "domain/profile/service/PublicService.java" \
    "com.abdelwahab.CampusCard.service" "com.abdelwahab.CampusCard.domain.profile.service"

echo "Step 9: Moving COMMON domain files..."
# Config files
move_and_update "config/SecurityConfig.java" "domain/common/config/SecurityConfig.java" \
    "com.abdelwahab.CampusCard.config" "com.abdelwahab.CampusCard.domain.common.config"

move_and_update "config/MinioConfig.java" "domain/common/config/MinioConfig.java" \
    "com.abdelwahab.CampusCard.config" "com.abdelwahab.CampusCard.domain.common.config"

move_and_update "config/RateLimitConfig.java" "domain/common/config/RateLimitConfig.java" \
    "com.abdelwahab.CampusCard.config" "com.abdelwahab.CampusCard.domain.common.config"

move_and_update "config/AdminUserInitializer.java" "domain/common/config/AdminUserInitializer.java" \
    "com.abdelwahab.CampusCard.config" "com.abdelwahab.CampusCard.domain.common.config"

# Security files
move_and_update "security/JwtService.java" "domain/common/security/JwtService.java" \
    "com.abdelwahab.CampusCard.security" "com.abdelwahab.CampusCard.domain.common.security"

move_and_update "security/JwtAuthenticationFilter.java" "domain/common/security/JwtAuthenticationFilter.java" \
    "com.abdelwahab.CampusCard.security" "com.abdelwahab.CampusCard.domain.common.security"

move_and_update "security/RateLimitInterceptor.java" "domain/common/security/RateLimitInterceptor.java" \
    "com.abdelwahab.CampusCard.security" "com.abdelwahab.CampusCard.domain.common.security"

# Converters
move_and_update "converter/RoleConverter.java" "domain/common/converter/RoleConverter.java" \
    "com.abdelwahab.CampusCard.converter" "com.abdelwahab.CampusCard.domain.common.converter"

move_and_update "converter/StatusConverter.java" "domain/common/converter/StatusConverter.java" \
    "com.abdelwahab.CampusCard.converter" "com.abdelwahab.CampusCard.domain.common.converter"

move_and_update "converter/VisibilityConverter.java" "domain/common/converter/VisibilityConverter.java" \
    "com.abdelwahab.CampusCard.converter" "com.abdelwahab.CampusCard.domain.common.converter"

# Validation
move_and_update "validation/annotation/PsuEmail.java" "domain/common/validation/annotation/PsuEmail.java" \
    "com.abdelwahab.CampusCard.validation.annotation" "com.abdelwahab.CampusCard.domain.common.validation.annotation"

move_and_update "validation/validator/PsuEmailValidator.java" "domain/common/validation/validator/PsuEmailValidator.java" \
    "com.abdelwahab.CampusCard.validation.validator" "com.abdelwahab.CampusCard.domain.common.validation.validator"

# Exception handler
move_and_update "exception/GlobalExceptionHandler.java" "domain/common/exception/GlobalExceptionHandler.java" \
    "com.abdelwahab.CampusCard.exception" "com.abdelwahab.CampusCard.domain.common.exception"

echo ""
echo "Step 10: Updating imports in all new files..."

# Update imports in all domain files
find domain -type f -name "*.java" -exec sed -i \
    -e 's|com\.abdelwahab\.CampusCard\.controller\.|com.abdelwahab.CampusCard.domain.auth.controller.|g' \
    -e 's|com\.abdelwahab\.CampusCard\.service\.LoginService|com.abdelwahab.CampusCard.domain.auth.service.LoginService|g' \
    -e 's|com\.abdelwahab\.CampusCard\.service\.SignUpService|com.abdelwahab.CampusCard.domain.auth.service.SignUpService|g' \
    -e 's|com\.abdelwahab\.CampusCard\.service\.EmailService|com.abdelwahab.CampusCard.domain.auth.service.EmailService|g' \
    -e 's|com\.abdelwahab\.CampusCard\.service\.ProfileService|com.abdelwahab.CampusCard.domain.profile.service.ProfileService|g' \
    -e 's|com\.abdelwahab\.CampusCard\.service\.PublicService|com.abdelwahab.CampusCard.domain.profile.service.PublicService|g' \
    -e 's|com\.abdelwahab\.CampusCard\.service\.AdminService|com.abdelwahab.CampusCard.domain.admin.service.AdminService|g' \
    -e 's|com\.abdelwahab\.CampusCard\.service\.ContentModerationService|com.abdelwahab.CampusCard.domain.moderation.service.ContentModerationService|g' \
    -e 's|com\.abdelwahab\.CampusCard\.service\.MinioService|com.abdelwahab.CampusCard.domain.storage.service.MinioService|g' \
    -e 's|com\.abdelwahab\.CampusCard\.model\.User|com.abdelwahab.CampusCard.domain.user.model.User|g' \
    -e 's|com\.abdelwahab\.CampusCard\.model\.Profile|com.abdelwahab.CampusCard.domain.profile.model.Profile|g' \
    -e 's|com\.abdelwahab\.CampusCard\.model\.Faculty|com.abdelwahab.CampusCard.domain.academic.model.Faculty|g' \
    -e 's|com\.abdelwahab\.CampusCard\.model\.Department|com.abdelwahab.CampusCard.domain.academic.model.Department|g' \
    -e 's|com\.abdelwahab\.CampusCard\.model\.BannedWord|com.abdelwahab.CampusCard.domain.moderation.model.BannedWord|g' \
    -e 's|com\.abdelwahab\.CampusCard\.model\.FlaggedContent|com.abdelwahab.CampusCard.domain.moderation.model.FlaggedContent|g' \
    -e 's|com\.abdelwahab\.CampusCard\.repository\.UserRepository|com.abdelwahab.CampusCard.domain.user.repository.UserRepository|g' \
    -e 's|com\.abdelwahab\.CampusCard\.repository\.ProfileRepository|com.abdelwahab.CampusCard.domain.profile.repository.ProfileRepository|g' \
    -e 's|com\.abdelwahab\.CampusCard\.repository\.FacultyRepository|com.abdelwahab.CampusCard.domain.academic.repository.FacultyRepository|g' \
    -e 's|com\.abdelwahab\.CampusCard\.repository\.DepartmentRepository|com.abdelwahab.CampusCard.domain.academic.repository.DepartmentRepository|g' \
    -e 's|com\.abdelwahab\.CampusCard\.repository\.BannedWordRepository|com.abdelwahab.CampusCard.domain.moderation.repository.BannedWordRepository|g' \
    -e 's|com\.abdelwahab\.CampusCard\.repository\.FlaggedContentRepository|com.abdelwahab.CampusCard.domain.moderation.repository.FlaggedContentRepository|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.LoginRequest|com.abdelwahab.CampusCard.domain.auth.dto.LoginRequest|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.LoginResponse|com.abdelwahab.CampusCard.domain.auth.dto.LoginResponse|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.SignUpRequest|com.abdelwahab.CampusCard.domain.auth.dto.SignUpRequest|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.SignUpResponse|com.abdelwahab.CampusCard.domain.auth.dto.SignUpResponse|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.EmailVerificationRequest|com.abdelwahab.CampusCard.domain.auth.dto.EmailVerificationRequest|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.ProfileResponse|com.abdelwahab.CampusCard.domain.profile.dto.ProfileResponse|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.UpdateProfileRequest|com.abdelwahab.CampusCard.domain.profile.dto.UpdateProfileRequest|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.UpdateVisibilityRequest|com.abdelwahab.CampusCard.domain.profile.dto.UpdateVisibilityRequest|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.AdminDashboardStats|com.abdelwahab.CampusCard.domain.admin.dto.AdminDashboardStats|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.UserApprovalResponse|com.abdelwahab.CampusCard.domain.admin.dto.UserApprovalResponse|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.ApprovalDecisionRequest|com.abdelwahab.CampusCard.domain.admin.dto.ApprovalDecisionRequest|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.ChangeRoleRequest|com.abdelwahab.CampusCard.domain.admin.dto.ChangeRoleRequest|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.BannedWordResponse|com.abdelwahab.CampusCard.domain.moderation.dto.BannedWordResponse|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.AddBannedWordRequest|com.abdelwahab.CampusCard.domain.moderation.dto.AddBannedWordRequest|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.FlaggedContentResponse|com.abdelwahab.CampusCard.domain.moderation.dto.FlaggedContentResponse|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.ProfilePhotoResponse|com.abdelwahab.CampusCard.domain.storage.dto.ProfilePhotoResponse|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.NationalIdScanResponse|com.abdelwahab.CampusCard.domain.storage.dto.NationalIdScanResponse|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.FacultyResponse|com.abdelwahab.CampusCard.domain.academic.dto.FacultyResponse|g' \
    -e 's|com\.abdelwahab\.CampusCard\.dto\.DepartmentResponse|com.abdelwahab.CampusCard.domain.academic.dto.DepartmentResponse|g' \
    -e 's|com\.abdelwahab\.CampusCard\.config\.|com.abdelwahab.CampusCard.domain.common.config.|g' \
    -e 's|com\.abdelwahab\.CampusCard\.security\.JwtService|com.abdelwahab.CampusCard.domain.common.security.JwtService|g' \
    -e 's|com\.abdelwahab\.CampusCard\.security\.JwtAuthenticationFilter|com.abdelwahab.CampusCard.domain.common.security.JwtAuthenticationFilter|g' \
    -e 's|com\.abdelwahab\.CampusCard\.security\.RateLimitInterceptor|com.abdelwahab.CampusCard.domain.common.security.RateLimitInterceptor|g' \
    -e 's|com\.abdelwahab\.CampusCard\.converter\.|com.abdelwahab.CampusCard.domain.common.converter.|g' \
    -e 's|com\.abdelwahab\.CampusCard\.validation\.annotation\.|com.abdelwahab.CampusCard.domain.common.validation.annotation.|g' \
    -e 's|com\.abdelwahab\.CampusCard\.validation\.validator\.|com.abdelwahab.CampusCard.domain.common.validation.validator.|g' \
    -e 's|com\.abdelwahab\.CampusCard\.exception\.|com.abdelwahab.CampusCard.domain.common.exception.|g' \
    {} \;

echo "Step 11: Removing old directory structure..."
# Delete old files
while read -r file; do
    if [ -f "$file" ]; then
        rm "$file"
        echo "Deleted $file"
    fi
done < /tmp/files_to_delete.txt

# Remove empty directories
rmdir controller 2>/dev/null || true
rmdir service 2>/dev/null || true
rmdir repository 2>/dev/null || true
rmdir dto 2>/dev/null || true
rmdir model 2>/dev/null || true
rmdir converter 2>/dev/null || true
rmdir validation/annotation 2>/dev/null || true
rmdir validation/validator 2>/dev/null || true
rmdir validation 2>/dev/null || true
rmdir security 2>/dev/null || true
rmdir exception 2>/dev/null || true
rmdir config 2>/dev/null || true

echo ""
echo "✅ Backend restructuring complete!"
echo ""
echo "New domain structure:"
echo "  domain/"
echo "    ├── auth/          (Login, SignUp, Email verification)"
echo "    ├── user/          (User model, repository)"
echo "    ├── profile/       (Profile management)"
echo "    ├── admin/         (Admin dashboard, approval workflow)"
echo "    ├── moderation/    (Content moderation, banned words)"
echo "    ├── storage/       (File upload, MinIO)"
echo "    ├── academic/      (Faculty, Department)"
echo "    └── common/        (Shared config, security, validation)"
echo ""
echo "Next steps:"
echo "1. Compile the project: ./mvnw clean compile"
echo "2. Run tests: ./mvnw test"
echo "3. Fix any remaining import issues"
