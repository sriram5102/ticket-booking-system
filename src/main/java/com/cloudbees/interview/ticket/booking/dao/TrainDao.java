package com.cloudbees.interview.ticket.booking.dao;

import com.cloudbees.interview.ticket.booking.domain.Section;
import com.cloudbees.interview.ticket.booking.domain.Station;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "train",
uniqueConstraints = @UniqueConstraint(columnNames = {"train_no", "travel_date" }))
public class TrainDao implements Serializable {
    @Id
    @Column(name = "train_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    int trainId;
    @Column(name = "train_no")
    int trainNo;
    @Column(name = "travel_date")
    LocalDate travelDate;
    @Column(name = "from_station")
    Station fromStation;
    @Column(name = "to_station")
    Station toStation;
    @Column(name = "seats_left")
    int seatsLeft;
    @OneToMany(cascade = CascadeType.ALL)
    @JsonManagedReference
    List<SectionDao> sections = new ArrayList<>();
}
