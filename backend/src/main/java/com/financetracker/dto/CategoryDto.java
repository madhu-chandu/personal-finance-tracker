package com.financetracker.dto;

import com.financetracker.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private TransactionType type;
}
