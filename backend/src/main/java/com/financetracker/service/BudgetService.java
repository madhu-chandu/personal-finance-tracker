package com.financetracker.service;

import com.financetracker.dto.BudgetDto;
import com.financetracker.model.Budget;
import com.financetracker.model.Category;
import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.repository.BudgetRepository;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.TransactionRepository;
import com.financetracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public List<BudgetDto> getForMonth(String username, int month, int year) {
        User user = getUser(username);
        return budgetRepository.findByUserAndMonthAndYear(user, month, year)
                .stream().map(b -> toDto(b, user)).collect(Collectors.toList());
    }

    public BudgetDto create(String username, BudgetDto dto) {
        User user = getUser(username);
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .monthlyLimit(dto.getMonthlyLimit())
                .month(dto.getMonth())
                .year(dto.getYear())
                .build();

        return toDto(budgetRepository.save(budget), user);
    }

    public BudgetDto update(String username, Long id, BudgetDto dto) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found"));

        if (!budget.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Not allowed to modify this budget");
        }

        budget.setMonthlyLimit(dto.getMonthlyLimit());
        return toDto(budgetRepository.save(budget), budget.getUser());
    }

    public void delete(String username, Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found"));

        if (!budget.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Not allowed to delete this budget");
        }
        budgetRepository.delete(budget);
    }

    private BigDecimal spentForCategory(User user, Long categoryId, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        return transactionRepository.findByUserIdAndDateBetween(user.getId(), start, end)
                .stream()
                .filter(t -> t.getCategory().getId().equals(categoryId))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BudgetDto toDto(Budget b, User user) {
        BudgetDto dto = new BudgetDto();
        dto.setId(b.getId());
        dto.setCategoryId(b.getCategory().getId());
        dto.setCategoryName(b.getCategory().getName());
        dto.setMonthlyLimit(b.getMonthlyLimit());
        dto.setMonth(b.getMonth());
        dto.setYear(b.getYear());

        BigDecimal spent = spentForCategory(user, b.getCategory().getId(), b.getMonth(), b.getYear());
        dto.setSpent(spent);
        dto.setOverBudget(spent.compareTo(b.getMonthlyLimit()) > 0);
        return dto;
    }
}
