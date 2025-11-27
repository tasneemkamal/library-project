package library.services;


import library.models.CD;
import library.repositories.CDRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

/**
 * Comprehensive unit tests for CDService
 * @author Library Team
 * @version 1.0
 */
@DisplayName("CDService Tests")
class CDServiceTest {
    private CDService cdService;
    private CDRepository cdRepository;

    @BeforeEach
    void setUp() {
        cdRepository = mock(CDRepository.class);
        cdService = new CDService(cdRepository);
    }

    @Nested
    @DisplayName("Add CD Tests")
    class AddCDTests {
        @Test
        @DisplayName("Should add CD successfully with valid data")
        void testAddCDSuccess() {
            // Arrange
            String title = "Greatest Hits";
            String artist = "Queen";
            String genre = "Rock";
            int trackCount = 12;
            String publisher = "Music Corp";
            int releaseYear = 2020;

            when(cdRepository.save(any(CD.class))).thenReturn(true);

            // Act
            boolean result = cdService.addCD(title, artist, genre, trackCount, publisher, releaseYear);

            // Assert
            assertTrue(result);
            verify(cdRepository).save(any(CD.class));
        }

        @Test
        @DisplayName("Should fail to add CD with empty title")
        void testAddCDEmptyTitle() {
            // Arrange
            String emptyTitle = "";
            String artist = "Queen";
            String genre = "Rock";
            int trackCount = 12;
            String publisher = "Music Corp";
            int releaseYear = 2020;

            // Act
            boolean result = cdService.addCD(emptyTitle, artist, genre, trackCount, publisher, releaseYear);

            // Assert
            assertFalse(result);
            verify(cdRepository, never()).save(any(CD.class));
        }

        @Test
        @DisplayName("Should fail to add CD with null artist")
        void testAddCDNullArtist() {
            // Arrange
            String title = "Greatest Hits";
            String nullArtist = null;
            String genre = "Rock";
            int trackCount = 12;
            String publisher = "Music Corp";
            int releaseYear = 2020;

            // Act
            boolean result = cdService.addCD(title, nullArtist, genre, trackCount, publisher, releaseYear);

            // Assert
            assertFalse(result);
            verify(cdRepository, never()).save(any(CD.class));
        }

        @Test
        @DisplayName("Should fail to add CD with zero track count")
        void testAddCDZeroTrackCount() {
            // Arrange
            String title = "Greatest Hits";
            String artist = "Queen";
            String genre = "Rock";
            int zeroTrackCount = 0;
            String publisher = "Music Corp";
            int releaseYear = 2020;

            // Act
            boolean result = cdService.addCD(title, artist, genre, zeroTrackCount, publisher, releaseYear);

            // Assert
            assertFalse(result);
            verify(cdRepository, never()).save(any(CD.class));
        }

        @Test
        @DisplayName("Should fail to add CD with invalid release year")
        void testAddCDInvalidReleaseYear() {
            // Arrange
            String title = "Greatest Hits";
            String artist = "Queen";
            String genre = "Rock";
            int trackCount = 12;
            String publisher = "Music Corp";
            int invalidYear = 1800; // Too old

            // Act
            boolean result = cdService.addCD(title, artist, genre, trackCount, publisher, invalidYear);

            // Assert
            assertFalse(result);
            verify(cdRepository, never()).save(any(CD.class));
        }
    }

    @Nested
    @DisplayName("Search CD Tests")
    class SearchCDTests {
        @Test
        @DisplayName("Should search CDs by title")
        void testSearchByTitle() {
            // Arrange
            String query = "Greatest";
            CD cd1 = new CD("Greatest Hits", "Queen", "Rock", 12, "Music Corp", 2020);
            CD cd2 = new CD("Greatest Hits 2024", "Various", "Pop", 15, "Music Corp", 2024);
            List<CD> expectedCDs = Arrays.asList(cd1, cd2);

            when(cdRepository.search(query)).thenReturn(expectedCDs);

            // Act
            List<CD> result = cdService.searchCDs(query);

            // Assert
            assertEquals(2, result.size());
            verify(cdRepository).search(query);
        }

        @Test
        @DisplayName("Should search CDs by artist")
        void testSearchByArtist() {
            // Arrange
            String query = "Queen";
            CD cd = new CD("Greatest Hits", "Queen", "Rock", 12, "Music Corp", 2020);
            List<CD> expectedCDs = Collections.singletonList(cd);

            when(cdRepository.search(query)).thenReturn(expectedCDs);

            // Act
            List<CD> result = cdService.searchCDs(query);

            // Assert
            assertEquals(1, result.size());
            assertEquals("Queen", result.get(0).getArtist());
        }

        @Test
        @DisplayName("Should search CDs by genre")
        void testSearchByGenre() {
            // Arrange
            String query = "Rock";
            CD cd = new CD("Greatest Hits", "Queen", "Rock", 12, "Music Corp", 2020);
            List<CD> expectedCDs = Collections.singletonList(cd);

            when(cdRepository.search(query)).thenReturn(expectedCDs);

            // Act
            List<CD> result = cdService.searchCDs(query);

            // Assert
            assertEquals(1, result.size());
            assertEquals("Rock", result.get(0).getGenre());
        }

