package com.ridelink.fare_payment_service.dto;

import com.ridelink.fare_payment_service.model.PaymentMethod;
import com.ridelink.fare_payment_service.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentReceiptResponse {

    private String receiptNumber;
    private String paymentId;
    private String rideId;
    private String passengerId;
    private String driverId;
    private double totalAmount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionReference;
    private FareDetailResponse fareBreakdown;
    private LocalDateTime issuedAt;
}
