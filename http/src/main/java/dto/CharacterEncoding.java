package dto;

public enum CharacterEncoding {
    UTF_8("charset=UTF-8"),
    ISO_8859_1("charset=iso-8859-1");

    private final String headerValue;

    CharacterEncoding(String headerValue) {
        this.headerValue = headerValue;
    }

    public String getHeaderValue() {
        return headerValue;
    }
}
