package com.financetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class DashboardResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private Map<String, BigDecimal> expenseByCategory;
    private Map<String, BigDecimal> incomeByCategory;
    private List<BudgetDto> budgetStatus;
}
