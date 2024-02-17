package com.cloudbees.interview.ticket.booking.domain;

import lombok.Data;

@Data
public class Receipt {
    int receiptId;
    PurchaseRequest purchaseRequest;

}
