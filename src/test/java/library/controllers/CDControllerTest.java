package library.controllers;

import library.models.CD;
import library.services.CDService;

import org.junit.jupiter.api.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CDControllerTest {

    private CDService cdService;
    private CDController controller;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        cdService = mock(CDService.class);
        controller = new CDController(cdService);
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }


    @Test
    void testAddCD_Fail() {
        when(cdService.addCD(any(), any(), any(), anyInt(), any(), anyInt()))
                .thenReturn(false);

        controller.addCD("title", "artist", "genre", 10, "pub", 2000);

        assertTrue(outputStream.toString().contains("Failed to add CD"));
    }

    @Test
    void testAddCD_Success() {
        when(cdService.addCD(any(), any(), any(), anyInt(), any(), anyInt()))
                .thenReturn(true);

        controller.addCD("title", "artist", "genre", 10, "pub", 2000);

        assertFalse(outputStream.toString().contains("Failed"));
    }

    

    @Test
    void testSearchCDs_NoResults() {
        when(cdService.searchCDs("rock")).thenReturn(Collections.emptyList());

        controller.searchCDs("rock");

        assertTrue(outputStream.toString().contains("No CDs found"));
    }

    @Test
    void testSearchCDs_WithResults() {
        CD cd = createCD();
        when(cdService.searchCDs("hit")).thenReturn(List.of(cd));

        controller.searchCDs("hit");

        String out = outputStream.toString();
        assertTrue(out.contains("CD Search Results"));
        assertTrue(out.contains("Test Title"));
        assertTrue(out.contains("Test Artist"));
    }

    
    @Test
    void testViewAllCDs_NoCDs() {
        when(cdService.getAllCDs()).thenReturn(Collections.emptyList());

        controller.viewAllCDs();

        assertTrue(outputStream.toString().contains("No CDs available"));
    }

    @Test
    void testViewAllCDs_WithCDs() {
        CD cd = createCD();
        when(cdService.getAllCDs()).thenReturn(List.of(cd));

        controller.viewAllCDs();

        String out = outputStream.toString();
        assertTrue(out.contains("All CDs"));
        assertTrue(out.contains("Test Title"));
    }

    

    @Test
    void testViewCDsByArtist_NoCDs() {
        when(cdService.getCDsByArtist("Adele")).thenReturn(Collections.emptyList());

        controller.viewCDsByArtist("Adele");

        assertTrue(outputStream.toString().contains("No CDs found for artist: Adele"));
    }

    @Test
    void testViewCDsByArtist_WithCDs() {
        CD cd = createCD();
        when(cdService.getCDsByArtist("Adele")).thenReturn(List.of(cd));

        controller.viewCDsByArtist("Adele");

        assertTrue(outputStream.toString().contains("CDs by Adele"));
        assertTrue(outputStream.toString().contains("Test Title"));
    }

    
    @Test
    void testViewCDsByGenre_NoCDs() {
        when(cdService.getCDsByGenre("Rock")).thenReturn(Collections.emptyList());

        controller.viewCDsByGenre("Rock");

        assertTrue(outputStream.toString().contains("No CDs found in genre: Rock"));
    }

    @Test
    void testViewCDsByGenre_WithCDs() {
        CD cd = createCD();
        when(cdService.getCDsByGenre("Rock")).thenReturn(List.of(cd));

        controller.viewCDsByGenre("Rock");

        assertTrue(outputStream.toString().contains("Rock CDs"));
        assertTrue(outputStream.toString().contains("Test Artist"));
    }

    

    @Test
    void testShortenString_Indirect() {
        CD cd = createCD();
        cd.setTitle("abcdefghijklmnopqrstuv"); // long title

        when(cdService.searchCDs("long")).thenReturn(List.of(cd));

        controller.searchCDs("long");

        String out = outputStream.toString();

        
        String expectedShort = "abcdefghijklmno...";

        assertTrue(out.contains(expectedShort),
                "Expected shortened title not found in output. Output was:\n" + out);
    }



    
    private CD createCD() {
        CD cd = new CD();
        cd.setId("CD1234567");
        cd.setTitle("Test Title");
        cd.setArtist("Test Artist");
        cd.setGenre("TestGenre");
        cd.setTrackCount(10);
        cd.setPublisher("pub");
        cd.setReleaseYear(2000);
        cd.setAvailable(true);
        return cd;
    }
}
