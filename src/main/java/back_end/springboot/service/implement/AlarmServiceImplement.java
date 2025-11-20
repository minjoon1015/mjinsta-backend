package back_end.springboot.service.implement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import back_end.springboot.common.AlarmType;
import back_end.springboot.dto.object.alarm.AlarmDto;
import back_end.springboot.dto.object.alarm.alarm.FollowAlarmDto;
import back_end.springboot.dto.response.ResponseDto;
import back_end.springboot.dto.response.alarm.GetAlarmListResponseDto;
import back_end.springboot.entity.AlarmEntity;
import back_end.springboot.entity.FollowsEntity;
import back_end.springboot.entity.UserEntity;
import back_end.springboot.repository.AlarmRepository;
import back_end.springboot.repository.FollowsRepository;
import back_end.springboot.repository.UserRepository;
import back_end.springboot.service.AlarmService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmServiceImplement implements AlarmService {
    private final AlarmRepository alarmRepository;
    private final FollowsRepository followsRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<? super GetAlarmListResponseDto> getList(String id) {
        try {
            List<AlarmDto> alarmList = new ArrayList<>();
            List<AlarmEntity> list = alarmRepository.findAllByUserId(id);
            if (!list.isEmpty()) {
                for (AlarmEntity l : list) {
                    if (l.getAlarmType() == AlarmType.FOLLOW) {
                        FollowsEntity followsEntity = followsRepository.findById(Integer.parseInt(l.getReferenceId())).orElse(null);
                        UserEntity getUser = userRepository.findById(followsEntity.getFollowerId()).orElse(null);
                        FollowAlarmDto followAlarmDto = new FollowAlarmDto(AlarmType.FOLLOW, l.getCreate_at(),
                                getUser.getId(), getUser.getProfileImage());
                        alarmList.add(followAlarmDto);
                    }
                }
            }

            return GetAlarmListResponseDto.success(alarmList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

}
