package com.cloudbees.interview.ticket.booking.service;

import com.cloudbees.interview.ticket.booking.dao.*;
import com.cloudbees.interview.ticket.booking.domain.*;
import com.cloudbees.interview.ticket.booking.exception.*;
import com.cloudbees.interview.ticket.booking.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TicketPurchaseService {

    @Value("${ticket.booking.seat.limit}")
    int seatLimit;

    @Autowired
    UserService userService;

    @Autowired
    ITrainRepository trainRepository;

    @Autowired
    IUserRepository userRepository;

    @Autowired
    ISectionRepository sectionRepository;

    @Autowired
    ISeatRepository seatRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    IReceiptRepository receiptRepository;

    public Receipt submitPurchase(PurchaseRequest purchaseRequest) throws SeatNotAvailableException,ReceiptCreationException {
        TrainDetails trainDetails = bookTrainUsingPurchaseRequest(purchaseRequest);
        Receipt receipt = createReceiptForPurchase(purchaseRequest, trainDetails);
        return receipt;
    }

    public boolean removeUserFromTrain(RemoveUserRequest removeUserRequest) throws InvalidTrainException, InvalidUserException {
        Optional<TrainDao> optionalTrainDao = trainRepository.findByTrainNoAndTravelDate(removeUserRequest.getTrainNo(), removeUserRequest.getTravelDate());
        if(optionalTrainDao.isEmpty()) {
            throw new InvalidTrainException("Invalid Train Details");
        } else {
            TrainDao trainDao = optionalTrainDao.get();
            Optional<SeatDao> optional = trainDao.getSections().stream().flatMap(section -> section.getSeats().stream()).
                    filter(seat -> {
                        if(seat.getUserDao()!=null){
                           if(seat.getUserDao().getUserId()==removeUserRequest.getUser().getId())
                               return true;
                        }
                        return false;
                    })
                    .findFirst();
            if(optional.isEmpty()) {
                throw new InvalidUserException("Invalid User Details");
            } else {
                SeatDao seatDao = optional.get();
                seatDao.setUserDao(null);
                seatRepository.save(seatDao);
                return true;
            }
        }
    }

    public boolean moveUserToAnotherSeat(MoveUserRequest moveUserRequest) throws InvalidTrainException, InvalidUserException, SeatNotAvailableException {
        Optional<TrainDao> optionalTrainDao = trainRepository.findByTrainNoAndTravelDate(moveUserRequest.getTrainNo(), moveUserRequest.getTravelDate());
        if(optionalTrainDao.isEmpty()) {
            throw new InvalidTrainException("Invalid Train Details");
        } else {
            TrainDao trainDao = optionalTrainDao.get();
            Optional<SeatDao> optional = trainDao.getSections().stream().flatMap(section -> section.getSeats().stream()).
                    filter(seat -> {
                        if(seat.getUserDao()!=null){
                            if(seat.getUserDao().getUserId()==moveUserRequest.getUser().getId())
                                return true;
                        }
                        return false;
                    })
                    .findFirst();
            if(optional.isEmpty()) {
                throw new InvalidUserException("Invalid User Details");
            } else {
                SeatDao seatDao = optional.get();
                UserDao userDao = seatDao.getUserDao();
                seatDao.setUserDao(null);
                seatRepository.save(seatDao);
                Optional<SectionDao> optionalSectionDao = trainDao.getSections().stream().
                        filter(sectionDao -> sectionDao.getSectionName().equals(moveUserRequest.getTargetSectionName()))
                        .findFirst();
                if(optionalSectionDao.isEmpty()) {
                    throw new InvalidTrainException("Invalid Section Details");
                }
                optional = optionalSectionDao.get().getSeats().stream().
                        filter(current -> current.getSeatId()==moveUserRequest.getTargetSeatId())
                        .findFirst();
                if(!optional.isEmpty()) {
                    throw new SeatNotAvailableException("Requested seat already occupied by other user");
                }

                SeatDao targetSeatDao = new SeatDao();
                targetSeatDao.setSeatId(moveUserRequest.getTargetSeatId());
                targetSeatDao.setSeatStatus(SeatStatus.BOOKED.name());
                targetSeatDao.setUserDao(userDao);
                targetSeatDao.setSectionDao(optionalSectionDao.get());
                optionalSectionDao.get().getSeats().add(targetSeatDao);
                sectionRepository.save(optionalSectionDao.get());
                return true;
            }
        }
    }

    public Receipt findReceiptById(String receiptId) throws ReceiptNotFoundException, JsonProcessingException {
        Optional<ReceiptDao> optional = receiptRepository.findById(receiptId);
        if(optional.isEmpty()) {
            throw new ReceiptNotFoundException("Receipt: "+receiptId+ " not found");
        } else {
            ReceiptDao receiptDao = optional.get();
            return convertReceiptDaoToReceipt(receiptDao);
        }
    }

    public SectionDetails viewBySection(SectionViewRequest sectionViewRequest) throws InvalidTrainException {
        Optional<TrainDao> optionalTrainDao = trainRepository.findByTrainNoAndTravelDate(sectionViewRequest.getTrainNo(), sectionViewRequest.getTravelDate());
        if(optionalTrainDao.isEmpty()) {
            throw new InvalidTrainException("Invalid Train Details");
        } else {
            TrainDao trainDao = optionalTrainDao.get();
            Optional<SectionDao> optionalSectionDao = trainDao.getSections().stream().filter(current -> current.getSectionName().equals(sectionViewRequest.getSectionName())).findFirst();
            if(optionalSectionDao.isEmpty()) {
                throw new InvalidTrainException("Invalid Section Details");
            }
            SectionDao sectionDao = optionalSectionDao.get();
            SectionDetails sectionDetails = new SectionDetails();
            sectionDetails.setSectionName(sectionDao.getSectionName());
            for(SeatDao seatDao : sectionDao.getSeats()){
                UserDetails userDetails = new UserDetails();
                if(seatDao.getUserDao()!=null) {
                    userDetails.setFirstName(seatDao.getUserDao().getFirstName());
                    userDetails.setLastName(seatDao.getUserDao().getLastName());
                    userDetails.setEmail(seatDao.getUserDao().getEmail());
                    userDetails.setSeatNo(seatDao.getSeatId());
                }
                sectionDetails.getUsers().add(userDetails);
            }
            return sectionDetails;
        }
    }

    private Receipt createReceiptForPurchase(PurchaseRequest purchaseRequest, TrainDetails trainDetails) throws ReceiptCreationException {
        Receipt receipt = null;
        try {
            trainDetails.setTrainDao(null);
            String trainDetailsAsString = objectMapper.writeValueAsString(trainDetails);
            String purchaseRequestAsString = objectMapper.writeValueAsString(purchaseRequest);
            ReceiptDao receiptDao = saveReceipt(trainDetailsAsString,purchaseRequestAsString,trainDetails.getSeatDao().getUserDao(), purchaseRequest.getPrice());
            return convertReceiptDaoToReceipt(receiptDao);
        } catch (Exception e) {
            log.error("Error during receipt creation ",e);
            throw new ReceiptCreationException(e.getMessage());
        }
    }

    private Receipt convertReceiptDaoToReceipt(ReceiptDao receiptDao) throws JsonProcessingException {
        Receipt receipt = new Receipt();
        receipt.setReceiptId(receiptDao.getReceiptId());
        receipt.setPurchaseRequest(objectMapper.readValue(receiptDao.getPurchaseRequest(), PurchaseRequest.class));
        receipt.setReceiptTime(receiptDao.getReceiptDate());
        receipt.setTrainDetails(objectMapper.readValue(receiptDao.getTrainDetails(), TrainDetails.class));
        receipt.setUserDetails(receiptDao.getUserDao());
        receipt.setPrice(receiptDao.getPrice());
        return receipt;
    }

    private ReceiptDao saveReceipt(String trainDetails, String purchaseRequest, UserDao userDao, double price) {
        ReceiptDao receiptDao = new ReceiptDao();
        receiptDao.setReceiptId(UUID.randomUUID().toString());
        receiptDao.setReceiptDate(LocalDateTime.now());
        receiptDao.setPrice(price);
        receiptDao.setTrainDetails(trainDetails);
        receiptDao.setUserDao(userDao);
        receiptDao.setPurchaseRequest(purchaseRequest);
        receiptDao = receiptRepository.save(receiptDao);
        return receiptDao;
    }

    private TrainDetails bookTrainUsingPurchaseRequest(PurchaseRequest purchaseRequest) throws SeatNotAvailableException{
        TrainDao trainDao = null;
        TrainDetails trainDetails = null;
        Optional<TrainDao> optionalTrainDao = trainRepository.findByTrainNoAndTravelDate(purchaseRequest.getTrain().getTrainNo(), purchaseRequest.getTravelDate());
        if(optionalTrainDao.isEmpty()) {
            trainDetails = createTrainDetails(purchaseRequest);
        } else {
            trainDetails = createTrainDetails(purchaseRequest,optionalTrainDao.get());
        }
        trainDao = trainDetails.getTrainDao();
        trainRepository.save(trainDao);

        return trainDetails;
    }

    private TrainDetails createTrainDetails(PurchaseRequest purchaseRequest) {
        TrainDetails trainDetails = new TrainDetails();

        TrainDao trainDao = new TrainDao();
        SectionDao sectionDao = new SectionDao();
        SeatDao seatDao = new SeatDao();
        UserDao userDao = userService.convertDomainToDao(purchaseRequest.getUser());

        seatDao.setUserDao(userDao);
        seatDao.setSeatStatus(SeatStatus.BOOKED.name());
        seatDao.setUserDao(userDao);

        sectionDao.setSectionName("A");
        sectionDao.setTrainDao(trainDao);

        trainDao.setTrainNo(purchaseRequest.getTrain().getTrainNo());
        trainDao.setFromStation(Station.valueOf(purchaseRequest.getFrom().toUpperCase(Locale.ROOT)));
        trainDao.setToStation(Station.valueOf(purchaseRequest.getTo().toUpperCase(Locale.ROOT)));
        trainDao.setSeatsLeft(seatLimit-1);

        List<SectionDao> sections = Collections.singletonList(sectionDao);
        List<SeatDao> seats = Collections.singletonList(seatDao);

        seatDao.setSectionDao(sectionDao);
        sectionDao.setSeats(seats);
        sectionDao.setSeatsLeft((seatLimit/2)-1);

        trainDao.setSections(sections);
        trainDao.setTravelDate(purchaseRequest.getTravelDate());
        trainDetails.setTrainDao(trainDao);
        trainDetails.setSeatDao(seatDao);
        trainDetails.setSectionName(sectionDao.getSectionName());
        trainDetails.setTrainNo(trainDao.getTrainNo());
        return trainDetails;
    }

    public TrainDetails createTrainDetails(PurchaseRequest purchaseRequest, TrainDao trainDao) throws SeatNotAvailableException{
        TrainDetails trainDetails = new TrainDetails();
        trainDetails.setTrainDao(trainDao);
        List<SectionDao> sections = trainDao.getSections();
        SectionDao sectionDao = null;
        Optional<SectionDao> optional = sections.stream()
                .filter(current -> current.getSeats().size() <=((seatLimit/2)-1))
                .findFirst();
        if(optional.isEmpty()) {
            if(sections.size()==1){
                sectionDao = new SectionDao();
                sectionDao.setSectionName("B");
                sectionDao.setSeats(new ArrayList<>());
                sectionDao.setSeatsLeft((seatLimit/2)-1);
                sectionDao.setTrainDao(trainDao);
                trainDao.getSections().add(sectionDao);
            } else {
                throw new SeatNotAvailableException("Seats are full in current train");
            }
        } else {
            sectionDao = optional.get();
            sectionDao.setSeatsLeft(sectionDao.getSeatsLeft()-1);
        }

        SeatDao seatDao = new SeatDao();
        UserDao userDao = userService.convertDomainToDao(purchaseRequest.getUser());

        seatDao.setUserDao(userDao);
        seatDao.setSeatStatus(SeatStatus.BOOKED.name());
        seatDao.setUserDao(userDao);

        trainDao.setSeatsLeft(trainDao.getSeatsLeft()-1);

        seatDao.setSectionDao(sectionDao);
        sectionDao.getSeats().add(seatDao);

        trainDao.setTravelDate(purchaseRequest.getTravelDate());
        trainDetails.setSeatDao(seatDao);
        trainDetails.setSectionName(sectionDao.getSectionName());
        trainDetails.setTrainNo(trainDao.getTrainNo());
        return trainDetails;
    }

}
