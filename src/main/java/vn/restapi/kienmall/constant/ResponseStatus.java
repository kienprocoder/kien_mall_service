package vn.restapi.kienmall.constant;

public enum ResponseStatus {
    SUCCESS(200, "SUCCESS"),
    BAD_REQUEST(400, "ERROR"),
    NOT_FOUND(404, "NOT_FOUND"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR"),
    UNAUTHORIZED(401, "UNAUTHORIZED");
    private final int code;
    private final String message;

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
