package dto;

public enum ContentType {
    TEXT("Content-Type: text/html"),
    JPG("Content-Type: image/jpeg");


    private final String headerContent;

    ContentType(String headerContent) {
        this.headerContent = headerContent;
    }

    public String getHeaderContent() {
        return headerContent;
    }
}
