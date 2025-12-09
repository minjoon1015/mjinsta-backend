package back_end.springboot.component;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AiKeyManager {
    @Value("${google.service-account-key}")
    private String googleServiceAccountJsonKey;

    private GoogleCredentials credentials;
    
    @PostConstruct
    public void setUpGoogleCredentials() {
        if (googleServiceAccountJsonKey == null || googleServiceAccountJsonKey.isBlank()) return;
        
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(googleServiceAccountJsonKey.getBytes());
            this.credentials = ServiceAccountCredentials.fromStream(stream);  
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public GoogleCredentials getCredentials() {
        return this.credentials;
    }
}
