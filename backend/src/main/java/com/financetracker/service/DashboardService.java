package com.financetracker.service;

import com.financetracker.dto.BudgetDto;
import com.financetracker.dto.DashboardResponse;
import com.financetracker.model.Transaction;
import com.financetracker.model.TransactionType;
import com.financetracker.model.User;
import com.financetracker.repository.TransactionRepository;
import com.financetracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BudgetService budgetService;

    public DashboardResponse getSummary(String username, int month, int year) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Transaction> transactions = transactionRepository
                .findByUserIdAndDateBetween(user.getId(), start, end);

        BigDecimal totalIncome = sum(transactions, TransactionType.INCOME);
        BigDecimal totalExpense = sum(transactions, TransactionType.EXPENSE);

        Map<String, BigDecimal> expenseByCategory = groupByCategory(transactions, TransactionType.EXPENSE);
        Map<String, BigDecimal> incomeByCategory = groupByCategory(transactions, TransactionType.INCOME);

        List<BudgetDto> budgetStatus = budgetService.getForMonth(username, month, year);

        return new DashboardResponse(
                totalIncome,
                totalExpense,
                totalIncome.subtract(totalExpense),
                expenseByCategory,
                incomeByCategory,
                budgetStatus
        );
    }

    private BigDecimal sum(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<String, BigDecimal> groupByCategory(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        LinkedHashMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
    }
}
