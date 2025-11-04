package back_end.springboot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import back_end.springboot.dto.response.alarm.GetAlarmListResponseDto;
import back_end.springboot.service.AlarmService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/alarm")
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;

    @GetMapping("/getList")
    public ResponseEntity<? super GetAlarmListResponseDto> getList(@AuthenticationPrincipal UserDetails userDetails) {
        return alarmService.getList(userDetails.getUsername());
    }
}
