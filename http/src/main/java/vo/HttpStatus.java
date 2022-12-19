package vo;

public enum HttpStatus {
    FORBIDDEN("403", "Forbidden"),
    SERVICE_UNAVAILABLE("503", "Service Unavailable"),
    OK("200", "OK");

    private final String code;
    private final String message;

    HttpStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
