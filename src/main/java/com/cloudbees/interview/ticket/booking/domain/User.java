package com.cloudbees.interview.ticket.booking.domain;

import lombok.Data;

@Data
public class User {
    int id;
    String firstName;
    String lastName;
    String email;
}
