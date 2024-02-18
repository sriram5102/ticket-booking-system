package com.cloudbees.interview.ticket.booking.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class Receipt extends CrudOperation{
    String receiptId;
    Object purchaseRequest;
    Object trainDetails;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime receiptTime;
    double price;
    Object userDetails;
}
