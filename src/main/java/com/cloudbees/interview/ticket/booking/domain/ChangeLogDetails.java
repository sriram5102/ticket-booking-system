package com.cloudbees.interview.ticket.booking.domain;

import lombok.Data;
import org.json.JSONObject;

import java.time.LocalDateTime;

@Data
public class ChangeLogDetails {
    int changeId;
    Seat seat;
    User user;
    LocalDateTime travelDate;
    JSONObject from;
    JSONObject to;
    LocalDateTime changedOn;
    String changedBy;
}
