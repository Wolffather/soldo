package ru.savvy.soldo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentUpdateRequest {

    @NotNull
    private String paymentStatus;

    @PositiveOrZero
    private BigDecimal amountPaid;
}