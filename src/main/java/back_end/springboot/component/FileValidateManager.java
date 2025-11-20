package back_end.springboot.component;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import back_end.springboot.common.MessageType;

public class FileValidateManager {
    public static boolean isValidFileType(MultipartFile file) {
        String extension = getExtension(file.getOriginalFilename());
        String realType;
        try {
            realType = detectFileType(file);
        } catch (Exception e) {
            return false;
        }

        return extension.equalsIgnoreCase(realType);
    }

    private static String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    public static String detectFileType(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[8];
            is.read(header);

            String hex = bytesToHex(header);

            if (hex.startsWith("FFD8FF"))
                return "jpg";
            else if (hex.startsWith("89504E47"))
                return "png";
            else if (hex.startsWith("47494638"))
                return "gif";
            else if (hex.startsWith("25504446"))
                return "pdf";
            else if (hex.startsWith("D0CF11E0A1B11AE1"))
                return "hwp"; 
            else if (hex.startsWith("504B0304"))
                return "hwp"; 
            
            return null;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static MessageType getFileType(MultipartFile file) throws IOException {
        String detect = detectFileType(file);
        // jpg, png, gif = 이미지
        // pdf, hwp = 파일
        MessageType type = null;
        if (detect.equals("jpg")) {
            type = MessageType.IMAGE;
        }
        if (detect.equals("png")) {
            type = MessageType.IMAGE;
        }
        if (detect.equals("gif")) {
            type = MessageType.IMAGE;
        }
        if (detect.equals("pdf")) {
            type = MessageType.FILE;
        }
        if (detect.equals("hwp")) {
            type = MessageType.FILE;
        }
        return type;
    }
}
