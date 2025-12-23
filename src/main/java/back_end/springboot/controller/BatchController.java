package back_end.springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import back_end.springboot.service.ScheduleService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {
    private final ScheduleService scheduleService;

    @GetMapping("/run_scheduler")
    public void test() {
        scheduleService.updateUserInterest();
    }
}
