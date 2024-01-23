package carsharingapp.exception;

public class TelegramBotMessageException extends RuntimeException {
    public TelegramBotMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
