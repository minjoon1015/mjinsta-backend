# Instagram Clone

## Project URL
- **URL**: https://mjinsta.pe.kr/
- **Frontend Repository**: https://github.com/minjoon1015/mjinsta-frontend

<br/>


---

<br/>

## 기술 스택

### Backend
- **Framework**: Spring Boot, Spring Security
- **Authentication**: JWT
- **ORM**: JPA (Hibernate)
- **Real-time**: WebSocket (STOMP), RabbitMQ
- **Storage**: AWS S3

### Infrastructure
- **Server**: AWS EC2 (Docker)
- **Database**: MySQL
- **Cache**: Redis
- **Message Broker**: RabbitMQ
- **Load Balancer**: Nginx
- **Deployment**: Cloudflare Pages

### External API
- Google OAuth 2.0
- Google Cloud Vision API
- Komoran (형태소 분석)

<br/>

---

<br/>

## 시스템 아키텍처
<img width="1571" height="618" alt="스크린샷 2026-01-14 102851" src="https://github.com/user-attachments/assets/093d7eff-0eb1-4c7d-8006-cd73b771fb70" />

<br/>

## ERD

---

<br/>
<img width="1971" height="1718" alt="Untitled" src="https://github.com/user-attachments/assets/6f5f41f6-45ef-4d6f-b64e-9e62535518c4" />

## 주요 기능

### 1. 실시간 채팅
- 1:1 개인 채팅 및 그룹 채팅
- 실시간 읽음 처리
- 채팅 히스토리 페이지네이션
- 메시지 타입 구분 (일반/시스템 알림)

### 2. 맞춤형 피드 추천
- 사용자 행동 기반 관심사 분석 (댓글, 좋아요, 조회)
- Active User 대상 Top 10 관심사 추출
- Redis 캐싱 기반 빠른 피드 제공

### 3. 소셜 기능
- 게시글 작성/조회/좋아요/댓글
- 이미지 업로드 (S3)
- 해시태그 및 자동 태그 추출
- 실시간 알림

### 4. 인증/인가
- JWT 기반 인증
- Google OAuth 2.0 소셜 로그인
- Redis 기반 임시 회원가입 캐싱

<br/>

---

<br/>

## 주요 기술적 챌린지

<br/>

### 1. Scale-out 환경에서 WebSocket 세션 공유 문제

#### 문제 상황
- Nginx 라운드로빈 환경에서 WebSocket 개인 큐 메시지가 전달되지 않는 이슈 발생
- Spring Boot의 기본 STOMP 세션이 각 서버 메모리에만 저장되어 서버 간 공유 불가

#### 해결 과정
1. RabbitMQ 라우팅 설정 검토
2. TCP 연결 상태 점검
3. Heartbeat 모니터링
4. 근본 원인 파악: Spring Boot 메모리 기반 세션 관리의 한계

#### 해결 방법
- Redis 기반 세션 저장소 구현
- WebSocket 세션 ID를 Redis에 캐싱
- 세션 정보를 가진 서버가 메시지를 처리하도록 라우팅 로직 구현

#### 결과
- 메시지 전달 성공률 **100%** 달성
- 서버 수평 확장 가능한 아키텍처 구축

<br/>

### 2. 그룹 채팅 실시간 읽음 처리 설계

#### 설계 고민
여러 사용자의 읽음 상태를 실시간으로 동기화하는 방법

#### 구현 방식
- `last_read` 테이블 설계: 사용자별 마지막 읽은 메시지 ID 저장
- 채팅방 진입 시 모든 참여자의 읽음 정보 로드
- WebSocket을 통한 실시간 읽음 상태 브로드캐스트

<br/>

### 3. 채팅방 나가기 및 데이터 무결성

#### 문제
그룹 채팅에서 사용자가 나간 후 해당 사용자의 메시지가 유령 데이터가 되는 현상

#### 해결
- `chatRoom_participant` 테이블에 `isHidden` 플래그 추가
- 소프트 삭제 방식으로 히스토리 보존
- **그룹 채팅**: 모든 사용자 나가면 완전 삭제
- **개인 채팅**: 재참여 시 기존 히스토리 유지

<br/>

### 4. 맞춤형 피드 추천 알고리즘

#### 구현 단계
1. **Active User 판별**: 최근 로그인 기록 기반
2. **관심사 추출**: 
   - 최근 30일 사용자 행동 분석 (댓글, 좋아요, 조회)
   - 행동별 가중치 차등 부여
   - Komoran 형태소 분석기로 명사만 추출
3. **게시글 캐싱**:
   - 최근 상호작용한 게시글 200개
   - 관심사 매칭 + 미열람 게시글 필터링
   - Redis List로 저장
4. **재추천 방지**: Index 기반 조회 이력 관리

#### 최적화
- `@Scheduled` + `SchedulerLock`: 다중 서버 환경에서 중복 실행 방지
- 전체 캐싱 소진 시 폴백 로직

<br/>

### 5. 이미지 분석 및 태그 자동화

#### 문제
- Google Cloud Vision API가 반환하는 태그가 문장, 형용사, 명사 혼재
- 사용자 관심사 측정에 부적합한 데이터

#### 해결
- Komoran 라이브러리를 활용한 명사 추출
- 첫 번째 이미지를 대표 이미지로 자동 설정
- `post_tags` 테이블에 정제된 태그 저장

<br/>

### 알림 시스템 다형성 설계

#### 문제
알림이 참조하는 대상이 정형화되지 않음 (게시글, 댓글, 채팅 등)

#### 해결
- `alarm_type` 필드로 알림 타입 구분
- `reference_id`로 참조 PK 저장
- 알림 조회 시 타입별로 다른 Repository 사용하여 데이터 로드

<br/>

### 채팅 페이지네이션

#### 배경
모든 채팅 데이터를 한 번에 로드하면 성능 저하

#### 구현
- `messageId` 기준 Cursor 기반 페이지네이션
- 무한 스크롤 방식으로 점진적 로딩

<br/>

### 8. Google OAuth 불완전 정보 처리

#### 문제
Google OAuth로 전달받는 정보가 회원가입 필수 정보 미달

#### 해결
1. 첫 로그인 시 사용자 정보를 Redis에 임시 캐싱
2. 프론트엔드에서 추가 정보 입력 폼 제공
3. 정보 완성 후 정상 회원가입 프로세스 진행

<br/>

---

<br/>

## 성능 최적화

- **Redis 캐싱**: 반복 조회 데이터 캐싱으로 DB 부하 감소
- **메시지 영속성**: RabbitMQ를 통한 서버 다운 시 메시지 유실 방지
- **로드 밸런싱**: Nginx 라운드로빈 방식으로 트래픽 분산

<br/>

---

<br/>

## 향후 개선 계획

- 모니터링 시스템 구축 (Prometheus + Grafana)
- CI/CD 파이프라인 구축 (Github Actions 기반)
- 부하 테스트 및 성능 지표 정량화 (JMeter 활용)
- 비동기 이벤트 처리 구현

<br/>

---

<br/>

## 회고

실제 프로덕션 환경에서 발생할 수 있는 분산 시스템 문제들을 경험하고 해결하면서 많은 것을 배웠습니다. 
특히 Scale-out 환경에서의 상태 관리, 실시간 통신의 복잡성, 그리고 사용자 경험을 고려한 시스템 설계의 중요성을 깊이 이해하게 되었습니다.
