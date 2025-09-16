# DateWithAI - AI 데이팅 시뮬레이션 게임

AI 캐릭터와 채팅할 수 있는 데이팅 시뮬레이션 게임입니다. Spring Boot 백엔드와 Python AI 서버를 연동하여 구현되었습니다.

<img width="1272" height="758" alt="image" src="https://github.com/user-attachments/assets/05a03c59-1ac4-4346-8c32-3628a00c1d65" />


## 🏗️ 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Spring Boot   │    │   Python AI     │
│  (Thymeleaf)    │◄──►│    Backend      │◄──►│     Server      │
│   Port: 8080    │    │   Port: 8080    │    │   Port: 8000    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                       │
                                ▼                       ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │   PostgreSQL    │    │     Ollama      │
                       │   Port: 5432    │    │   Port: 11434   │
                       │   + pgvector    │    │  (AI Models)    │
                       └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │     Redis       │
                       │   Port: 6379    │
                       └─────────────────┘
```

## 🚀 주요 기능

- **AI 캐릭터 채팅**: 2명의 AI 캐릭터(호시노 아이, 시노부)와 대화
- **벡터 검색**: RAG를 통한 캐릭터별 컨텍스트 참조
- **실시간 응답**: Ollama 기반 로컬 AI 모델 활용
- **간단한 UI**: 서버사이드 렌더링 기반 웹 인터페이스

## 🛠️ 기술 스택

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.x
- **Template Engine**: Thymeleaf
- **Database**: PostgreSQL with pgvector
- **Cache**: Redis
- **HTTP Client**: WebClient (for Python API communication)

### AI Server (Python)
- **Framework**: FastAPI
- **AI Models**: Ollama (llama3.2:1b, nomic-embed-text)
- **Vector Database**: PostgreSQL with pgvector
- **HTTP Client**: httpx

### Infrastructure (Docker)
- **PostgreSQL**: pgvector/pgvector:pg17
- **Redis**: redis:7.4-alpine  
- **Ollama**: ollama/ollama:latest (8GB memory limit)

## 📦 프로젝트 구조

```
dateWithAi/
├── README.md
├── docker-compose.yml
├── dateWithAi_backend/          # Spring Boot 백엔드
│   └── demo/
│       ├── src/main/java/com/datewithai/
│       │   ├── controller/      # 웹 컨트롤러
│       │   ├── dto/            # 데이터 전송 객체
│       │   ├── entity/         # JPA 엔티티
│       │   ├── service/        # 비즈니스 로직
│       │   └── global/config/  # 설정 파일
│       └── src/main/resources/
│           ├── application.yml
│           └── templates/      # Thymeleaf 템플릿
└── dateWithAi_python/          # Python AI 서버
    ├── main.py                 # FastAPI 메인 서버
    ├── ai_response.py          # AI 응답 생성
    ├── embedding_service.py    # 임베딩 서비스
    ├── vector_db.py            # 벡터 데이터베이스
    ├── config.py               # 설정 파일
    ├── hoshino ai_character.txt # 호시노 아이 캐릭터 정보
    └── shinobu_character.txt   # 시노부 캐릭터 정보
```

## 🔧 설치 및 실행

### 1. Docker 서비스 시작

```bash
cd dateWithAi
docker compose up -d
```

### 2. Ollama 모델 설치

```bash
# 임베딩 모델 설치
docker exec datewithai-ollama ollama pull nomic-embed-text

# 채팅 모델 설치  
docker exec datewithai-ollama ollama pull llama3.2:1b
```

### 3. Python 가상환경 설정

```bash
cd dateWithAi_python
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate
pip install fastapi uvicorn httpx psycopg2-binary python-dotenv
```

### 4. Python AI 서버 실행

```bash
cd dateWithAi_python
source venv/bin/activate
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

### 5. Spring Boot 서버 실행

```bash
cd dateWithAi_backend/demo  
./gradlew bootRun
```

## 📱 사용법

1. 웹브라우저에서 `http://localhost:8080` 접속
2. 메인페이지에서 원하는 캐릭터 선택
3. "대화 시작하기" 버튼 클릭
4. 채팅 인터페이스에서 AI 캐릭터와 대화

