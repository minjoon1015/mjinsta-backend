package back_end.springboot.dto.response.alarm;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.object.alarm.AlarmDto;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAlarmListResponseDto extends ResponseDto {
    List<AlarmDto> list = null;

    public GetAlarmListResponseDto(ResponseCode code, List<AlarmDto> list) {
        super(code);
        this.list = list;
    }

    public static ResponseEntity<GetAlarmListResponseDto> success(List<AlarmDto> list) {
        GetAlarmListResponseDto responseDto = new GetAlarmListResponseDto(ResponseCode.SC, list);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    
}
