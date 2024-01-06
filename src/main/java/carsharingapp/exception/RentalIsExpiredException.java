package carsharingapp.exception;

public class RentalIsExpiredException extends RuntimeException {
    public RentalIsExpiredException(String message) {
        super(message);
    }
}
