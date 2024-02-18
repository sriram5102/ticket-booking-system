package com.cloudbees.interview.ticket.booking.dao;

import com.cloudbees.interview.ticket.booking.domain.PurchaseRequest;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table(name = "receipt")
@Entity
public class ReceiptDao {
    @Id
    @Column(name = "receipt_id")
    String receiptId;
    @Lob
    @Column(name = "purchase_request")
    String purchaseRequest;
    @Lob
    @Column(name = "train_details")
    String trainDetails;
    @Column(name = "price")
    double price;
    @OneToOne(cascade = CascadeType.ALL)
    UserDao userDao;
    @Column(name = "receipt_date")
    LocalDateTime receiptDate;
}
