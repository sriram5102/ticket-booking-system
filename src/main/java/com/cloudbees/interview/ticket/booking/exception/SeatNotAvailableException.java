package com.cloudbees.interview.ticket.booking.exception;


public class SeatNotAvailableException extends Exception {
    public SeatNotAvailableException(String message) {
        super(message);
    }
}
