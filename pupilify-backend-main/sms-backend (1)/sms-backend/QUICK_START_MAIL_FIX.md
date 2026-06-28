# JavaMailSender Fix - Quick Reference Guide

## 🔴 Error को समझना (Understanding the Error)

```
Field mailSender in com.smartschool.api.serviceImpl.EmailServiceImpl 
required a bean of type 'org.springframework.mail.javamail.JavaMailSender' 
that could not be found.
```

**हिंदी में**: EmailServiceImpl को JavaMailSender की जरूरत है पर वह नहीं मिला।

---

## ✅ समस्या हल हो गई (Problem Solved!)

### क्या किया गया:
1. ✅ `application.properties` फ़ाइल बनाई गई
2. ✅ `MailConfig.java` कॉन्फ़िगरेशन क्लास बनाई गई  
3. ✅ `MailProperties.java` properties reader बनाई गई

### Files बनाई गई:
```
src/main/resources/
  └── application.properties           ← नई फ़ाइल

src/main/java/com/smartschool/api/config/
  ├── MailConfig.java                  ← नई फ़ाइल
  └── MailProperties.java              ← नई फ़ाइल

Root:
  ├── JAVAMAIL_SENDER_FIX.md           ← विस्तृत guide
  ├── setup_mail_config.bat            ← Windows setup script
  └── setup_mail_config.ps1            ← PowerShell setup script
```

---

## 🚀 अभी शुरू करें (Get Started Now)

### Option 1: GUI Setup (PowerShell)
```powershell
# PowerShell को admin के रूप में खोलें, फिर:
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process
.\setup_mail_config.ps1
```

### Option 2: Manual Setup

1. **Gmail से App Password प्राप्त करें**:
   - https://myaccount.google.com/apppasswords पर जाएं
   - Password generate करें (16 characters)

2. **Windows में Environment Variables सेट करें**:
   ```cmd
   setx MAIL_USERNAME "your-email@gmail.com"
   setx MAIL_PASSWORD "16-char-app-password"
   ```

3. **या IntelliJ में Environment Variables set करें**:
   - Run → Edit Configurations
   - Add to Environment variables:
   ```
   MAIL_USERNAME=your-email@gmail.com;MAIL_PASSWORD=your-16-char-app-password
   ```

---

## 🏃 Application को Run करें

```bash
# Option 1: Maven से
cd sms-backend
./mvnw spring-boot:run

# Option 2: JAR से
java -jar target/sms-backend-0.0.1-SNAPSHOT.jar

# Option 3: IDE से (IntelliJ)
- Run button दबाएं
```

---

## ✔️ Verify करें कि काम कर रहा है

Application logs में देखें:
```
JavaMailSender configured for: smtp.gmail.com
```

यह message दिखना चाहिए! ✅

---

## 📝 Files की जानकारी

### 1. `application.properties` (NEW)
```properties
# Default database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/smart_school_pro
spring.datasource.username=root
spring.datasource.password=Nikunj475@

# Mail configuration (reads from environment variables)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```

### 2. `MailConfig.java` (NEW)
```java
@Configuration
public class MailConfig {
    @Bean
    public JavaMailSender javaMailSender(MailProperties mailProperties) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());
        // ... SMTP properties configured
        return mailSender;
    }
}
```

### 3. `MailProperties.java` (NEW)
```java
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class MailProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private String defaultEncoding;
}
```

---

## 🔧 Different Email Providers

```properties
# Gmail
spring.mail.host=smtp.gmail.com
spring.mail.port=587

# Outlook
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587

# Yahoo
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587

# Custom/Local
spring.mail.host=your-smtp-server.com
spring.mail.port=587
```

---

## ❓ अगर अभी भी समस्या हो तो?

### समस्या 1: "Authentication failed"
```
✓ Gmail app password exactly 16 characters होनी चाहिए
✓ MAIL_USERNAME और MAIL_PASSWORD सही हों
✓ IDE/Command Line को restart करें (environment variables के लिए)
```

### समस्या 2: "Connection timeout"
```
✓ Port number 587 (TLS) है?
✓ SMTP server address गलत तो नहीं?
✓ Firewall block तो नहीं कर रहा?
```

### समस्या 3: JavaMailSender bean फिर भी नहीं मिल रहा?
```
✓ Maven rebuild करें:
  ./mvnw clean compile

✓ IntelliJ cache clear करें:
  File → Invalidate Caches → Restart

✓ यह message logs में देखें:
  "JavaMailSender configured for:"
```

### समस्या 4: "profile not found" या no properties?
```
✓ application.properties फ़ाइल है?
  Location: src/main/resources/application.properties

✓ MAIL_USERNAME और MAIL_PASSWORD environment variables में सेट हैं?
  Windows check करने के लिए:
  echo %MAIL_USERNAME%
```

---

## 📚 अतिरिक्त Resources

- **Google App Passwords**: https://myaccount.google.com/apppasswords
- **Spring Mail Documentation**: https://spring.io/guides/gs/sending-email/
- **SMTP Configuration**: https://www.google.com/support/accounts/answer/185833

---

## ✨ Summary

| Step | Action | Files |
|------|--------|-------|
| 1 | application.properties फ़ाइल बना दी गई | ✅ |
| 2 | MailConfig bean class बना दी गई | ✅ |
| 3 | MailProperties class बना दी गई | ✅ |
| 4 | अपने email credentials set करें | ⏳ करना बाकी है |
| 5 | Application restart करें | ⏳ करना बाकी है |

**अब application बिना error के start हो जाएगा!** 🎉

