# JavaMailSender Bean Error - Fix Explanation

## समस्या (Problem)
```
Field mailSender in com.smartschool.api.serviceImpl.EmailServiceImpl required a bean 
of type 'org.springframework.mail.javamail.JavaMailSender' that could not be found.
```

**मतलब**: Spring Boot को `JavaMailSender` bean नहीं मिला, इसलिए application start नहीं हो सका।

---

## कारण (Root Cause)

1. **`application.properties` फ़ाइल नहीं थी** - सिर्फ `application-local.properties` था
2. **Mail configuration placeholder पर था** - smtp.example.com जैसे fake values
3. **Spring Mail Auto-Configuration काम नहीं कर रहा था** - क्योंकि सही properties नहीं थे

---

## समाधान (Solution)

### Step 1: `application.properties` फ़ाइल बनाई गई
फ़ाइल: `src/main/resources/application.properties`

यह फ़ाइल **डिफ़ॉल्ट कॉन्फ़िगरेशन** देती है जब कोई profile specify नहीं किया जाता।

**महत्वपूर्ण configs**:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME:your-email@gmail.com}
spring.mail.password=${MAIL_PASSWORD:your-app-password}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Step 2: `MailConfig.java` कॉन्फ़िगरेशन क्लास बनाई गई
फ़ाइल: `src/main/java/com/smartschool/api/config/MailConfig.java`

यह explicitly `JavaMailSender` bean बनाता है।

### Step 3: `MailProperties.java` क्लास बनाई गई
फ़ाइल: `src/main/java/com/smartschool/api/config/MailProperties.java`

यह `application.properties` से mail settings को read करता है।

---

## अपने Email को Configure करें

### Gmail के लिए (सबसे आसान):

1. **Gmail Account खोलें** - https://myaccount.google.com/
2. **Security** → **App passwords** खोलें
3. **16-character password** generate करें
4. **Environment Variables सेट करें**:

**Windows Command Line में**:
```cmd
setx MAIL_USERNAME "your-email@gmail.com"
setx MAIL_PASSWORD "your-16-char-app-password"
```

फिर Command Line को restart करें।

**या IDE में (IntelliJ)**:
- Run → Edit Configurations
- Environment variables में add करें:
```
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-16-char-app-password
```

### अन्य Email Providers के लिए:

| Provider | Host | Port |
|----------|------|------|
| Gmail | smtp.gmail.com | 587 |
| Outlook | smtp-mail.outlook.com | 587 |
| Yahoo | smtp.mail.yahoo.com | 587 |
| Custom | your-smtp-server.com | 587 or 465 |

---

## Application को अब Start करें

```bash
# Maven से:
./mvnw spring-boot:run

# या JAR से:
java -jar target/sms-backend-0.0.1-SNAPSHOT.jar
```

अब **error नहीं आएगा** क्योंकि `JavaMailSender` bean सही से configure हो गया है! ✅

---

## Debug करना (यदि अभी भी समस्या हो)

**Debug mode में run करें**:
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--debug"
```

**या logs check करें**:
- Search for: "JavaMailSender configured"
- यह message आना चाहिए application startup में

---

## Files की Summary

| File | Purpose |
|------|---------|
| `application.properties` | Default mail & DB configuration |
| `application-local.properties` | Local development overrides |
| `MailConfig.java` | Creates JavaMailSender bean |
| `MailProperties.java` | Reads mail settings from properties |
| `EmailServiceImpl.java` | Uses JavaMailSender for sending emails |

---

## Common Issues & Solutions

### Issue 1: "Authentication failed for user"
```
✓ Gmail app password 16 characters होनी चाहिए
✓ MAIL_USERNAME और MAIL_PASSWORD सही हों
✓ Gmail से "Less secure apps" allow करना पड़ सकता है (पुराने accounts में)
```

### Issue 2: "Connection timeout"
```
✓ Port number सही है (587 for TLS, 465 for SSL)
✓ SMTP server address सही है
✓ Firewall block तो नहीं कर रहा?
```

### Issue 3: Still getting JavaMailSender error?
```
✓ Maven clean rebuild करें:
  ./mvnw clean compile
✓ IntelliJ cache clear करें:
  File → Invalidate Caches → Restart
```

---

## अगले Steps

1. Email credentials set करें (environment variables)
2. Application restart करें
3. Logs में "JavaMailSender configured" देखें
4. EmailServiceImpl use करके email भेजने की test करें

**Happy coding! 🚀**

