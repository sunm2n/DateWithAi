# DateWithAI - AI 게임 로직

데이팅 시뮬레이션 게임을 위한 AI 전용 로직입니다. OpenAI 임베딩과 PostgreSQL pgvector를 사용하여 지능적인 캐릭터 응답을 생성합니다.

## 주요 기능

- **텍스트 임베딩**: OpenAI의 text-embedding-3-small 모델 사용
- **벡터 검색**: PostgreSQL + pgvector로 유사도 기반 컨텍스트 검색
- **AI 응답 생성**: GPT-4를 사용한 자연스러운 대화 생성
- **감정 기반 응답**: 다양한 감정 상태에 따른 응답 생성
- **캐릭터별 지식 관리**: 캐릭터마다 고유한 지식 베이스 구축

## 설치 및 설정

### 1. 의존성 설치
```bash
pip install -r requirements.txt
```

### 2. 환경 변수 설정
`.env.example`을 `.env`로 복사하고 설정값을 입력하세요:

```bash
cp .env.example .env
```

필요한 환경 변수:
- `OPENAI_API_KEY`: OpenAI API 키
- `POSTGRES_HOST`: PostgreSQL 호스트
- `POSTGRES_PORT`: PostgreSQL 포트 (기본값: 5432)
- `POSTGRES_DB`: 데이터베이스 이름
- `POSTGRES_USER`: PostgreSQL 사용자명
- `POSTGRES_PASSWORD`: PostgreSQL 비밀번호

### 3. PostgreSQL 설정
PostgreSQL에 pgvector 확장을 설치해야 합니다:

```sql
CREATE EXTENSION vector;
```

## 사용 방법

### 1. 기본 사용법

```python
from game_ai import GameAI

# AI 인스턴스 생성
game_ai = GameAI()

# 데이터베이스 초기화
game_ai.initialize_database()

# AI와 대화
result = game_ai.chat_with_character(
    user_message="안녕하세요!",
    character_info="친근한 게임 캐릭터입니다."
)

print(result['response'])
game_ai.close()
```

### 2. 캐릭터 지식 베이스 업로드

```python
# 텍스트 파일에서 지식 업로드
result = game_ai.upload_character_knowledge(
    file_path="character_knowledge.txt",
    character_id="character_001"
)
```

### 3. 감정 기반 응답

```python
result = game_ai.chat_with_character(
    user_message="오늘 날씨가 좋네요!",
    emotion="happy",
    emotion_intensity=0.8
)
```

### 4. FastAPI 서버 실행

```bash
python main.py
```

API 엔드포인트:
- `POST /chat`: AI와 채팅
- `POST /upload-knowledge`: 지식 베이스 업로드
- `DELETE /knowledge/{source_file}`: 지식 삭제
- `GET /knowledge/sources`: 지식 소스 목록
- `GET /health`: 헬스 체크

## API 사용 예제

### 채팅 요청
```python
import requests

response = requests.post("http://localhost:8000/chat", json={
    "message": "안녕하세요!",
    "character_info": "친근한 캐릭터",
    "emotion": "happy",
    "emotion_intensity": 0.7
})

print(response.json())
```

## 파일 구조

```
dateWithAi_python/
├── config.py              # 설정 관리
├── embedding_service.py   # 임베딩 처리
├── vector_db.py          # PostgreSQL pgvector 연동
├── ai_response.py        # AI 응답 생성
├── game_ai.py            # 메인 게임 AI 클래스
├── main.py               # FastAPI 서버
├── example_usage.py      # 사용 예제
├── requirements.txt      # 의존성
├── .env.example         # 환경 변수 템플릿
└── README.md            # 문서
```

## 주요 클래스

### GameAI
메인 AI 로직 클래스
- `upload_character_knowledge()`: 캐릭터 지식 업로드
- `chat_with_character()`: AI와 대화
- `get_conversation_history()`: 대화 히스토리 조회

### EmbeddingService
텍스트 임베딩 처리
- `create_embedding()`: 단일 텍스트 임베딩
- `process_text_file()`: 텍스트 파일 처리 및 청킹

### VectorDatabase
PostgreSQL pgvector 연동
- `similarity_search()`: 유사도 기반 검색
- `insert_embeddings()`: 임베딩 저장

### AIResponseGenerator
AI 응답 생성
- `generate_game_response()`: 일반 게임 응답
- `generate_emotion_response()`: 감정 기반 응답

## 설정 옵션

`config.py`에서 다음 옵션들을 조정할 수 있습니다:

- `EMBEDDING_MODEL`: 임베딩 모델 (기본값: text-embedding-3-small)
- `CHAT_MODEL`: 채팅 모델 (기본값: gpt-4)
- `MAX_TOKENS`: 최대 토큰 수 (기본값: 1000)
- `TEMPERATURE`: AI 응답의 창의성 (기본값: 0.7)
- `SIMILARITY_THRESHOLD`: 유사도 임계값 (기본값: 0.7)
- `MAX_SEARCH_RESULTS`: 최대 검색 결과 수 (기본값: 5)