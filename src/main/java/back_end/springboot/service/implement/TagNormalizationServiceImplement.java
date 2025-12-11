package back_end.springboot.service.implement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import back_end.springboot.service.TagNormalizationService;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagNormalizationServiceImplement implements TagNormalizationService {
    private final Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);

    @Override
    public List<String> extractHeadNouns(List<String> tags) {
        Set<String> normalizedTags = new HashSet<>();

        for (String tag : tags) {
            if (tag == null || tag.isEmpty())
                continue;
            KomoranResult analyzeResult = komoran.analyze(tag);
            List<Token> nouns = analyzeResult.getTokenList().stream()
                    .filter(token -> token.getPos().startsWith("N")) // 명사(NN)로 시작하는 품사만 필터링
                    .collect(Collectors.toList());

            if (!nouns.isEmpty()) {
                normalizedTags.add(nouns.get(nouns.size() - 1).getMorph());
            } else {
                normalizedTags.add(tag);
            }
        }
        return new ArrayList<>(normalizedTags);
    }

}
