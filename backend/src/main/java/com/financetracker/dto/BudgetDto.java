package com.financetracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetDto {
    private Long id;

    @NotNull
    private Long categoryId;

    private String categoryName;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal monthlyLimit;

    @Min(1) @Max(12)
    private int month;

    private int year;

    // populated by service for dashboard/alerts
    private BigDecimal spent;
    private boolean overBudget;
}
