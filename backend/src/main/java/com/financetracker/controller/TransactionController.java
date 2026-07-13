package com.financetracker.controller;

import com.financetracker.dto.TransactionDto;
import com.financetracker.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public List<TransactionDto> getAll(Authentication auth) {
        return transactionService.getAllForUser(auth.getName());
    }

    @PostMapping
    public ResponseEntity<TransactionDto> create(Authentication auth, @Valid @RequestBody TransactionDto dto) {
        return ResponseEntity.ok(transactionService.create(auth.getName(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDto> update(Authentication auth, @PathVariable Long id,
                                                  @Valid @RequestBody TransactionDto dto) {
        return ResponseEntity.ok(transactionService.update(auth.getName(), id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication auth, @PathVariable Long id) {
        transactionService.delete(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
