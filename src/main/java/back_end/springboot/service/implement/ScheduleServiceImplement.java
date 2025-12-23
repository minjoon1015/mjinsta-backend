package back_end.springboot.service.implement;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import back_end.springboot.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImplement implements ScheduleService {
    private final InterestBatchServiceImplement interestBatchServiceImplement;

    @Override
    @SchedulerLock(name = "updateUserInterest", lockAtLeastFor = "PT5M", lockAtMostFor = "PT14M")
    @Scheduled(cron = "0 0 3 * * *")
    public void updateUserInterest() {
        interestBatchServiceImplement.collectHashTagInterest();
        interestBatchServiceImplement.collectAiObjectInterest();
        interestBatchServiceImplement.collectPostViewInterest();
        interestBatchServiceImplement.collectActiveUsers();
        interestBatchServiceImplement.collectActiveUsersInterest();
        interestBatchServiceImplement.collectActiveUsersReadPost();
        interestBatchServiceImplement.runPostRankingBatch();
    }
}
