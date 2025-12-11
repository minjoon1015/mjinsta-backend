package back_end.springboot.service;

import java.util.List;

public interface TagNormalizationService {
    public List<String> extractHeadNouns(List<String> tags);    
}
