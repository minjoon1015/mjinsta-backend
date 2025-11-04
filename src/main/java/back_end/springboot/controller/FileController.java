package back_end.springboot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import back_end.springboot.component.FileManager;
import back_end.springboot.dto.response.user.UpdateProfileUrlResponseDto;
import back_end.springboot.service.ChatService;
import back_end.springboot.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final ChatService chatService;
    private final UserService userService;
    private final FileManager fileManager;

    @PostMapping("/upload/profile")
    public ResponseEntity<? super UpdateProfileUrlResponseDto> profile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {

        String beforeUrl = userService.existProfileImage(userDetails.getUsername());

        if (beforeUrl != null && !beforeUrl.isEmpty()) {
            fileManager.deleteFile(beforeUrl);
        }

        String url = fileManager.uploadFile(file, "profile");
        return userService.updateProfileImage(userDetails.getUsername(), url);
    }

    @PostMapping("/upload/chat")
    public void message(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("chatRoomId") Integer chatRoomId,
            @AuthenticationPrincipal UserDetails userDetails) {

        chatService.sendFile(files, chatRoomId, userDetails.getUsername());
    }
}
