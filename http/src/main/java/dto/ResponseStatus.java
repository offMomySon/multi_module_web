package dto;

public enum ResponseStatus {
    FORBIDDEN("HTTP/1.1 403 Forbidden\r\n"),
    SERVICE_UNAVAILABLE("HTTP/1.1 503 Service Unavailable\r\n"),
    OK("HTTP/1.1 200 OK\r\n");

    private final String responseLine;

    ResponseStatus(String responseLine) {
        this.responseLine = responseLine;
    }

    public String getStatusLine() {
        return responseLine;
    }
}
