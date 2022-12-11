package dto;

public enum ResponseStatus {
    FORBIDDEN("HTTP/1.1 403 Forbidden"),
    SERVICE_UNAVAILABLE("HTTP/1.1 503 Service Unavailable"),
    OK("HTTP/1.1 200 OK");

    private final String responseLine;

    ResponseStatus(String responseLine) {
        this.responseLine = responseLine;
    }

    public String getStatusLine() {
        return responseLine;
    }
}
