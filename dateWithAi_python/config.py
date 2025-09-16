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
    MAX_TOKENS = 1000
    TEMPERATURE = 0.7
    SIMILARITY_THRESHOLD = 0.7
    MAX_SEARCH_RESULTS = 5