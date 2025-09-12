# DateWithAI - AI 데이팅 시뮬레이션 게임

AI 캐릭터와 채팅할 수 있는 데이팅 시뮬레이션 게임입니다. Spring Boot 백엔드와 Python AI 서버를 연동하여 구현되었습니다.

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