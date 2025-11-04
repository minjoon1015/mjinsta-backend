package back_end.springboot.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import back_end.springboot.common.AlarmType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alarm")
@Getter
@Setter
@NoArgsConstructor
public class AlarmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String userId;
    @Enumerated(EnumType.STRING)
    AlarmType alarmType;
    Integer referenceId;
    LocalDateTime create_at;

    public AlarmEntity(String userId, AlarmType alarmType, Integer referenceId) {
        this.userId = userId;
        this.alarmType = alarmType;
        this.referenceId = referenceId;
        this.create_at = LocalDateTime.now();
    }
}
