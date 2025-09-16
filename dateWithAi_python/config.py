import os
from dotenv import load_dotenv

load_dotenv()

class Config:
    # Ollama 설정
    OLLAMA_BASE_URL = os.getenv("OLLAMA_BASE_URL", "http://localhost:11434")
    OLLAMA_EMBEDDING_MODEL = os.getenv("OLLAMA_EMBEDDING_MODEL", "nomic-embed-text")
    OLLAMA_CHAT_MODEL = os.getenv("OLLAMA_CHAT_MODEL", "llama3.1:8b")
    
    # 기존 OpenAI 설정 (백업용)
    OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
    
    # 데이터베이스 설정
    DATABASE_URL = os.getenv("DATABASE_URL")
    POSTGRES_HOST = os.getenv("POSTGRES_HOST", "localhost")
    POSTGRES_PORT = int(os.getenv("POSTGRES_PORT", 5433))
    POSTGRES_DB = os.getenv("POSTGRES_DB", "datewithai")
    POSTGRES_USER = os.getenv("POSTGRES_USER", "postgres")
    POSTGRES_PASSWORD = os.getenv("POSTGRES_PASSWORD", "password")
    
    # AI 모델 설정
    EMBEDDING_MODEL = OLLAMA_EMBEDDING_MODEL
    CHAT_MODEL = OLLAMA_CHAT_MODEL
    MAX_TOKENS = 200  # 더 짧은 응답으로 속도 향상
    TEMPERATURE = 0.5  # 낮은 temperature로 더 일관된 빠른 응답
    SIMILARITY_THRESHOLD = 0.5  # 더 많은 관련 컨텍스트를 가져오도록 임계값 낮춤
    MAX_SEARCH_RESULTS = 2  # 검색 결과를 더 줄여서 속도 향상