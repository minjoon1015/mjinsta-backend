package back_end.springboot.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailManager {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public void send(String sendTo, String sub, String con) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        mimeMessage.setFrom(sender);
        mimeMessage.addRecipient(RecipientType.TO, new InternetAddress(sendTo));
        mimeMessage.setSubject(sub); // 제목
        mimeMessage.setText(con); // 내용
        javaMailSender.send(mimeMessage);
    }


}
