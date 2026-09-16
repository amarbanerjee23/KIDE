package com.kide.enterprise.context;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

final class EnterpriseContextRules {
    static final int MAX_FILE_BYTES = 64 * 1024;
    static final int MAX_NAME_LENGTH = 128;
    static final int MAX_METADATA_ENTRIES = 64;
    static final int MAX_METADATA_VALUE_LENGTH = 512;
    private static final Pattern METADATA_KEY = Pattern.compile("[A-Za-z0-9][A-Za-z0-9._-]{0,63}");

    private EnterpriseContextRules() {
    }

    static Optional<String> validateName(String value, EnterpriseScope scope) {
        if (value == null || value.trim().isEmpty()) {
            return Optional.of(scope.token() + " display name must not be empty");
        }
        String trimmed = value.trim();
        if (trimmed.length() > MAX_NAME_LENGTH) {
            return Optional.of(scope.token() + " display name exceeds " + MAX_NAME_LENGTH + " characters");
        }
        if (containsUnsafeControl(trimmed)) {
            return Optional.of(scope.token() + " display name contains unsupported control characters");
        }
        return Optional.empty();
    }

    static Optional<String> validateMetadata(Map<String, String> metadata, EnterpriseScope scope) {
        if (metadata == null) {
            return Optional.of(scope.token() + " metadata must not be null");
        }
        if (metadata.size() > MAX_METADATA_ENTRIES) {
            return Optional.of(scope.token() + " metadata exceeds " + MAX_METADATA_ENTRIES + " entries");
        }
        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key == null || !METADATA_KEY.matcher(key).matches()) {
                return Optional.of(scope.token() + " metadata contains an invalid key");
            }
            if (value == null || value.length() > MAX_METADATA_VALUE_LENGTH || containsUnsafeControl(value)) {
                return Optional.of(scope.token() + " metadata value is invalid for key " + key);
            }
        }
        return Optional.empty();
    }

    static boolean containsUnsafeControl(String value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if ((c < 0x20 && c != '\t') || c == 0x7f) {
                return true;
            }
        }
        return false;
    }
}
