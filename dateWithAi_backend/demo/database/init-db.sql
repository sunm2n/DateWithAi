-- PostgreSQL 초기화 스크립트
-- PGVector 확장 설치
CREATE EXTENSION IF NOT EXISTS vector;

-- 데이터베이스 설정
SET timezone = 'Asia/Seoul';

-- 사용자 테이블
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 캐릭터 테이블
CREATE TABLE IF NOT EXISTS characters (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    personality TEXT,
    speaking_style TEXT,
    profile_image_url VARCHAR(255),
    age INTEGER NOT NULL,
    occupation VARCHAR(255),
    background TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 스토리 테이블 (벡터 임베딩 포함)
CREATE TABLE IF NOT EXISTS stories (
    id BIGSERIAL PRIMARY KEY,
    character_id BIGINT NOT NULL REFERENCES characters(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    embedding_vector vector(1536),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 채팅 메시지 테이블
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    character_id BIGINT NOT NULL REFERENCES characters(id) ON DELETE CASCADE,
    message TEXT NOT NULL,
    message_type VARCHAR(20) NOT NULL CHECK (message_type IN ('USER', 'AI')),
    session_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_chat_messages_user_id ON chat_messages(user_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_character_id ON chat_messages(character_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_session_id ON chat_messages(session_id);
CREATE INDEX IF NOT EXISTS idx_stories_character_id ON stories(character_id);
CREATE INDEX IF NOT EXISTS idx_stories_embedding_vector ON stories USING ivfflat (embedding_vector vector_cosine_ops);

-- 기본 데이터 삽입 (선택사항)
INSERT INTO characters (name, description, personality, speaking_style, age, occupation, background) 
VALUES 
    ('AI Assistant', 'A helpful AI companion', 'Friendly and supportive', 'Warm and encouraging', 25, 'AI Assistant', 'Created to help users with various tasks')
ON CONFLICT DO NOTHING;