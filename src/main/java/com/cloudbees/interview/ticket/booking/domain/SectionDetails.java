package com.cloudbees.interview.ticket.booking.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SectionDetails {
    String sectionName;
    List<UserDetails> users = new ArrayList<>();
}
