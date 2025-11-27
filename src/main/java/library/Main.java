package library;


import library.controllers.*;
import library.services.*;
import library.repositories.*;
import library.models.User;
import library.models.Book;
import library.models.CD;
import library.config.EmailConfig;

import java.util.Scanner;

/**
 * Main application class for Library Management System
 * @author Library Team
 * @version 1.0
 */
public class Main {
    private AuthController authController;
    private BookController bookController;
    private CDController cdController;
    private UserController userController;
    private NotificationController notificationController;
    private LoanController loanController;
    private CDLoanController cdLoanController;
    private Scanner scanner;
    private boolean isRunning;

    // Service instances for dependency injection
    private AuthService authService;
    private BookService bookService;
    private CDService cdService;
    private LoanService loanService;
    private CDLoanService cdLoanService;
    private FineService fineService;
    private CDFineService cdFineService;
    private NotificationService notificationService;
    private NotificationManager notificationManager;

    public Main() {
        initializeDependencies();
        initializeTestData();
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
    }

    /**
     * Initialize application dependencies
     */
    private void initializeDependencies() {
        try {
            // Initialize repositories
            UserRepository userRepository = new UserRepository();
            BookRepository bookRepository = new BookRepository();
            CDRepository cdRepository = new CDRepository();
            LoanRepository loanRepository = new LoanRepository();
            CDLoanRepository cdLoanRepository = new CDLoanRepository();
            FineRepository fineRepository = new FineRepository();
            CDFineRepository cdFineRepository = new CDFineRepository();
            
            // Initialize services
            SecurityService securityService = new SecurityService();
            this.authService = new AuthService(userRepository, securityService);
            this.bookService = new BookService(bookRepository);
            this.cdService = new CDService(cdRepository);
            this.fineService = new FineService(fineRepository, loanRepository);
            this.cdFineService = new CDFineService(cdFineRepository, cdLoanRepository);
            this.loanService = new LoanService(loanRepository, bookRepository, userRepository, fineService);
            this.cdLoanService = new CDLoanService(cdLoanRepository, cdRepository, userRepository, cdFineService);
            this.notificationService = new NotificationService();
            this.notificationManager = new NotificationManager(notificationService, loanRepository, cdLoanRepository, fineRepository, cdFineRepository, userRepository);
            
            // Initialize controllers
            this.authController = new AuthController(authService);
            this.bookController = new BookController(bookService);
            this.cdController = new CDController(cdService);
            this.userController = new UserController(userRepository, authService);
            this.notificationController = new NotificationController(notificationManager, notificationService);
            this.loanController = new LoanController(loanService, fineService, userRepository, bookRepository);
            this.cdLoanController = new CDLoanController(cdLoanService, cdFineService, userRepository, cdRepository);

            // Create default admin if doesn't exist
            createDefaultAdmin();
            
            System.out.println("✅ System initialized successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing system: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create default admin user if not exists
     */
    private void createDefaultAdmin() {
        try {
            UserRepository userRepo = new UserRepository();
            if (userRepo.findByEmail("admin@library.com") == null) {
                SecurityService securityService = new SecurityService();
                String hashedPassword = securityService.hashPassword("admin123");
                User admin = new User("System Administrator", "admin@library.com", hashedPassword, "ADMIN");
                userRepo.save(admin);
                System.out.println("🔑 Default admin created: admin@library.com / admin123");
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not create default admin: " + e.getMessage());
        }
    }

    /**
     * Initialize test data for books and CDs
     */
    private void initializeTestData() {
        try {
            BookRepository bookRepo = new BookRepository();
            CDRepository cdRepo = new CDRepository();
            
            // Add sample books if none exist
            if (bookRepo.findAll().isEmpty()) {
                Book book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "978-0743273565", "BOOK");
                Book book2 = new Book("To Kill a Mockingbird", "Harper Lee", "978-0061120084", "BOOK");
                Book book3 = new Book("1984", "George Orwell", "978-0451524935", "BOOK");
                
                bookRepo.save(book1);
                bookRepo.save(book2);
                bookRepo.save(book3);
                
                System.out.println("✅ Sample books added for testing");
            }
            
            // Add sample CDs if none exist
            if (cdRepo.findAll().isEmpty()) {
                CD cd1 = new CD("Greatest Hits 2024", "Various Artists", "Pop", 15, "Music Corp", 2024);
                CD cd2 = new CD("Jazz Classics", "Miles Davis", "Jazz", 10, "Jazz Records", 2020);
                CD cd3 = new CD("Rock Anthems", "Queen", "Rock", 12, "Rock Music", 2018);
                CD cd4 = new CD("Classical Masterpieces", "Beethoven", "Classical", 8, "Classical Records", 2019);
                
                cdRepo.save(cd1);
                cdRepo.save(cd2);
                cdRepo.save(cd3);
                cdRepo.save(cd4);
                
                System.out.println("✅ Sample CDs added for testing");
            }
        } catch (Exception e) {
            System.out.println("Note: Could not initialize test data: " + e.getMessage());
        }
    }

    /**
     * Start the application
     */
    public void start() {
        System.out.println("🚀 === Library Management System ===");
        System.out.println("📚 Welcome to the Library Management System!");

        while (isRunning) {
            try {
                if (!authController.isLoggedIn()) {
                    showLoginMenu();
                } else {
                    if (authController.isAdmin()) {
                        showAdminMenu();
                    } else {
                        showUserMenu();
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ An error occurred: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }
        
        scanner.close();
        System.out.println("👋 Thank you for using Library Management System!");
    }

    /**
     * Show login/registration menu
     */
    private void showLoginMenu() {
        System.out.println("\n🏠 === Main Menu ===");
        System.out.println("1. 🔐 Login");
        System.out.println("2. 📝 Register");
        System.out.println("3. ❌ Exit");
        System.out.print("👉 Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                handleLogin();
                break;
            case 2:
                handleRegistration();
                break;
            case 3:
                isRunning = false;
                break;
            default:
                System.out.println("❌ Invalid option! Please try again.");
        }
    }

    /**
     * Show admin menu
     */
    private void showAdminMenu() {
        System.out.println("\n👨‍💼 === Admin Menu ===");
        System.out.println("1. 📖 Manage Books");
        System.out.println("2. 💿 Manage CDs");
        System.out.println("3. 👥 Manage Users");
        System.out.println("4. 📧 Notification Settings");
        System.out.println("5. ⏰ Send Reminders");
        System.out.println("6. 📊 System Statistics");
        System.out.println("7. 🏠 Logout");
        System.out.print("👉 Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                handleBookManagement();
                break;
            case 2:
                handleCDManagement();
                break;
            case 3:
                handleUserManagement();
                break;
            case 4:
                handleNotificationSettings();
                break;
            case 5:
                handleSendReminders();
                break;
            case 6:
                handleSystemStatistics();
                break;
            case 7:
                authController.logout();
                break;
            default:
                System.out.println("❌ Invalid option! Please try again.");
        }
    }

    /**
     * Show user menu
     */
    private void showUserMenu() {
        System.out.println("\n👤 === User Menu ===");
        System.out.println("1. 📚 Browse Books");
        System.out.println("2. 💿 Browse CDs");
        System.out.println("3. 📖 My Book Loans");
        System.out.println("4. 💿 My CD Loans");
        System.out.println("5. 💰 My Fines");
        System.out.println("6. 🏠 Logout");
        System.out.print("👉 Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                handleBookBrowsing();
                break;
            case 2:
                handleCDBrowsing();
                break;
            case 3:
                handleMyBookLoans();
                break;
            case 4:
                handleMyCDLoans();
                break;
            case 5:
                handleMyFines();
                break;
            case 6:
                authController.logout();
                break;
            default:
                System.out.println("❌ Invalid option! Please try again.");
        }
    }

    /**
     * Handle book management (admin)
     */
    private void handleBookManagement() {
        System.out.println("\n📖 === Book Management ===");
        System.out.println("1. ➕ Add Book");
        System.out.println("2. 🔍 Search Books");
        System.out.println("3. 📚 View All Books");
        System.out.println("4. ↩️ Back to Main Menu");
        System.out.print("👉 Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                handleAddBook();
                break;
            case 2:
                handleSearchBooks();
                break;
            case 3:
                handleViewAllBooks();
                break;
            case 4:
                return;
            default:
                System.out.println("❌ Invalid option!");
        }
    }

    /**
     * Handle CD management (admin)
     */
    private void handleCDManagement() {
        System.out.println("\n💿 === CD Management ===");
        System.out.println("1. ➕ Add CD");
        System.out.println("2. 🔍 Search CDs");
        System.out.println("3. 📀 View All CDs");
        System.out.println("4. 🎵 View CDs by Artist");
        System.out.println("5. 🎼 View CDs by Genre");
        System.out.println("6. ↩️ Back to Main Menu");
        System.out.print("👉 Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                handleAddCD();
                break;
            case 2:
                handleSearchCDs();
                break;
            case 3:
                handleViewAllCDs();
                break;
            case 4:
                handleViewCDsByArtist();
                break;
            case 5:
                handleViewCDsByGenre();
                break;
            case 6:
                return;
            default:
                System.out.println("❌ Invalid option!");
        }
    }

    /**
     * Handle adding a new book
     */
    private void handleAddBook() {
        System.out.println("\n📖 === Add New Book ===");
        System.out.print("📗 Title: ");
        String title = scanner.nextLine().trim();
        
        System.out.print("👨‍💼 Author: ");
        String author = scanner.nextLine().trim();
        
        System.out.print("🔢 ISBN: ");
        String isbn = scanner.nextLine().trim();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            System.out.println("❌ Title, author, and ISBN are required!");
            return;
        }

        bookController.addBook(title, author, isbn, "BOOK");
    }

    /**
     * Handle adding a new CD
     */
    private void handleAddCD() {
        System.out.println("\n💿 === Add New CD ===");
        System.out.print("📀 Title: ");
        String title = scanner.nextLine().trim();
        
        System.out.print("🎤 Artist: ");
        String artist = scanner.nextLine().trim();
        
        System.out.print("🎼 Genre: ");
        String genre = scanner.nextLine().trim();
        
        System.out.print("🔢 Track Count: ");
        int trackCount = getIntInput();
        
        System.out.print("🏢 Publisher: ");
        String publisher = scanner.nextLine().trim();
        
        System.out.print("📅 Release Year: ");
        int releaseYear = getIntInput();

        if (title.isEmpty() || artist.isEmpty() || genre.isEmpty()) {
            System.out.println("❌ Title, artist, and genre are required!");
            return;
        }

        if (trackCount <= 0) {
            System.out.println("❌ Track count must be positive!");
            return;
        }

        cdController.addCD(title, artist, genre, trackCount, publisher, releaseYear);
    }

    /**
     * Handle book browsing (user)
     */
    private void handleBookBrowsing() {
        System.out.println("\n📚 === Book Browsing ===");
        System.out.println("1. 🔍 Search Books");
        System.out.println("2. 📚 View All Books");
        System.out.println("3. 📖 Borrow Book");
        System.out.println("4. ↩️ Back to Menu");
        System.out.print("👉 Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                handleSearchBooks();
                break;
            case 2:
                handleViewAllBooks();
                break;
            case 3:
                handleBorrowBook();
                break;
            case 4:
                return;
            default:
                System.out.println("❌ Invalid option!");
        }
    }

    /**
     * Handle CD browsing (user)
     */
    private void handleCDBrowsing() {
        System.out.println("\n💿 === CD Browsing ===");
        System.out.println("1. 🔍 Search CDs");
        System.out.println("2. 📀 View All CDs");
        System.out.println("3. 🎵 View CDs by Artist");
        System.out.println("4. 🎼 View CDs by Genre");
        System.out.println("5. 💿 Borrow CD");
        System.out.println("6. ↩️ Back to Menu");
        System.out.print("👉 Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                handleSearchCDs();
                break;
            case 2:
                handleViewAllCDs();
                break;
            case 3:
                handleViewCDsByArtist();
                break;
            case 4:
                handleViewCDsByGenre();
                break;
            case 5:
                handleBorrowCD();
                break;
            case 6:
                return;
            default:
                System.out.println("❌ Invalid option!");
        }
    }

    /**
     * Handle book search
     */
    private void handleSearchBooks() {
        System.out.println("\n🔍 === Search Books ===");
        System.out.print("🔎 Enter search query (title, author, or ISBN): ");
        String query = scanner.nextLine().trim();
        bookController.searchBooks(query);
    }

    /**
     * Handle CD search
     */
    private void handleSearchCDs() {
        System.out.println("\n🔍 === Search CDs ===");
        System.out.print("🔎 Enter search query (title, artist, or genre): ");
        String query = scanner.nextLine().trim();
        cdController.searchCDs(query);
    }

    /**
     * Handle viewing all books
     */
    private void handleViewAllBooks() {
        System.out.println("\n📚 === All Books ===");
        bookController.viewAllBooks();
    }

    /**
     * Handle viewing all CDs
     */
    private void handleViewAllCDs() {
        System.out.println("\n💿 === All CDs ===");
        cdController.viewAllCDs();
    }

    /**
     * Handle viewing CDs by artist
     */
    private void handleViewCDsByArtist() {
        System.out.println("\n🎵 === CDs by Artist ===");
        System.out.print("Enter artist name: ");
        String artist = scanner.nextLine().trim();
        cdController.viewCDsByArtist(artist);
    }

    /**
     * Handle viewing CDs by genre
     */
    private void handleViewCDsByGenre() {
        System.out.println("\n🎼 === CDs by Genre ===");
        System.out.print("Enter genre: ");
        String genre = scanner.nextLine().trim();
        cdController.viewCDsByGenre(genre);
    }

    /**
     * Handle borrowing a book
     */
    private void handleBorrowBook() {
        System.out.print("Enter Book ID to borrow: ");
        String bookId = scanner.nextLine().trim();
        String userId = authService.getCurrentUser().getId();
        loanController.borrowBook(userId, bookId);
    }

    /**
     * Handle borrowing a CD
     */
    private void handleBorrowCD() {
        System.out.print("Enter CD ID to borrow: ");
        String cdId = scanner.nextLine().trim();
        String userId = authService.getCurrentUser().getId();
        cdLoanController.borrowCD(userId, cdId);
    }

    /**
     * Handle user's book loans
     */
    private void handleMyBookLoans() {
        System.out.println("\n📖 === My Book Loans ===");
        String currentUserId = authService.getCurrentUser().getId();
        loanController.viewUserLoans(currentUserId);
        
        System.out.println("\n1. ↩️ Return Book");
        System.out.println("2. ↩️ Back to Menu");
        System.out.print("👉 Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                handleReturnBook();
                break;
            case 2:
                return;
            default:
                System.out.println("❌ Invalid option!");
        }
    }

    /**
     * Handle user's CD loans
     */
    private void handleMyCDLoans() {
        System.out.println("\n💿 === My CD Loans ===");
        String currentUserId = authService.getCurrentUser().getId();
        cdLoanController.viewUserCDLoans(currentUserId);
        
        System.out.println("\n1. ↩️ Return CD");
        System.out.println("2. ↩️ Back to Menu");
        System.out.print("👉 Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                handleReturnCD();
                break;
            case 2:
                return;
            default:
                System.out.println("❌ Invalid option!");
        }
    }

    private void handleReturnBook() {
        System.out.print("Enter Loan ID to return: ");
        String loanId = scanner.nextLine().trim();
        loanController.returnBook(loanId);
    }

    private void handleReturnCD() {
        System.out.print("Enter CD Loan ID to return: ");
        String cdLoanId = scanner.nextLine().trim();
        cdLoanController.returnCD(cdLoanId);
    }

    /**
     * Handle user's fines
     */
    private void handleMyFines() {
        System.out.println("\n💰 === My Fines ===");
        String currentUserId = authService.getCurrentUser().getId();
        
        System.out.println("\n=== Book Fines ===");
        loanController.viewUserFines(currentUserId);
        
        System.out.println("\n=== CD Fines ===");
        cdLoanController.viewUserCDFines(currentUserId);
        
        System.out.println("\n1. 💳 Pay Book Fine");
        System.out.println("2. 💳 Pay CD Fine");
        System.out.println("3. ↩️ Back to Menu");
        System.out.print("👉 Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                handlePayBookFine();
                break;
            case 2:
                handlePayCDFine();
                break;
            case 3:
                return;
            default:
                System.out.println("❌ Invalid option!");
        }
    }

    private void handlePayBookFine() {
        System.out.print("Enter Book Fine ID to pay: ");
        String fineId = scanner.nextLine().trim();
        System.out.print("Enter payment amount: ");
        double amount = getDoubleInput();
        loanController.payFine(fineId, amount);
    }

    private void handlePayCDFine() {
        System.out.print("Enter CD Fine ID to pay: ");
        String cdFineId = scanner.nextLine().trim();
        System.out.print("Enter payment amount: ");
        double amount = getDoubleInput();
        cdLoanController.payCDFine(cdFineId, amount);
    }

    // باقي الدوال (user management, notifications, etc.) تبقى كما هي مع تحديثات طفيفة
    private void handleUserManagement() {
        System.out.println("\n👥 === User Management ===");
        System.out.println("1. 👀 View All Users");
        System.out.println("2. 🚫 Deactivate User");
        System.out.println("3. ✅ Activate User");
        System.out.println("4. 📊 View User Statistics");
        System.out.println("5. ↩️ Back to Main Menu");
        System.out.print("👉 Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                userController.viewAllUsers();
                break;
            case 2:
                handleDeactivateUser();
                break;
            case 3:
                handleActivateUser();
                break;
            case 4:
                userController.viewUserStatistics();
                break;
            case 5:
                return;
            default:
                System.out.println("❌ Invalid option!");
        }
    }

    private void handleDeactivateUser() {
        System.out.print("Enter user ID to deactivate: ");
        String userId = scanner.nextLine().trim();
        userController.deactivateUser(userId);
    }

    private void handleActivateUser() {
        System.out.print("Enter user ID to activate: ");
        String userId = scanner.nextLine().trim();
        userController.activateUser(userId);
    }

    /**
     * Handle system statistics
     */
    private void handleSystemStatistics() {
        System.out.println("\n📊 === System Statistics ===");
        
        // Books statistics
        var allBooks = bookService.getAllBooks();
        long totalBooks = allBooks.size();
        long availableBooks = allBooks.stream().filter(Book::isAvailable).count();
        
        // CDs statistics
        var allCDs = cdService.getAllCDs();
        long totalCDs = allCDs.size();
        long availableCDs = allCDs.stream().filter(CD::isAvailable).count();
        
        // Loans statistics
        var allBookLoans = loanService.getOverdueLoans();
        var allCDLoans = cdLoanService.getOverdueCDLoans();
        
        System.out.println("📚 Books: " + totalBooks + " total, " + availableBooks + " available");
        System.out.println("💿 CDs: " + totalCDs + " total, " + availableCDs + " available");
        System.out.println("⏰ Overdue Book Loans: " + allBookLoans.size());
        System.out.println("⏰ Overdue CD Loans: " + allCDLoans.size());
        System.out.println("📦 Total Media Items: " + (totalBooks + totalCDs));
        
        userController.viewUserStatistics();
    }

    // الدوال الأخرى (login, registration, notifications) تبقى كما هي

    private void handleLogin() {
        System.out.println("\n🔐 === Login ===");
        System.out.print("📧 Email: ");
        String email = scanner.nextLine().trim();
        
        System.out.print("🔒 Password: ");
        String password = scanner.nextLine().trim();

        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("❌ Email and password cannot be empty!");
            return;
        }

        authController.login(email, password);
    }

    private void handleRegistration() {
        System.out.println("\n📝 === Registration ===");
        System.out.print("👤 Name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("📧 Email: ");
        String email = scanner.nextLine().trim();
        
        System.out.print("🔒 Password: ");
        String password = scanner.nextLine().trim();
        
        System.out.print("🎭 Role (ADMIN/USER): ");
        String role = scanner.nextLine().trim().toUpperCase();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("❌ All fields are required!");
            return;
        }

        if (!role.equals("ADMIN") && !role.equals("USER")) {
            role = "USER";
            System.out.println("ℹ️ Defaulting to USER role.");
        }

        authController.register(name, email, password, role);
    }

    // الدوال المساعدة للـ notifications تبقى كما هي
    private void handleNotificationSettings() {
        System.out.println("\n📧 === Notification Settings ===");
        System.out.println("1. 📊 View Status");
        System.out.println("2. ⚙️ Configure Email");
        System.out.println("3. ✅ Enable Real Mode");
        System.out.println("4. 🧪 Enable Mock Mode");
        System.out.println("5. 🧪 Test Email");
        System.out.println("6. ↩️ Back to Main Menu");
        System.out.print("👉 Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                notificationController.getStatus();
                break;
            case 2:
                handleConfigureEmail();
                break;
            case 3:
                notificationController.enableRealMode();
                break;
            case 4:
                notificationController.enableMockMode();
                break;
            case 5:
                System.out.print("Enter test email address: ");
                String testEmail = scanner.nextLine().trim();
                notificationController.testEmail(testEmail);
                break;
            case 6:
                return;
            default:
                System.out.println("❌ Invalid option!");
        }
    }

    private void handleConfigureEmail() {
        System.out.println("\n⚙️ === Configure Email Settings ===");
        // ... نفس الكود السابق
    }

    private void handleSendReminders() {
        System.out.println("\n⏰ === Send Reminders ===");
        System.out.println("1. 📨 Send Overdue Reminders");
        System.out.println("2. ↩️ Back");
        System.out.print("👉 Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                notificationController.sendOverdueReminders();
                break;
            case 2:
                return;
            default:
                System.out.println("❌ Invalid option!");
        }
    }

    /**
     * Get integer input from user
     * @return integer input
     */
    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Get double input from user
     * @return double input
     */
    private double getDoubleInput() {
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }

    /**
     * Main method to start the application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            Main app = new Main();
            app.start();
        } catch (Exception e) {
            System.err.println("💥 Critical error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}