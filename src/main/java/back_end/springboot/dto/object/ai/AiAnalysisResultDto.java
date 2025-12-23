package back_end.springboot.dto.object.ai;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisResultDto {
    private List<String> objectTag;
}