        @Test
        @DisplayName("Should return all CDs for empty query")
        void testSearchEmptyQuery() {
            // Arrange
            String query = "";
            CD cd1 = new CD("CD 1", "Artist 1", "Genre 1", 10, "Publisher 1", 2020);
            CD cd2 = new CD("CD 2", "Artist 2", "Genre 2", 12, "Publisher 2", 2021);
            List<CD> allCDs = Arrays.asList(cd1, cd2);

            when(cdRepository.search(query)).thenReturn(allCDs);

            // Act
            List<CD> result = cdService.searchCDs(query);

            // Assert
            assertEquals(2, result.size());
        }
    }

    @Nested
    @DisplayName("CD Management Tests")
    class CDManagementTests {
        @Test
        @DisplayName("Should get all CDs")
        void testGetAllCDs() {
            // Arrange
            CD cd1 = new CD("CD 1", "Artist 1", "Rock", 10, "Publisher 1", 2020);
            CD cd2 = new CD("CD 2", "Artist 2", "Pop", 12, "Publisher 2", 2021);
            List<CD> allCDs = Arrays.asList(cd1, cd2);

            when(cdRepository.findAll()).thenReturn(allCDs);

            // Act
            List<CD> result = cdService.getAllCDs();

            // Assert
            assertEquals(2, result.size());
            verify(cdRepository).findAll();
        }

        @Test
        @DisplayName("Should find CD by ID")
        void testFindCDById() {
            // Arrange
            String cdId = "CD_123";
            CD expectedCD = new CD("Test CD", "Test Artist", "Test Genre", 10, "Test Publisher", 2020);
            expectedCD.setId(cdId);

            when(cdRepository.findById(cdId)).thenReturn(expectedCD);

            // Act
            CD result = cdService.findCDById(cdId);

            // Assert
            assertNotNull(result);
            assertEquals(cdId, result.getId());
            verify(cdRepository).findById(cdId);
        }

        @Test
        @DisplayName("Should return null for non-existent CD ID")
        void testFindNonExistentCDById() {
            // Arrange
            String nonExistentId = "NON_EXISTENT";
            when(cdRepository.findById(nonExistentId)).thenReturn(null);

            // Act
            CD result = cdService.findCDById(nonExistentId);

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("Should get CDs by artist")
        void testGetCDsByArtist() {
            // Arrange
            String artist = "Queen";
            CD cd1 = new CD("Greatest Hits", "Queen", "Rock", 12, "Music Corp", 2020);
            CD cd2 = new CD("Live Album", "Queen", "Rock", 15, "Music Corp", 2018);
            List<CD> expectedCDs = Arrays.asList(cd1, cd2);

            when(cdRepository.findByArtist(artist)).thenReturn(expectedCDs);

            // Act
            List<CD> result = cdService.getCDsByArtist(artist);

            // Assert
            assertEquals(2, result.size());
            verify(cdRepository).findByArtist(artist);
        }

        @Test
        @DisplayName("Should get CDs by genre")
        void testGetCDsByGenre() {
            // Arrange
            String genre = "Rock";
            CD cd1 = new CD("Greatest Hits", "Queen", "Rock", 12, "Music Corp", 2020);
            CD cd2 = new CD("Rock Anthems", "Various", "Rock", 20, "Music Corp", 2021);
            List<CD> expectedCDs = Arrays.asList(cd1, cd2);

            when(cdRepository.findByGenre(genre)).thenReturn(expectedCDs);

            // Act
            List<CD> result = cdService.getCDsByGenre(genre);

            // Assert
            assertEquals(2, result.size());
            verify(cdRepository).findByGenre(genre);
        }

        @Test
        @DisplayName("Should update CD availability")
        void testUpdateAvailability() {
            // Arrange
            String cdId = "CD_123";
            CD cd = new CD("Test CD", "Test Artist", "Test Genre", 10, "Test Publisher", 2020);
            cd.setId(cdId);
            cd.setAvailable(true);

            when(cdRepository.findById(cdId)).thenReturn(cd);
            when(cdRepository.update(cd)).thenReturn(true);

            // Act
            boolean result = cdService.updateAvailability(cdId, false);

            // Assert
            assertTrue(result);
            assertFalse(cd.isAvailable());
            verify(cdRepository).update(cd);
        }

        @Test
        @DisplayName("Should fail to update availability for non-existent CD")
        void testUpdateAvailabilityNonExistentCD() {
            // Arrange
            String nonExistentId = "NON_EXISTENT";
            when(cdRepository.findById(nonExistentId)).thenReturn(null);

            // Act
            boolean result = cdService.updateAvailability(nonExistentId, false);

            // Assert
            assertFalse(result);
            verify(cdRepository, never()).update(any(CD.class));
        }
    }
}