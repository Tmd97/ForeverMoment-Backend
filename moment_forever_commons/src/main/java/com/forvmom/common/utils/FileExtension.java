package com.forvmom.common.utils;

import java.util.UUID;

public final class FileExtension {

    // Private constructor to prevent instantiation
    private FileExtension() {
    }

    /**
     * Returns file extension including dot (.png)
     */
    public static String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * Returns file extension without dot (png)
     */
    public static String getExtensionWithoutDot(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * Returns filename without extension
     */
    public static String getNameWithoutExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return filename;
        }
        return filename.substring(0, filename.lastIndexOf("."));
    }

    /**
     * Generates timestamp-based filename
     * Example: image_1709058392.png
     */
    public static String generateTimestampName(String filename) {
        String baseName = getNameWithoutExtension(filename);
        String extension = getExtension(filename);

        return baseName + "_" + System.currentTimeMillis() + extension;
    }

    /**
     * Generates UUID-based filename (Recommended for production)
     * Example: 550e8400-e29b-41d4-a716-446655440000.png
     */
    public static String generateUUIDName(String filename) {
        String extension = getExtension(filename);
        return UUID.randomUUID().toString() + extension;
    }
}