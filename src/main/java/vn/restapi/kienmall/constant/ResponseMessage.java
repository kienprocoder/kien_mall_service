package vn.restapi.kienmall.constant;

public class ResponseMessage {
    private String message;
    private ResponseStatus status;
    public ResponseMessage(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseMessage(String message) {
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public ResponseStatus getStatus() {
        return status;
    }
    public void setStatus(ResponseStatus status) {
        this.status = status;
    }
}
