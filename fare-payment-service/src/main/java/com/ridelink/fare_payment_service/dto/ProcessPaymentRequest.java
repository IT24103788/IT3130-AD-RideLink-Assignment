package com.ridelink.fare_payment_service.dto;

import com.ridelink.fare_payment_service.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessPaymentRequest {

    private PaymentMethod paymentMethod;
    private String transactionNote;
}
