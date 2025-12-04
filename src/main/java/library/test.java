
package library;

import io.github.cdimascio.dotenv.Dotenv;
import library.services.NotificationService;
import library.config.EmailConfig;

public class test {

    public static void main(String[] args) {
        System.out.println("🚀 بدء تجربة إرسال الإيميل باستخدام .env ...");

        // 1️⃣ تحميل القيم من ملف .env (في root المشروع)
        Dotenv dotenv = Dotenv.load();

        String username = dotenv.get("EMAIL_USERNAME");
        String password = dotenv.get("EMAIL_PASSWORD");
        String testEmail = dotenv.get("TEST_EMAIL");

        // 2️⃣ تأكدي إن القيم مش null
        if (username == null || password == null || testEmail == null) {
            System.err.println("❌ خطأ: تحقق من محتوى ملف .env");
            return;
        }

        // 3️⃣ إعداد الإيميل
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setHost("smtp.gmail.com");
        emailConfig.setPort("587");
        emailConfig.setUsername(username);
        emailConfig.setPassword(password);
        emailConfig.setEnableTLS(true);

        // 4️⃣ إنشاء خدمة الإشعارات
        NotificationService notificationService = new NotificationService(emailConfig, true);

        // 5️⃣ إرسال إيميل تجريبي
        try {
            boolean success = notificationService.sendEmail(
                testEmail,
                "🔔 تجربة نظام المكتبة - Test from Library System",
                "مرحبًا!\n\nهذا إيميل تجريبي من نظام إدارة المكتبة.\nإذا استلمت هذا الإيميل، فهذا يعني أن النظام يعمل بشكل صحيح! 🎉\n\nمع أطيب التحيات،\nنظام إدارة المكتبة"
            );

            if (success) {
                System.out.println("✅ تم إرسال الإيميل بنجاح إلى: " + testEmail);
            } else {
                System.out.println("❌ فشل إرسال الإيميل. تحقق من البيانات.");
            }

        } catch (Exception e) {
            System.err.println("❌ حدث خطأ أثناء الإرسال: " + e.getMessage());
            e.printStackTrace();
        }
    }
}






/*
package library;



import library.services.NotificationService;
import library.config.EmailConfig;
import library.models.User;

/**
 * تجربة مباشرة لإرسال إيميل
 */
/*
public class test {
    
    public static void main(String[] args) {
        System.out.println("🚀  tبدء تجربة إرسال الإيميل...");
        
        // 1. إعداد配置 الإيميل (استبدل بالبيانات الحقيقية)
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setHost("smtp.gmail.com"); // أو smtp.outlook.com لـ Outlook
        emailConfig.setPort("587");
        emailConfig.setUsername("ramadafer25@gmail.com"); // 🔹 ضع ايميلك هنا
        emailConfig.setPassword("pbfu okca ivpa wkft"); // 🔹 ضع كلمة المرور التطبيقية هنا
        emailConfig.setEnableTLS(true);
        
        // 2. إنشاء خدمة الإشعارات
        NotificationService notificationService = new NotificationService(emailConfig, true);
        
        // 3. إيميلك الشخصي للتجربة
        String yourPersonalEmail = "ramadafer25@gmail.com"; // 🔹 ضع ايميلك هنا
        
        try {
            System.out.println("📧 جارٍ إرسال إيميل تجريبي...");
            
            // تجربة 1: إرسال إيميل بسيط
            boolean success = notificationService.sendEmail(
                yourPersonalEmail,
                "🔔 تجربة نظام المكتبة - Test from Library System",
                "مرحبا!\n\n" +
                "هذا إيميل تجريبي من نظام إدارة المكتبة.\n" +
                "إذا استلمت هذا الإيميل، فهذا يعني أن النظام يعمل بشكل صحيح! 🎉\n\n" +
                "مع أطيب التحيات،\nنظام إدارة المكتبة"
            );
            
            if (success) {
                System.out.println("✅ تم إرسال الإيميل بنجاح إلى: " + yourPersonalEmail);
                System.out.println("🎉 النظام يعمل بشكل ممتاز!");
            } else {
                System.out.println("❌ فشل إرسال الإيميل");
                System.out.println("💡 تحقق من:");
                System.out.println("   - بيانات الإيميل وكلمة المرور");
                System.out.println("   - تفعيل SMTP في حسابك");
                System.out.println("   - كلمة المرور التطبيقية (لجيميل)");
            }
            
        } catch (Exception e) {
            System.err.println("❌ حدث خطأ: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
*/