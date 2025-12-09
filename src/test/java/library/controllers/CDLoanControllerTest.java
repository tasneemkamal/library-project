package library.controllers;

import library.models.CD;
import library.models.CDFine;
import library.models.CDLoan;
import library.repositories.CDRepository;
import library.repositories.UserRepository;
import library.services.CDFineService;
import library.services.CDLoanService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CDLoanControllerTest {

    private CDLoanService loanService;
    private CDFineService fineService;
    private UserRepository userRepo;
    private CDRepository cdRepo;

    private CDLoanController controller;

    @BeforeEach
    void setup() {
        loanService = mock(CDLoanService.class);
        fineService = mock(CDFineService.class);
        userRepo = mock(UserRepository.class);
        cdRepo = mock(CDRepository.class);

        controller = new CDLoanController(loanService, fineService, userRepo, cdRepo);
    }

    // ---- Test: shortenString() ----
    @Test
    void testShortenString_null() throws Exception {
        Method m = CDLoanController.class.getDeclaredMethod("shortenString", String.class, int.class);
        m.setAccessible(true);

        String result = (String) m.invoke(controller, null, 10);

        assertEquals("", result);
    }

    @Test
    void testShortenString_short() throws Exception {
        Method m = CDLoanController.class.getDeclaredMethod("shortenString", String.class, int.class);
        m.setAccessible(true);

        String result = (String) m.invoke(controller, "Hello", 10);

        assertEquals("Hello", result);
    }

    @Test
    void testShortenString_long() throws Exception {
        Method m = CDLoanController.class.getDeclaredMethod("shortenString", String.class, int.class);
        m.setAccessible(true);

        String result = (String) m.invoke(controller, "ABCDEFGHIJK", 5);

        assertEquals("AB...", result);
    }

    // ---- Test: borrowCD ----
    @Test
    void testBorrowCD_success() {
        when(loanService.borrowCD("U1", "CD1")).thenReturn(true);

        ByteArrayOutputStream out = captureSystemOut();
        controller.borrowCD("U1", "CD1");

        assertTrue(out.toString().contains("CD borrowed successfully"));
    }

    @Test
    void testBorrowCD_fail() {
        when(loanService.borrowCD("U1", "CD1")).thenReturn(false);

        ByteArrayOutputStream out = captureSystemOut();
        controller.borrowCD("U1", "CD1");

        assertTrue(out.toString().contains("Failed to borrow CD"));
    }

    // ---- Test: returnCD ----
    @Test
    void testReturnCD_success() {
        when(loanService.returnCD("L1")).thenReturn(true);

        ByteArrayOutputStream out = captureSystemOut();
        controller.returnCD("L1");

        assertTrue(out.toString().contains("CD returned successfully"));
    }

    @Test
    void testReturnCD_fail() {
        when(loanService.returnCD("L1")).thenReturn(false);

        ByteArrayOutputStream out = captureSystemOut();
        controller.returnCD("L1");

        assertTrue(out.toString().contains("Failed to return CD"));
    }

    // ---- Test: payCDFine ----
    @Test
    void testPayFine_invalidAmount() {
        ByteArrayOutputStream out = captureSystemOut();
        controller.payCDFine("F1", -5);

        assertTrue(out.toString().contains("Invalid payment amount"));
    }

    @Test
    void testPayFine_success() {
        when(fineService.payCDFine("F1", 10)).thenReturn(true);

        ByteArrayOutputStream out = captureSystemOut();
        controller.payCDFine("F1", 10);

        assertTrue(out.toString().contains("CD fine payment processed successfully"));
    }

    @Test
    void testPayFine_fail() {
        when(fineService.payCDFine("F1", 10)).thenReturn(false);

        ByteArrayOutputStream out = captureSystemOut();
        controller.payCDFine("F1", 10);

        assertTrue(out.toString().contains("Failed to process CD fine payment"));
    }

    // ---- Test: viewUserCDLoans ----
    @Test
    void testViewLoans_empty() {
        when(loanService.getUserCDLoans("U1")).thenReturn(Collections.emptyList());

        ByteArrayOutputStream out = captureSystemOut();
        controller.viewUserCDLoans("U1");

        assertTrue(out.toString().contains("No CD loans found."));
    }

    @Test
    void testViewLoans_withData() {
        CDLoan loan = new CDLoan();
        loan.setId("L123456789");
        loan.setCdId("CD1");
        loan.setDueDateTime(LocalDateTime.now().plusDays(2));

        when(loanService.getUserCDLoans("U1")).thenReturn(List.of(loan));

        CD cd = new CD();
        cd.setTitle("Test CD");
        cd.setArtist("Artist");

        when(cdRepo.findById("CD1")).thenReturn(cd);

        ByteArrayOutputStream out = captureSystemOut();
        controller.viewUserCDLoans("U1");

        String text = out.toString();
        assertTrue(text.contains("Test CD"));
        assertTrue(text.contains("Artist"));
    }

    // ---- Test: viewUserCDFines ----
    @Test
    void testViewFines_empty() {
        when(fineService.getUserCDFines("U1")).thenReturn(Collections.emptyList());

        ByteArrayOutputStream out = captureSystemOut();
        controller.viewUserCDFines("U1");

        assertTrue(out.toString().contains("No CD fines found."));
    }

    @Test
    void testViewFines_withData() {
        CDFine fine = new CDFine();
        fine.setId("F1234567");
        fine.setAmount(10.0);
        fine.setPaidAmount(5.0);
        fine.setRemainingAmount(5.0);
        fine.setPaid(false);

        when(fineService.getUserCDFines("U1")).thenReturn(List.of(fine));

        ByteArrayOutputStream out = captureSystemOut();
        controller.viewUserCDFines("U1");

        String txt = out.toString();
        assertTrue(txt.contains("10.00"));
        assertTrue(txt.contains("5.00"));
    }

    // Utility: Capture console prints
    private ByteArrayOutputStream captureSystemOut() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        return out;
    }
}
