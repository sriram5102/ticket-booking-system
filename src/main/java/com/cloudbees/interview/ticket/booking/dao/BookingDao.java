package com.cloudbees.interview.ticket.booking.dao;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "booking")
@Data
public class BookingDao {

    @Id
    @Column(name = "receipt_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    int receiptId;

    @OneToOne
    TrainDao train;

    @OneToOne
    UserDao user;

}
