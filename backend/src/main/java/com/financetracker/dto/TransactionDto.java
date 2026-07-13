package com.financetracker.dto;

import com.financetracker.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionDto {
    private Long id;

    @NotNull
    private Long categoryId;

    private String categoryName;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @NotNull
    private TransactionType type;

    private String description;

    @NotNull
    private LocalDate date;
}
