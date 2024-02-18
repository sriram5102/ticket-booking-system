package com.cloudbees.interview.ticket.booking.api;

import com.cloudbees.interview.ticket.booking.dao.SectionDao;
import com.cloudbees.interview.ticket.booking.domain.*;
import com.cloudbees.interview.ticket.booking.exception.*;
import com.cloudbees.interview.ticket.booking.service.TicketPurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.status.StatusData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/ticket/")
@Slf4j
public class TicketPurchaseController {

    @Autowired
    TicketPurchaseService ticketPurchaseService;

    @PostMapping("/purchase/submit")
    public ResponseEntity<Status> submitPurchase(@RequestBody PurchaseRequest purchaseRequest) {
        Status status = new Status();
        try {
            purchaseRequest.setRequestId(UUID.randomUUID().toString());
            log.info("Received Submit Purchase. Request ID is {}",purchaseRequest.getRequestId());
            Receipt receipt = ticketPurchaseService.submitPurchase(purchaseRequest);
            status.setStatusCode(200);
            status.setStatusDescription("Successfully booked Ticket");
            status.setCrudOperation(receipt);
            return ResponseEntity.ok(status);
        } catch (SeatNotAvailableException ex) {
            status.setStatusCode(412);
            status.setStatusDescription(ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(412)).body(status);
        } catch (ReceiptCreationException ex) {
            status.setStatusCode(500);
            status.setStatusDescription(ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(status);
        } catch (Exception ex) {
            log.error("Unknown exception occurred",ex);
            status.setStatusCode(500);
            status.setStatusDescription(ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(status);
        }
    }

    @GetMapping("/purchase/receipt/{receiptId}")
    public ResponseEntity<Status> findReceiptById(@PathVariable String receiptId) {
        Status status = new Status();
        try {
            log.info("Received request for Request ID {}",receiptId);
            Receipt receipt = ticketPurchaseService.findReceiptById(receiptId);
            status.setStatusCode(200);
            status.setStatusDescription("Successfully fetched Receipt");
            status.setCrudOperation(receipt);
            return ResponseEntity.ok(status);
        } catch (ReceiptNotFoundException ex) {
            status.setStatusCode(404);
            status.setStatusDescription(ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(status);
        } catch (Exception ex) {
            log.error("Unknown exception occurred",ex);
            status.setStatusCode(500);
            status.setStatusDescription(ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(status);
        }
    }

    @PostMapping("/purchase/remove")
    public ResponseEntity<Status> removePurchase(@RequestBody RemoveUserRequest removeUserRequest) {
        Status status = new Status();
        try {
            boolean remove = ticketPurchaseService.removeUserFromTrain(removeUserRequest);
            status.setStatusCode(200);
            status.setStatusDescription("Successfully removed user");
            return ResponseEntity.ok(status);
        } catch (InvalidTrainException | InvalidUserException ex) {
            status.setStatusCode(404);
            status.setStatusDescription(ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(status);
        } catch (Exception ex) {
            log.error("Unknown exception occurred while removing user",ex);
            status.setStatusCode(500);
            status.setStatusDescription(ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(status);
        }
    }

    @PostMapping("/purchase/move/user")
    public ResponseEntity<Status> moveUser(@RequestBody MoveUserRequest moveUserRequest) {
        Status status = new Status();
        try {
            boolean remove = ticketPurchaseService.moveUserToAnotherSeat(moveUserRequest);
            status.setStatusCode(200);
            status.setStatusDescription("Successfully moved user");
            return ResponseEntity.ok(status);
        } catch (InvalidTrainException | InvalidUserException ex) {
            status.setStatusCode(404);
            status.setStatusDescription(ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(status);
        } catch (Exception ex) {
            log.error("Unknown exception occurred while removing user",ex);
            status.setStatusCode(500);
            status.setStatusDescription(ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(status);
        }
    }

    @PostMapping("/purchase/section/view")
    public ResponseEntity<Object> viewBySection(@RequestBody SectionViewRequest sectionViewRequest) {
        Status status = new Status();
        try {
            SectionDetails sectionDetails = ticketPurchaseService.viewBySection(sectionViewRequest);
            status.setStatusCode(200);
            status.setStatusDescription("Successfully moved user");
            return ResponseEntity.ok(sectionDetails);
        } catch (InvalidTrainException ex) {
            status.setStatusCode(404);
            status.setStatusDescription(ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(status);
        } catch (Exception ex) {
            log.error("Unknown exception occurred while removing user",ex);
            status.setStatusCode(500);
            status.setStatusDescription(ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(status);
        }
    }

}
