package back_end.springboot.service;

import back_end.springboot.dto.object.ai.AiAnalysisResultDto;

public interface AiAnalysisService {
    AiAnalysisResultDto analyzeImage(String imageUrl);
    
}
