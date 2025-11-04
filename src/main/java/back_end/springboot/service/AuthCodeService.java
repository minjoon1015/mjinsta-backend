package back_end.springboot.service;

public interface AuthCodeService {
    public void saveAuthCode(String email, String authCode);
    public boolean verifyAuthCode(String email, String inputCode);
    public void deleteAuthCode(String email);
}
