package com.cloudbees.interview.ticket.booking.domain;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class PurchaseRequest {
    int requestId;
    String from;
    String to;
    User user;
    Train train;
    double price;
    LocalDateTime travelDate;
}
