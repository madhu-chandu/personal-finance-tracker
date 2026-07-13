package com.financetracker.controller;

import com.financetracker.dto.BudgetDto;
import com.financetracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public List<BudgetDto> getForMonth(Authentication auth,
                                        @RequestParam(required = false) Integer month,
                                        @RequestParam(required = false) Integer year) {
        LocalDate now = LocalDate.now();
        int m = month != null ? month : now.getMonthValue();
        int y = year != null ? year : now.getYear();
        return budgetService.getForMonth(auth.getName(), m, y);
    }

    @PostMapping
    public ResponseEntity<BudgetDto> create(Authentication auth, @Valid @RequestBody BudgetDto dto) {
        return ResponseEntity.ok(budgetService.create(auth.getName(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetDto> update(Authentication auth, @PathVariable Long id,
                                             @Valid @RequestBody BudgetDto dto) {
        return ResponseEntity.ok(budgetService.update(auth.getName(), id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        budgetService.delete(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