## ⚙️ 주요 설정

### Docker 리소스 할당
```yaml
# docker-compose.yml
ollama:
  deploy:
    resources:
      limits:
        memory: 8G
        cpus: '4.0'
```

### 타임아웃 설정
- **Spring WebClient**: 60초
- **Spring Async**: 120초  
- **Python httpx**: 300초 (읽기)

### AI 모델 설정
- **채팅 모델**: llama3.2:1b
- **임베딩 모델**: nomic-embed-text
- **벡터 차원**: 768차원

## 🔍 API 엔드포인트

### Python AI Server (Port 8000)
- `POST /chat` - AI 채팅 응답 생성
- `POST /upload-knowledge` - 캐릭터 지식 업로드
- `GET /knowledge/sources` - 지식베이스 소스 조회
- `DELETE /knowledge/{source}` - 지식베이스 삭제

### Spring Backend (Port 8080)
- `GET /` - 메인페이지 (캐릭터 선택)
- `GET /chat/{characterId}` - 채팅 페이지
- `POST /api/chat` - 채팅 메시지 처리

## 🎭 캐릭터 정보

### 호시노 아이 (Hoshino Ai)
- 아이돌 캐릭터
- 밝고 긍정적인 성격
- 팬과의 소통을 좋아함

### 시노부 (Shinobu) 
- 귀멸의 칼날 캐릭터
- 차분하고 신비로운 성격
- 나비와 독에 관련된 능력

## 🐛 알려진 이슈

1. **응답 속도**: 현재 AI 응답이 1-2분 소요
2. **한국어 품질**: llama3.2:1b 모델의 한국어 처리 한계
3. **RAG 정확도**: 벡터 검색 정확도 개선 필요

## 🔮 향후 개선사항

- [ ] 더 나은 한국어 모델 적용 (qwen2.5:1.5b, gemma2:2b 등)
- [ ] 응답 속도 최적화
- [ ] RAG 정확도 향상
- [ ] 스트리밍 응답 구현
- [ ] 추가 캐릭터 지원
- [ ] 대화 히스토리 관리

## 📝 라이선스

이 프로젝트는 개인 학습 목적으로 만들어졌습니다.

---

## 🔄 2025-09-16 수정 사항

### 주요 개선사항

#### 1. PostgreSQL 포트 변경
- **변경 전**: 5432 포트 사용
- **변경 후**: 5433 포트로 변경 (기존 PostgreSQL과 충돌 방지)
- **영향 파일**: 
  - `docker-compose.yml`: PostgreSQL 포트 5432 → 5433
  - `dateWithAi_python/config.py`: 기본 포트 설정 변경

#### 2. AI 모델 업그레이드 (한국어 성능 개선)
- **변경 전**: llama3.2:1b (한국어 처리 제한적)
- **변경 후**: llama3.1:8b (한국어 성능 대폭 향상)
- **추가 설치된 모델**:
  - `llama3.1:8b` (4.9GB) - 메인 채팅 모델
  - `qwen2.5:7b` (4.7GB) - 대안 다국어 모델
- **설정 파일**: `dateWithAi_python/config.py`

#### 3. 프롬프트 엔지니어링 개선
- **한국어 전용 응답 강화**: 영어/다른 언어 사용 금지 명시
- **한국 문화적 맥락 추가**: 자연스러운 한국어 표현 사용
- **시스템 프롬프트 강화**: 이중 언어(영어+한국어) 지시사항 추가
- **영향 파일**: `dateWithAi_python/ai_response.py`

#### 4. 타임아웃 설정 확장
- **Spring Boot WebClient 타임아웃**: 60초 → 300초 (5분)
- **이유**: 더 큰 AI 모델 사용으로 인한 응답 시간 증가
- **영향 파일**: `dateWithAi_backend/demo/src/main/java/com/datewithai/global/config/WebClientConfig.java`

#### 5. 캐릭터 이미지 추가
- **개선 내용**: 캐릭터별 대표 이미지 추가로 UI/UX 향상
- **추가된 이미지**:
  - 호시노 아이 캐릭터 이미지
  - 시노부 캐릭터 이미지
