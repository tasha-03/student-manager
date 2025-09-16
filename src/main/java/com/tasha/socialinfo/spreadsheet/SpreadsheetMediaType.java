package com.tasha.socialinfo.spreadsheet;

import org.springframework.web.multipart.MultipartFile;

public enum SpreadsheetMediaType {
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
    XLS("application/vnd.ms-excel", ".xls"),
    CSV("text/csv", ".csv"),
    ODS("application/vnd.oasis.opendocument.spreadsheet", ".ods"),
    ;

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    private final String mimeType;
    private final String extension;

    SpreadsheetMediaType(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public static SpreadsheetMediaType fromMimeType(String mimeType) {
        for (SpreadsheetMediaType type : values()) {
            if (type.mimeType.equalsIgnoreCase(mimeType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported MIME type: " + mimeType);
    }

    public static SpreadsheetMediaType fromExtension(String extension) {
        for (SpreadsheetMediaType type : values()) {
            if (type.extension.equalsIgnoreCase(extension)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported file extension: " + extension);
    }

    public static SpreadsheetMediaType validateSpreadsheetFile (MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (filename == null) throw new IllegalArgumentException("Filename is empty");
        if (!filename.contains(".")) throw new IllegalArgumentException("Filename should have an extension");

        String extension = filename.substring(filename.lastIndexOf(".")).toLowerCase();

        SpreadsheetMediaType type;
        type = SpreadsheetMediaType.fromExtension(extension);
        if (file.getContentType() == null || !file.getContentType().equalsIgnoreCase(type.mimeType))
            throw new IllegalArgumentException("File content type and extension do not match");
        return type;
    }
}
