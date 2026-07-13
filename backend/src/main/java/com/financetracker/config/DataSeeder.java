package com.financetracker.config;

import com.financetracker.model.Category;
import com.financetracker.model.TransactionType;
import com.financetracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            categoryRepository.saveAll(java.util.List.of(
                    Category.builder().name("Salary").type(TransactionType.INCOME).build(),
                    Category.builder().name("Freelance").type(TransactionType.INCOME).build(),
                    Category.builder().name("Investments").type(TransactionType.INCOME).build(),
                    Category.builder().name("Food & Dining").type(TransactionType.EXPENSE).build(),
                    Category.builder().name("Rent").type(TransactionType.EXPENSE).build(),
                    Category.builder().name("Utilities").type(TransactionType.EXPENSE).build(),
                    Category.builder().name("Transport").type(TransactionType.EXPENSE).build(),
                    Category.builder().name("Entertainment").type(TransactionType.EXPENSE).build(),
                    Category.builder().name("Healthcare").type(TransactionType.EXPENSE).build(),
                    Category.builder().name("Shopping").type(TransactionType.EXPENSE).build(),
                    Category.builder().name("Other").type(TransactionType.EXPENSE).build()
            ));
        }
    }
}
