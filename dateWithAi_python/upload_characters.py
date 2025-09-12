#!/usr/bin/env python3

import sys
import os
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from embedding_service import EmbeddingService
from vector_db import VectorDatabase

def upload_character_files():
    """캐릭터 텍스트 파일들을 임베딩해서 데이터베이스에 업로드"""
    
    # 서비스 초기화
    embedding_service = EmbeddingService()
    vector_db = VectorDatabase()
    
    # 테이블 생성
    vector_db.create_tables()
    
    # 캐릭터 파일 목록
    character_files = [
        "hoshino ai_character.txt",
        "shinobu_character.txt"
    ]
    
    for file_path in character_files:
        print(f"\n=== {file_path} 처리 중 ===")
        
        # 기존 데이터 삭제 (있다면)
        vector_db.delete_embeddings_by_source(file_path)
        
        # 텍스트 파일 처리 및 임베딩 생성
        embeddings_data = embedding_service.process_text_file(file_path)
        
        if embeddings_data:
            # 데이터베이스에 저장
            vector_db.insert_embeddings(embeddings_data)
            print(f"✅ {file_path}: {len(embeddings_data)}개 청크 저장 완료")
        else:
            print(f"❌ {file_path}: 임베딩 생성 실패")
    
    print("\n=== 업로드 완료 ===")
    
    # 저장된 소스 파일 목록 확인
    sources = vector_db.get_all_sources()
    print(f"저장된 소스 파일: {sources}")
    
    # 연결 종료
    vector_db.close_connection()

if __name__ == "__main__":
    upload_character_files()