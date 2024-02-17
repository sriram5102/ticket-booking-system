package com.cloudbees.interview.ticket.booking.domain;

import lombok.Data;

@Data
public class Seat {
    int seatNo;
    SeatType seatType;
    SeatStatus seatStatus;
}
