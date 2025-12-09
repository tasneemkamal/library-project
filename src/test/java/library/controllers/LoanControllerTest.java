package library.controllers;

import library.models.Fine;
import library.models.Loan;
import library.repositories.BookRepository;
import library.repositories.UserRepository;
import library.services.FineService;
import library.services.LoanService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoanControllerTest {

    private LoanService loanService;
    private FineService fineService;
    private UserRepository userRepository;
    private BookRepository bookRepository;
    private LoanController controller;

    private ByteArrayOutputStream out;

    @BeforeEach
    void setup() {
        loanService = mock(LoanService.class);
        fineService = mock(FineService.class);
        userRepository = mock(UserRepository.class);
        bookRepository = mock(BookRepository.class);

        controller = new LoanController(loanService, fineService, userRepository, bookRepository);

        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    // Helper: create fake Loan
    private Loan makeLoan(String id, String bookId, boolean returned, boolean overdue) {
        Loan loan = mock(Loan.class);
        when(loan.getId()).thenReturn(id);
        when(loan.getBookId()).thenReturn(bookId);
        when(loan.getBorrowDate()).thenReturn("2025-01-01T10:00:00");
        when(loan.getDueDate()).thenReturn("2025-01-10T10:00:00");
        when(loan.isReturned()).thenReturn(returned);
        when(loan.isOverdue()).thenReturn(overdue);
        return loan;
    }

    // Helper: fake Fine
    private Fine makeFine(String id, double amount, double paid, boolean isPaid) {
        Fine f = mock(Fine.class);
        when(f.getId()).thenReturn(id);
        when(f.getAmount()).thenReturn(amount);
        when(f.getPaidAmount()).thenReturn(paid);
        when(f.getRemainingAmount()).thenReturn(amount - paid);
        when(f.isPaid()).thenReturn(isPaid);
        return f;
    }

    @Test
    void testViewUserLoansWhenEmpty() {
        when(loanService.getUserLoans("U1")).thenReturn(List.of());

        controller.viewUserLoans("U1");

        assertTrue(out.toString().contains("No loans found."));
    }

    @Test
    void testViewUserLoansWithData() {
        Loan l1 = makeLoan("L123456789", "B987654321", false, true);

        when(loanService.getUserLoans("U1")).thenReturn(List.of(l1));

        controller.viewUserLoans("U1");

        String output = out.toString();
        assertTrue(output.contains("My Loans"));
        assertTrue(output.contains("L1234567..."));  // substring test
        assertTrue(output.contains("Overdue"));
    }

    @Test
    void testViewUserFinesEmpty() {
        when(fineService.getUserFines("U1")).thenReturn(List.of());

        controller.viewUserFines("U1");

        assertTrue(out.toString().contains("No fines found."));
    }

    @Test
    void testViewUserFinesWithData() {
        Fine f = makeFine("F123456789", 20, 10, false);

        when(fineService.getUserFines("U1")).thenReturn(List.of(f));

        controller.viewUserFines("U1");

        String output = out.toString();
        assertTrue(output.contains("My Fines"));
        assertTrue(output.contains("F1234567..."));
        assertTrue(output.contains("20.00"));
        assertTrue(output.contains("Unpaid"));
    }

    @Test
    void testBorrowBookSuccess() {
        when(loanService.borrowBook("U1", "B1")).thenReturn(true);

        controller.borrowBook("U1", "B1");

        assertTrue(out.toString().contains("Book borrowed successfully!"));
    }

    @Test
    void testBorrowBookFail() {
        when(loanService.borrowBook("U1", "B1")).thenReturn(false);

        controller.borrowBook("U1", "B1");

        String txt = out.toString();
        assertTrue(txt.contains("Failed to borrow book."));
        assertTrue(txt.contains("available"));
    }

    @Test
    void testReturnBookSuccess() {
        when(loanService.returnBook("L1")).thenReturn(true);

        controller.returnBook("L1");

        assertTrue(out.toString().contains("Book returned successfully!"));
    }

    @Test
    void testReturnBookFail() {
        when(loanService.returnBook("L1")).thenReturn(false);

        controller.returnBook("L1");

        assertTrue(out.toString().contains("Failed to return book."));
    }

    @Test
    void testPayFineInvalidAmount() {
        controller.payFine("F1", 0);

        assertTrue(out.toString().contains("Invalid payment amount!"));
    }

    @Test
    void testPayFineSuccess() {
        when(fineService.payFine("F1", 10)).thenReturn(true);

        controller.payFine("F1", 10);

        assertTrue(out.toString().contains("Payment processed successfully!"));
    }

    @Test
    void testPayFineFail() {
        when(fineService.payFine("F1", 10)).thenReturn(false);

        controller.payFine("F1", 10);

        assertTrue(out.toString().contains("Failed to process payment"));
    }
}
