import psycopg2
from psycopg2.extras import RealDictCursor
import numpy as np
from typing import List, Dict, Optional, Tuple
from config import Config

class VectorDatabase:
    def __init__(self):
        self.connection = None
        self.connect()
        
    def connect(self):
        try:
            self.connection = psycopg2.connect(
                host=Config.POSTGRES_HOST,
                port=Config.POSTGRES_PORT,
                database=Config.POSTGRES_DB,
                user=Config.POSTGRES_USER,
                password=Config.POSTGRES_PASSWORD
            )
            self.connection.autocommit = True
            print("PostgreSQL 연결 성공")
        except Exception as e:
            print(f"PostgreSQL 연결 오류: {e}")
            
    def create_tables(self):
        create_extension_query = "CREATE EXTENSION IF NOT EXISTS vector;"
        
        create_table_query = """
        CREATE TABLE IF NOT EXISTS embeddings (
            id SERIAL PRIMARY KEY,
            chunk_id INTEGER,
            text TEXT NOT NULL,
            embedding VECTOR(1024),
            source_file TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
        """
        
        create_index_query = """
        CREATE INDEX IF NOT EXISTS embeddings_vector_idx 
        ON embeddings USING ivfflat (embedding vector_cosine_ops) 
        WITH (lists = 100);
        """
        
        try:
            with self.connection.cursor() as cursor:
                cursor.execute(create_extension_query)
                cursor.execute(create_table_query)
                cursor.execute(create_index_query)
            print("테이블 및 인덱스 생성 완료")
        except Exception as e:
            print(f"테이블 생성 오류: {e}")
    
    def insert_embeddings(self, embeddings_data: List[dict]):
        insert_query = """
        INSERT INTO embeddings (chunk_id, text, embedding, source_file)
        VALUES (%(chunk_id)s, %(text)s, %(embedding)s, %(source_file)s)
        """
        
        try:
            with self.connection.cursor() as cursor:
                for data in embeddings_data:
                    cursor.execute(insert_query, {
                        'chunk_id': data['chunk_id'],
                        'text': data['text'],
                        'embedding': data['embedding'],
                        'source_file': data['source_file']
                    })
            print(f"{len(embeddings_data)}개의 임베딩 저장 완료")
        except Exception as e:
            print(f"임베딩 저장 오류: {e}")
    
    def similarity_search(self, query_embedding: List[float], limit: int = 5, threshold: float = 0.7) -> List[Dict]:
        search_query = """
        SELECT id, chunk_id, text, source_file, created_at,
               1 - (embedding <=> %s::vector) as similarity
        FROM embeddings 
        WHERE 1 - (embedding <=> %s::vector) > %s
        ORDER BY embedding <=> %s::vector
        LIMIT %s;
        """
        
        try:
            with self.connection.cursor(cursor_factory=RealDictCursor) as cursor:
                cursor.execute(search_query, (
                    query_embedding, query_embedding, threshold, query_embedding, limit
                ))
                results = cursor.fetchall()
                
                return [dict(row) for row in results]
        except Exception as e:
            print(f"유사도 검색 오류: {e}")
            return []
    
    def delete_embeddings_by_source(self, source_file: str):
        delete_query = "DELETE FROM embeddings WHERE source_file = %s"
        
        try:
            with self.connection.cursor() as cursor:
                cursor.execute(delete_query, (source_file,))
                deleted_count = cursor.rowcount
            print(f"{source_file}의 {deleted_count}개 임베딩 삭제 완료")
        except Exception as e:
            print(f"임베딩 삭제 오류: {e}")
    
    def get_all_sources(self) -> List[str]:
        query = "SELECT DISTINCT source_file FROM embeddings"
        
        try:
            with self.connection.cursor() as cursor:
                cursor.execute(query)
                results = cursor.fetchall()
                return [row[0] for row in results]
        except Exception as e:
            print(f"소스 파일 목록 조회 오류: {e}")
            return []
    
    def close_connection(self):
        if self.connection:
            self.connection.close()
            print("데이터베이스 연결 종료")