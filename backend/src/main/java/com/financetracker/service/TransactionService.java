package com.financetracker.service;

import com.financetracker.dto.TransactionDto;
import com.financetracker.model.Category;
import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.TransactionRepository;
import com.financetracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public List<TransactionDto> getAllForUser(String username) {
        User user = getUser(username);
        return transactionRepository.findByUserOrderByDateDesc(user)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public TransactionDto create(String username, TransactionDto dto) {
        User user = getUser(username);
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        Transaction transaction = Transaction.builder()
                .user(user)
                .category(category)
                .amount(dto.getAmount())
                .type(dto.getType())
                .description(dto.getDescription())
                .date(dto.getDate())
                .build();

        return toDto(transactionRepository.save(transaction));
    }

    public TransactionDto update(String username, Long id, TransactionDto dto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        if (!transaction.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Not allowed to modify this transaction");
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        transaction.setCategory(category);
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setDescription(dto.getDescription());
        transaction.setDate(dto.getDate());

        return toDto(transactionRepository.save(transaction));
    }

    public void delete(String username, Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        if (!transaction.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Not allowed to delete this transaction");
        }
        transactionRepository.delete(transaction);
    }

    private TransactionDto toDto(Transaction t) {
        TransactionDto dto = new TransactionDto();
        dto.setId(t.getId());
        dto.setCategoryId(t.getCategory().getId());
        dto.setCategoryName(t.getCategory().getName());
        dto.setAmount(t.getAmount());
        dto.setType(t.getType());
        dto.setDescription(t.getDescription());
        dto.setDate(t.getDate());
        return dto;
    }
}
