package back_end.springboot.service.implement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.cloud.vision.v1.ImageSource;

import back_end.springboot.component.AiKeyManager;
import back_end.springboot.dto.object.ai.AiAnalysisResultDto;
import back_end.springboot.service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiAnalysisServiceImplement implements AiAnalysisService {
    private final AiKeyManager aiKeyManager;
    private final ObjectMapper objectMapper;

    @Override
    public AiAnalysisResultDto analyzeImage(String imageUrl) {
        ImageAnnotatorClient client = null;
        try {
            FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(aiKeyManager.getCredentials());

            ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
            client = ImageAnnotatorClient.create(settings);

            ImageSource source = ImageSource.newBuilder().setImageUri(imageUrl).build();
            Image image = Image.newBuilder().setSource(source).build();

            Feature feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();

            List<AnnotateImageRequest> requests = new ArrayList<>();
            requests.add(request);
            
            List<AnnotateImageResponse> response = client.batchAnnotateImages(requests).getResponsesList();
            String jsonArrayList = null;
            if (!response.isEmpty()) {
                List<EntityAnnotation> labels =  response.get(0).getLabelAnnotationsList();
                List<String> tags = labels.stream().limit(5).map(EntityAnnotation::getDescription).collect(Collectors.toList());
                jsonArrayList = objectMapper.writeValueAsString(tags);
            }   

            return new AiAnalysisResultDto(jsonArrayList);       
        } catch (Exception e) {
            e.printStackTrace();
            return new AiAnalysisResultDto(null);
        }
    }
    
}
