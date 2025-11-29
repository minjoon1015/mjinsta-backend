from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from konlpy.tag import Mecab
import numpy as np
from typing import List

# 1. FastAPI 애플리케이션 초기화
app = FastAPI()

# 2. Mecab 형태소 분석기 초기화 (1회만 수행)
try:
    # 도커 환경에서는 이 단계가 안정적으로 작동해야 합니다.
    mecab = Mecab() 
except Exception as e:
    print(f"KoNLPy Mecab 초기화 실패: {e}")
    mecab = None

# 3. 요청 데이터 모델 정의 (Spring Boot에서 보낼 데이터 구조)
class PostAnalyzeRequest(BaseModel):
    post_id: int
    comment: str
    location: str
    image_url: str = None 

# 4. 키워드 추출 함수 (NLP)
def extract_keywords(text: str) -> List[str]:
    """텍스트에서 명사 중심의 핵심 키워드를 추출합니다."""
    if not mecab or not text:
        return []
    
    # 텍스트에서 명사만 추출
    nouns = mecab.nouns(text)
    # 한 글자 단어 및 불용어 (선택적) 필터링
    keywords = [noun for noun in nouns if len(noun) > 1]
    return keywords

# 5. 임베딩 벡터 생성 함수 (실제 ML 모델 추론 로직이 들어갈 곳)
def generate_embedding_vector(features: List[str]) -> List[float]:
    """모든 피처를 기반으로 512차원 임베딩 벡터를 생성합니다 (시뮬레이션)."""
    
    # 실제 환경에서는 BERT/CLIP 등의 모델을 로드하여 추론해야 합니다.
    
    # 시뮬레이션: 피처 리스트의 길이에 따라 고유한 임시 벡터 생성
    np.random.seed(42 + len("".join(features)))
    vector = np.random.rand(512).astype(np.float32).tolist()
    return vector

# 6. API 엔드포인트 정의
@app.post("/analyze/post")
async def analyze_post(request: PostAnalyzeRequest):
    """게시물 분석을 요청받고, 키워드 추출 및 임베딩 벡터를 반환합니다."""
    
    if not mecab:
        raise HTTPException(status_code=503, detail="NLP Engine not ready")

    # 1. 텍스트 분석 (NLP)
    combined_text = request.comment + " " + request.location
    keywords = extract_keywords(combined_text)
    
    # 2. 이미지 AI 분석 (Placeholder - 실제 Vision API 호출 로직으로 대체 필요)
    # 실제로는 call_vision_api(request.image_url) 호출 로직이 들어갑니다.
    vision_tags = ["강아지", "야외", "산책"] 

    # 3. 임베딩 벡터 생성
    all_features = keywords + vision_tags
    embedding_vector = generate_embedding_vector(all_features)
    
    # 4. 결과 반환 (Spring Boot에서 읽을 수 있는 JSON 형식)
    return {
        "post_id": request.post_id,
        "extracted_keywords": keywords,
        "image_ai_tags": vision_tags,
        # 벡터는 List<Float> 형태로 반환되어 Java에서 DTO로 받기 용이
        "embedding_vector": embedding_vector 
    }