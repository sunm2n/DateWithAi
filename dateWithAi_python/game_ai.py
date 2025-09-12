from typing import Dict, List, Optional
from embedding_service import EmbeddingService
from vector_db import VectorDatabase
from ai_response import AIResponseGenerator
from config import Config

class GameAI:
    def __init__(self):
        self.embedding_service = EmbeddingService()
        self.vector_db = VectorDatabase()
        self.ai_generator = AIResponseGenerator()
        self.conversation_history = []
        
    def initialize_database(self):
        self.vector_db.create_tables()
        
    def upload_character_knowledge(self, file_path: str, character_id: str = None) -> Dict:
        embeddings_data = self.embedding_service.process_text_file(file_path)
        
        if embeddings_data:
            for data in embeddings_data:
                data['source_file'] = f"{character_id}_{file_path}" if character_id else file_path
            
            self.vector_db.insert_embeddings(embeddings_data)
            
            return {
                "success": True,
                "message": f"캐릭터 지식 업로드 완료: {len(embeddings_data)}개 청크",
                "chunks": len(embeddings_data)
            }
        
        return {"success": False, "message": "파일 처리 실패"}
    
    def chat_with_character(self, 
                           user_message: str,
                           character_id: str = None,
                           character_info: str = None,
                           emotion: str = None,
                           emotion_intensity: float = 0.5) -> Dict:
        
        query_embedding = self.embedding_service.create_embedding(user_message)
        if not query_embedding:
            return {"success": False, "message": "메시지 처리 실패"}
        
        context_chunks = self.vector_db.similarity_search(
            query_embedding=query_embedding,
            limit=Config.MAX_SEARCH_RESULTS,
            threshold=Config.SIMILARITY_THRESHOLD
        )
        
        if character_id:
            context_chunks = [
                chunk for chunk in context_chunks 
                if character_id in chunk.get('source_file', '')
            ]
        
        if emotion:
            response = self.ai_generator.generate_emotion_response(
                emotion=emotion,
                context=user_message,
                intensity=emotion_intensity
            )
        else:
            response = self.ai_generator.generate_game_response(
                user_message=user_message,
                context_chunks=context_chunks,
                character_info=character_info
            )
        
        conversation_entry = {
            "user_message": user_message,
            "ai_response": response,
            "character_id": character_id,
            "context_count": len(context_chunks),
            "avg_similarity": sum(chunk.get('similarity', 0) for chunk in context_chunks) / len(context_chunks) if context_chunks else 0
        }
        
        self.conversation_history.append(conversation_entry)
        
        return {
            "success": True,
            "response": response,
            "context_used": len(context_chunks),
            "avg_similarity": conversation_entry["avg_similarity"],
            "conversation_id": len(self.conversation_history) - 1
        }
    
    def get_conversation_history(self, character_id: str = None) -> List[Dict]:
        if character_id:
            return [
                entry for entry in self.conversation_history 
                if entry.get('character_id') == character_id
            ]
        return self.conversation_history
    
    def clear_conversation_history(self, character_id: str = None):
        if character_id:
            self.conversation_history = [
                entry for entry in self.conversation_history 
                if entry.get('character_id') != character_id
            ]
        else:
            self.conversation_history = []
    
    def get_character_knowledge_stats(self, character_id: str = None) -> Dict:
        sources = self.vector_db.get_all_sources()
        
        if character_id:
            character_sources = [
                source for source in sources 
                if character_id in source
            ]
            return {
                "character_id": character_id,
                "knowledge_sources": len(character_sources),
                "sources": character_sources
            }
        
        return {
            "total_sources": len(sources),
            "sources": sources
        }
    
    def remove_character_knowledge(self, character_id: str):
        sources = self.vector_db.get_all_sources()
        character_sources = [
            source for source in sources 
            if character_id in source
        ]
        
        for source in character_sources:
            self.vector_db.delete_embeddings_by_source(source)
        
        return {
            "success": True,
            "message": f"{character_id}의 지식 {len(character_sources)}개 소스 삭제 완료"
        }
    
    def close(self):
        self.vector_db.close_connection()