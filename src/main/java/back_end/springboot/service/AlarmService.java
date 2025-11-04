package back_end.springboot.service;

import org.springframework.http.ResponseEntity;

import back_end.springboot.dto.response.alarm.GetAlarmListResponseDto;

public interface AlarmService {
    ResponseEntity<? super GetAlarmListResponseDto> getList(String id);
}
