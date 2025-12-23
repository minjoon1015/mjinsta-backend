package back_end.springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import back_end.springboot.service.ScheduleService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TestController {
    private final ScheduleService scheduleService;
    
    @GetMapping("/test")
    public void test() {
        scheduleService.updateUserInterest();
    }
}
