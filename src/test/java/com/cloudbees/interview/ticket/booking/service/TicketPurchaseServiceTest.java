package com.cloudbees.interview.ticket.booking.service;

import com.cloudbees.interview.ticket.booking.dao.*;
import com.cloudbees.interview.ticket.booking.domain.*;
import com.cloudbees.interview.ticket.booking.exception.InvalidTrainException;
import com.cloudbees.interview.ticket.booking.exception.ReceiptNotFoundException;
import com.cloudbees.interview.ticket.booking.repository.IReceiptRepository;
import com.cloudbees.interview.ticket.booking.repository.ITrainRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketPurchaseServiceTest  {

    @InjectMocks
    TicketPurchaseService ticketPurchaseService;

    @Mock
    ITrainRepository trainRepository;

    @Mock
    IReceiptRepository receiptRepository;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    UserService userService;


    @Test
    public void testViewBySectionInvalidTrainDetails() {
        when(trainRepository.findByTrainNoAndTravelDate(anyInt(),any())).thenReturn(Optional.empty());

        SectionViewRequest sectionViewRequest = new SectionViewRequest();
        sectionViewRequest.setSectionName("A");
        sectionViewRequest.setTravelDate(LocalDate.of(2024,2,2));
        sectionViewRequest.setTrainNo(123456);
        try {
            ticketPurchaseService.viewBySection(sectionViewRequest);
        } catch (InvalidTrainException e) {
            Assertions.assertEquals("Invalid Train Details",e.getMessage());
        }
    }

    @Test
    public void testViewBySectionInvalidSectionDetails() {
        when(trainRepository.findByTrainNoAndTravelDate(anyInt(),any())).thenReturn(optionalTrainDaoWithoutSection());

        SectionViewRequest sectionViewRequest = new SectionViewRequest();
        sectionViewRequest.setSectionName("A");
        sectionViewRequest.setTravelDate(LocalDate.of(2024,2,2));
        sectionViewRequest.setTrainNo(123456);
        try {
            ticketPurchaseService.viewBySection(sectionViewRequest);
        } catch (InvalidTrainException e) {
            Assertions.assertEquals("Invalid Section Details",e.getMessage());
        }
    }

    @Test
    public void testViewBySectionPositive() {
        when(trainRepository.findByTrainNoAndTravelDate(anyInt(),any())).thenReturn(optionalTrainDao());

        SectionViewRequest sectionViewRequest = new SectionViewRequest();
        sectionViewRequest.setSectionName("A");
        sectionViewRequest.setTravelDate(LocalDate.of(2024,2,2));
        sectionViewRequest.setTrainNo(12345);
        try {
            SectionDetails sectionDetails = ticketPurchaseService.viewBySection(sectionViewRequest);
            Assertions.assertEquals(1,sectionDetails.getUsers().size());
        } catch (InvalidTrainException e) {
            Assertions.assertEquals("Invalid Section Details",e.getMessage());
        }
    }


    @Test
    public void testInvalidReceiptRepository() {
        when(receiptRepository.findById(anyString())).thenReturn(Optional.empty());
        try {
            Receipt receipt = ticketPurchaseService.findReceiptById("sbcf");
        } catch (ReceiptNotFoundException e) {
            Assertions.assertEquals("Receipt: sbcf not found",e.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testReceiptRepositoryPositive() {
        when(receiptRepository.findById(anyString())).thenReturn(receipt());
        try {
            Receipt receipt = ticketPurchaseService.findReceiptById("62b71786-5c30-4c63-a53e-ba4b813dac30");
            Assertions.assertEquals("62b71786-5c30-4c63-a53e-ba4b813dac30",receipt.getReceiptId());
        } catch (ReceiptNotFoundException e) {
            Assertions.assertEquals("Receipt: sbcf not found",e.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testBookingSeatsNotAvailable() {
        try {
            ObjectMapper om = new ObjectMapper();
            //PurchaseRequest purchaseRequest = om.readValue(purchaseRequest(), PurchaseRequest.class);
            TrainDao trainDao = trainDaoFull();
            ticketPurchaseService.createTrainDetails(purchaseRequestObj(),trainDao);
        } catch (Exception e) {
            Assertions.assertEquals("Seats are full in current train",e.getMessage());
        }
    }

    @Test
    public void testBookingSeatsPositive() {
        try {
            ObjectMapper om = new ObjectMapper();
            //PurchaseRequest purchaseRequest = om.readValue(purchaseRequest(), PurchaseRequest.class);
            TrainDao trainDao = trainDao();
            ticketPurchaseService.createTrainDetails(purchaseRequestObj(),trainDao);
        } catch (Exception e) {
            Assertions.assertEquals("Seats are full in current train",e.getMessage());
        }
    }


    private Optional<ReceiptDao> receipt() {
        ReceiptDao receipt = new ReceiptDao();
        receipt.setPrice(20.0);
        receipt.setReceiptDate(LocalDateTime.now());
        receipt.setReceiptId("62b71786-5c30-4c63-a53e-ba4b813dac30");
        UserDao userDao = new UserDao();
        userDao.setEmail("john.cena@gmail.com");
        userDao.setFirstName("John");
        userDao.setLastName("Cena");
        receipt.setUserDao(userDao);

        receipt.setPurchaseRequest(purchaseRequest());
        return Optional.of(receipt);
    }

    private PurchaseRequest purchaseRequestObj() {
        PurchaseRequest purchaseRequest = new PurchaseRequest();
        purchaseRequest.setRequestId("ea374094-c812-4dd0-ad63-034fea401c3e");
        purchaseRequest.setPrice(20.0);
        purchaseRequest.setTo("Paris");
        purchaseRequest.setFrom("London");
        purchaseRequest.setTravelDate( LocalDate.of(2024,2,2));

        Train train = new Train();
        train.setTrainNo(12345);

        User user = new User();
        user.setFirstName("Sriram");
        user.setLastName("S");
        user.setEmail("sriram5102@gmail.com");

        purchaseRequest.setUser(user);
        purchaseRequest.setTrain(train);
        return purchaseRequest;
    }

    private String purchaseRequest() {
        String purchaseRequest = "{\n" +
                "            \"requestId\": \"ea374094-c812-4dd0-ad63-034fea401c3e\",\n" +
                "            \"from\": \"London\",\n" +
                "            \"to\": \"Paris\",\n" +
                "            \"user\": {\n" +
                "                \"id\": 0,\n" +
                "                \"firstName\": \"John\",\n" +
                "                \"lastName\": \"Cena\",\n" +
                "                \"email\": \"John.cena@gmail.com\"\n" +
                "            },\n" +
                "            \"train\": {\n" +
                "                \"trainNo\": 12345,\n" +
                "                \"trainName\": null,\n" +
                "                \"fromStation\": null,\n" +
                "                \"toStation\": null,\n" +
                "                \"sections\": null\n" +
                "            },\n" +
                "            \"price\": 20.0,\n" +
                "            \"travelDate\": \"2024-02-02\"\n" +
                "        }";
        return purchaseRequest;
    }


    private TrainDao trainDaoWithoutSection() {
        TrainDao trainDao = new TrainDao();
        trainDao.setTrainNo(12345);
        trainDao.setSeatsLeft(0);
        trainDao.setTrainId(1);
        trainDao.setToStation(Station.PARIS);
        trainDao.setFromStation(Station.LONDON);
        trainDao.setTravelDate( LocalDate.of(2024,2,2));
        return trainDao;
    }

    private Optional<TrainDao> optionalTrainDaoWithoutSection() {
        Optional<TrainDao> optionalTrainDao = Optional.of(trainDaoWithoutSection());
        return optionalTrainDao;
    }


    private TrainDao trainDaoFull() {
        TrainDao trainDao = new TrainDao();
        trainDao.setTrainNo(12345);
        trainDao.setSeatsLeft(0);
        trainDao.setTrainId(1);
        trainDao.setToStation(Station.PARIS);
        trainDao.setFromStation(Station.LONDON);
        trainDao.setTravelDate( LocalDate.of(2024,2,2));

        SectionDao sectionDao = new SectionDao();
        sectionDao.setSectionName("A");

        SectionDao sectionDaoB = new SectionDao();
        sectionDaoB.setSectionName("B");

        SeatDao seatDao = new SeatDao();
        seatDao.setSeatId(1);

        UserDao userDao = new UserDao();
        userDao.setUserId(1);
        userDao.setFirstName("Sriram");
        userDao.setLastName("S");
        userDao.setEmail("sriram5102@gmail.com");
        seatDao.setUserDao(userDao);

        SeatDao seatDao1 = new SeatDao();
        seatDao1.setSeatId(2);
        seatDao1.setUserDao(userDao);

        SeatDao seatDao2 = new SeatDao();
        seatDao2.setSeatId(3);
        seatDao2.setUserDao(userDao);

        sectionDao.getSeats().add(seatDao);
        sectionDao.getSeats().add(seatDao1);
        sectionDao.getSeats().add(seatDao2);

        sectionDaoB.getSeats().add(seatDao);
        sectionDaoB.getSeats().add(seatDao1);
        sectionDaoB.getSeats().add(seatDao2);


        trainDao.getSections().add(sectionDao);

        trainDao.getSections().add(sectionDaoB);
        return trainDao;
    }

    private TrainDao trainDao() {
        TrainDao trainDao = new TrainDao();
        trainDao.setTrainNo(12345);
        trainDao.setSeatsLeft(0);
        trainDao.setTrainId(1);
        trainDao.setToStation(Station.PARIS);
        trainDao.setFromStation(Station.LONDON);
        trainDao.setTravelDate( LocalDate.of(2024,2,2));

        SectionDao sectionDao = new SectionDao();
        sectionDao.setSectionName("A");

        SeatDao seatDao = new SeatDao();
        seatDao.setSeatId(1);

        UserDao userDao = new UserDao();
        userDao.setUserId(1);
        userDao.setFirstName("Sriram");
        userDao.setLastName("S");
        userDao.setEmail("sriram5102@gmail.com");
        seatDao.setUserDao(userDao);
        sectionDao.getSeats().add(seatDao);

        trainDao.getSections().add(sectionDao);
        return trainDao;
    }

    private Optional<TrainDao> optionalTrainDao() {
        Optional<TrainDao> optionalTrainDao = Optional.of(trainDaoWithoutSection());
        return optionalTrainDao;
    }

}
