package org.sunday.projectpop.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

// 커스텀 어노테이션
@Constraint(validatedBy = FileValidation.FileValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FileValidation {

    String message() default "유효하지 않은 파일 형식 또는 크기입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // Inner validator class
    class FileValidator implements ConstraintValidator<FileValidation, List<MultipartFile>> {
        // 허용 파일 Type (MIME types)
        private static final List<String> ALLOWED_MIME_TYPES = List.of(
                "application/pdf",
                "text/markdown",
                "text/csv",
                "text/plain",
                "application/msword", // .doc
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
                "application/vnd.ms-excel", // .xls
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
                "application/vnd.ms-powerpoint", // .ppt
                "application/vnd.openxmlformats-officedocument.presentationml.presentation" // .pptx
        );
        // MAX 파일 사이즈 (50MB)
        private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

        @Override
        public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
            // 빈 파일 체크
            if (files == null || files.isEmpty()) {
                return true;
            }

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                // 파일 크기 체크
                if (file.getSize() > MAX_FILE_SIZE) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("파일 크기는 50MB를 초과할 수 없습니다: " + file.getOriginalFilename())
                            .addConstraintViolation();
                    return false;
                }

                // 파일 형식 체크
                String contentType = file.getContentType();
                if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("지원되지 않는 파일 형식입니다: " + file.getOriginalFilename())
                            .addConstraintViolation();
                    return false;
                }
            }
            return true;
        }
    }
}