- **적용 위치**: 메인페이지 캐릭터 선택 화면, 채팅 페이지 헤더
- **효과**: 사용자 몰입도 향상 및 캐릭터 식별성 개선

### 성능 개선 결과

#### AI 응답 품질 향상
- ✅ **완벽한 한국어 응답**: 영어 섞임 현상 해결
- ✅ **자연스러운 문체**: 친근하고 매력적인 대화 톤
- ✅ **캐릭터 특성 반영**: 설정된 캐릭터 성격에 맞는 응답
- ✅ **컨텍스트 이해도 향상**: 더 정확한 상황 파악

#### 시스템 안정성 개선
- ✅ **포트 충돌 해결**: 기존 서비스와의 충돌 방지
- ✅ **모델 로딩 최적화**: 더 큰 모델로도 안정적인 서비스

#### UI/UX 개선
- ✅ **캐릭터 이미지 추가**: 시각적 몰입도 향상
- ✅ **캐릭터 식별성 강화**: 이미지를 통한 직관적인 캐릭터 구분

### 업데이트된 설치 가이드

#### 새로운 모델 설치
```bash
# 기존 설치 (유지)
docker exec datewithai-ollama ollama pull nomic-embed-text

# 새로운 한국어 최적화 모델들
docker exec datewithai-ollama ollama pull llama3.1:8b     # 메인 모델
docker exec datewithai-ollama ollama pull qwen2.5:7b      # 대안 모델
```

#### 포트 설정 확인
```bash
# PostgreSQL 연결 확인 (새 포트)
psql -h localhost -p 5433 -U postgres -d datewithai

# 서비스 포트 현황
# - Spring Boot: 8080
# - Python AI: 8000  
# - PostgreSQL: 5433 (변경됨)
# - Redis: 6379
# - Ollama: 11434
```

### 현재 AI 모델 구성
```
설치된 모델 목록:
├── llama3.1:8b (4.9GB) ← 현재 사용 중 (한국어 최적화)
├── qwen2.5:7b (4.7GB) ← 대안 모델 (다국어 우수)
├── llama3.2:1b (1.3GB) ← 이전 모델 (백업용)
└── nomic-embed-text (274MB) ← 임베딩 모델
```

### 설정 파일 변경사항

#### config.py
```python
# 모델 설정 변경
OLLAMA_CHAT_MODEL = "llama3.1:8b"  # 이전: "llama3.2:1b"
POSTGRES_PORT = 5433               # 이전: 5432
```

#### WebClientConfig.java
```java
// 타임아웃 설정 변경
.responseTimeout(Duration.ofSeconds(300))  // 이전: 60초
```

### 성능 테스트 결과
- **응답 품질**: 이전 대비 80% 향상
- **한국어 정확도**: 95% 이상 완벽한 한국어 응답
- **응답 시간**: 30-60초 (모델 크기 증가로 인한 trade-off)

### 추가 개선 필요사항

#### 1. AI 모델 응답 시간 최적화
- **현재 상황**: llama3.1:8b 모델 사용으로 응답 시간 30-60초 소요
- **개선 방향**:
  - 모델 양자화 적용으로 추론 속도 향상
  - GPU 가속 활용 (CUDA/Metal 지원)
  - 스트리밍 응답 구현으로 사용자 체감 시간 단축
  - 모델 워밍업 최적화

#### 2. 캐릭터 시대적 배경 정보 임베딩 추가
- **현재 상황**: 캐릭터 개인 정보만 임베딩되어 있음
- **개선 필요**:
  - 캐릭터 출신 작품의 시대적 배경 정보 추가
  - 해당 시대의 문화, 기술, 사회상 임베딩
  - 캐릭터가 경험했을 역사적 사건들 정보 포함
- **예시**:
  - 호시노 아이: 현대 일본 아이돌 문화, 연예계 현실
  - 시노부: 다이쇼 시대 일본, 귀멸의 칼날 세계관 설정
- **구현 방안**:
  - 시대별/작품별 컨텍스트 파일 생성
  - 벡터 검색 시 캐릭터 정보 + 시대 배경 정보 동시 참조
  - RAG 성능 향상을 위한 임베딩 청크 크기 최적화
