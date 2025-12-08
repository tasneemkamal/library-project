package library.repositories;

import library.models.CDFine;
import library.utils.JsonFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CDFineRepositoryTest {

    private CDFineRepository cdFineRepository;
    private JsonFileHandler mockFileHandler;

    @BeforeEach
    void setUp() {
        // محاكاة JsonFileHandler
        mockFileHandler = mock(JsonFileHandler.class);
        // استخدام المُنشئ الجديد مع المحاكي
        cdFineRepository = new CDFineRepository(mockFileHandler);
    }

    @Test
    void testSave() {
        CDFine fine = new CDFine("user123", "loan123", 50.0);

        // محاكاة استرجاع قيمة JSON عند الكتابة
        when(mockFileHandler.writeToFile(anyString(), anyString())).thenReturn(true);

        boolean isSaved = cdFineRepository.save(fine);

        assertTrue(isSaved);
        assertEquals(1, cdFineRepository.findAll().size());
    }

    @Test
    void testFindById() {
        CDFine fine = new CDFine("user123", "loan123", 50.0);
        cdFineRepository.save(fine);

        // اختبار البحث عن غرامة باستخدام المعرف
        CDFine foundFine = cdFineRepository.findById(fine.getId());

        assertNotNull(foundFine);
        assertEquals(fine.getId(), foundFine.getId());
    }

    @Test
    void testMakePayment() {
        CDFine fine = new CDFine("user123", "loan123", 50.0);
        cdFineRepository.save(fine);

        // اختبار الدفع المبدئي
        boolean paymentSuccess = fine.makePayment(20.0);
        assertTrue(paymentSuccess);
        assertEquals(30.0, fine.getRemainingAmount(), 0.01);

        // اختبار دفع كامل
        paymentSuccess = fine.makePayment(30.0);
        assertTrue(paymentSuccess);
        assertTrue(fine.isPaid());  // التأكد من أن الغرامة قد تم دفعها بالكامل
    }

    @Test
    void testFindByUserId() {
        CDFine fine1 = new CDFine("user123", "loan123", 50.0);
        CDFine fine2 = new CDFine("user456", "loan124", 30.0);

        cdFineRepository.save(fine1);
        cdFineRepository.save(fine2);

        // اختبار البحث عن الغرامات بواسطة معرف المستخدم
        List<CDFine> fines = cdFineRepository.findByUserId("user123");

        assertEquals(1, fines.size());
        assertEquals("user123", fines.get(0).getUserId());
    }

    @Test
    void testFindUnpaidCDFines() {
        // إعداد بعض الغرامات
        CDFine fine1 = new CDFine("user123", "loan123", 50.0);  // غرامة كاملة
        CDFine fine2 = new CDFine("user456", "loan124", 30.0);  // غرامة أخرى

        fine1.makePayment(25.0);  // دفع جزء من الغرامة
        fine2.makePayment(30.0);  // دفع الغرامة بالكامل

        cdFineRepository.save(fine1);
        cdFineRepository.save(fine2);

        // اختبار العثور على الغرامات غير المدفوعة
        List<CDFine> unpaidFines = cdFineRepository.findUnpaidCDFines();

        // تأكد من أن هناك فقط غرامة غير مدفوعة واحدة
        assertEquals(1, unpaidFines.size());
        assertFalse(unpaidFines.get(0).isPaid());  // التأكد من أن الغرامة التي لم تدفع بالكامل هي الغرامة الأولى
        assertEquals(25.0, unpaidFines.get(0).getRemainingAmount(), 0.01);  // التأكد من أن المبلغ المتبقي هو 25
    }



    @Test
    void testUpdate() {
        CDFine fine = new CDFine("user123", "loan123", 50.0);
        cdFineRepository.save(fine);

        // تحديث الغرامة
        fine.setAmount(60.0);
        boolean isUpdated = cdFineRepository.update(fine);

        assertTrue(isUpdated);
        assertEquals(60.0, cdFineRepository.findById(fine.getId()).getAmount());
    }

    @Test
    void testFindAll() {
        CDFine fine1 = new CDFine("user123", "loan123", 50.0);
        CDFine fine2 = new CDFine("user456", "loan124", 30.0);

        cdFineRepository.save(fine1);
        cdFineRepository.save(fine2);

        // اختبار العثور على جميع الغرامات
        List<CDFine> allFines = cdFineRepository.findAll();

        assertEquals(2, allFines.size());
    }
}
