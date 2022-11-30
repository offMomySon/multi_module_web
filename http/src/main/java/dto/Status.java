package dto;

public enum Status {
    FORBIDDEN("HTTP/1.1 403 Forbidden"),
    SERVICE_UNAVAILABLE("HTTP/1.1 503 Service Unavailable"),
    OK("HTTP/1.1 200 OK");

    private final String responseLine;

    Status(String responseLine) {
        this.responseLine = responseLine;
    }

    public String getStatusLine() {
        return responseLine;
    }

    public boolean isSuccess(){
        return this == OK;
    }

    public boolean doesNotSuccess(){
        return !isSuccess();
    }

}
