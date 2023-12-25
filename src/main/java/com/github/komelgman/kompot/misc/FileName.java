package com.github.komelgman.kompot.misc;

import java.text.Normalizer;

public final class FileName {
    private FileName() {
    }

    public static String sanitizeFilename(String filename) {
        // Remove any non-alphanumeric characters except for dot (.), hyphen (-), and underscore (_)
        String sanitizedFilename = filename.replaceAll("[^\\p{L}\\p{Nd}._-]", "");

        // Normalize the filename to remove any diacritical marks or special characters
        sanitizedFilename = Normalizer.normalize(sanitizedFilename, Normalizer.Form.NFD).replaceAll("\\p{M}", "");

        // Trim leading and trailing whitespace
        sanitizedFilename = sanitizedFilename.trim();

        // Truncate the filename to a reasonable length (e.g., 255 characters)
        int maxLength = 255;
        if (sanitizedFilename.length() > maxLength) {
            sanitizedFilename = sanitizedFilename.substring(0, maxLength);
        }

        return sanitizedFilename;
    }
}
