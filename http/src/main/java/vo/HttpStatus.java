package vo;

public enum HttpStatus {
    FORBIDDEN("403 Forbidden"),
    SERVICE_UNAVAILABLE("503 Service Unavailable"),
    OK("200 OK");

    private final String statusMessage;

    HttpStatus(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
