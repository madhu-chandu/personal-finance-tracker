package com.financetracker.service;

import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.repository.TransactionRepository;
import com.financetracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    private List<Transaction> monthlyTransactions(String username, int month, int year) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        YearMonth ym = YearMonth.of(year, month);
        return transactionRepository.findByUserIdAndDateBetween(user.getId(), ym.atDay(1), ym.atEndOfMonth());
    }

    public byte[] exportCsv(String username, int month, int year) throws IOException {
        List<Transaction> transactions = monthlyTransactions(username, month, year);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (OutputStreamWriter writer = new OutputStreamWriter(out);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("Date", "Category", "Type", "Description", "Amount"))) {

            for (Transaction t : transactions) {
                printer.printRecord(t.getDate(), t.getCategory().getName(), t.getType(),
                        t.getDescription(), t.getAmount());
            }
        }
        return out.toByteArray();
    }

    public byte[] exportPdf(String username, int month, int year) throws IOException {
        List<Transaction> transactions = monthlyTransactions(username, month, year);

        BigDecimal totalIncome = transactions.stream()
                .filter(t -> t.getType().name().equals("INCOME"))
                .map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = transactions.stream()
                .filter(t -> t.getType().name().equals("EXPENSE"))
                .map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            float margin = 50;
            float y = page.getMediaBox().getHeight() - margin;

            content.setFont(PDType1Font.HELVETICA_BOLD, 16);
            content.beginText();
            content.newLineAtOffset(margin, y);
            content.showText("Monthly Expense Report - " + YearMonth.of(year, month));
            content.endText();
            y -= 30;

            content.setFont(PDType1Font.HELVETICA_BOLD, 11);
            content.beginText();
            content.newLineAtOffset(margin, y);
            content.showText(String.format("Total Income: %.2f   Total Expense: %.2f   Balance: %.2f",
                    totalIncome, totalExpense, totalIncome.subtract(totalExpense)));
            content.endText();
            y -= 25;

            content.setFont(PDType1Font.HELVETICA_BOLD, 10);
            content.beginText();
            content.newLineAtOffset(margin, y);
            content.showText(String.format("%-12s %-15s %-8s %-25s %10s", "Date", "Category", "Type", "Description", "Amount"));
            content.endText();
            y -= 15;

            content.setFont(PDType1Font.HELVETICA, 9);
            for (Transaction t : transactions) {
                if (y < margin) {
                    // Simple demo cap: stop listing further rows once the page is full.
                    // For production, open a new PDPageContentStream per additional page.
                    break;
                }
                content.beginText();
                content.newLineAtOffset(margin, y);
                String desc = t.getDescription() == null ? "" : t.getDescription();
                if (desc.length() > 25) desc = desc.substring(0, 22) + "...";
                content.showText(String.format("%-12s %-15s %-8s %-25s %10.2f",
                        t.getDate(), t.getCategory().getName(), t.getType(), desc, t.getAmount()));
                content.endText();
                y -= 14;
            }
            content.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }
}
