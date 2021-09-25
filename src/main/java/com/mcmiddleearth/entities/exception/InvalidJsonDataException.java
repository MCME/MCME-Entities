package com.mcmiddleearth.entities.exception;

public class InvalidJsonDataException extends InvalidDataException {

    private final String jsonPath;
    private final String reason;
    private final String reasonMessage;

    public InvalidJsonDataException(String message, String reason, String reasonMessage, String jsonPath) {
        super(message);
        this.reasonMessage = reasonMessage;
        this.jsonPath = jsonPath;
        this.reason = reason;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public String getReasonMessage() {
        return reasonMessage;
    }

    public String getReason() {
        return reason;
    }
}
