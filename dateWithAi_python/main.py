from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional
import uvicorn

from embedding_service import EmbeddingService
from vector_db import VectorDatabase
from ai_response import AIResponseGenerator
from config import Config

app = FastAPI(title="DateWithAI Game Logic", version="1.0.0")

embedding_service = EmbeddingService()
vector_db = VectorDatabase()
ai_generator = AIResponseGenerator()

class ChatRequest(BaseModel):
    message: str
    character_id: Optional[str] = None
    character_info: Optional[str] = None
    emotion: Optional[str] = None
    emotion_intensity: Optional[float] = 0.5

class ChatResponse(BaseModel):
    response: str
    context_used: List[dict]
    similarity_scores: List[float]

class FileUploadRequest(BaseModel):
    file_path: str
    character_id: Optional[str] = None

@app.on_event("startup")
async def startup_event():
    vector_db.create_tables()
    print("AI 게임 로직 서버 시작됨")

@app.on_event("shutdown")
async def shutdown_event():
    vector_db.close_connection()
    print("AI 게임 로직 서버 종료됨")

@app.post("/chat", response_model=ChatResponse)
async def chat_with_ai(request: ChatRequest):
    try:
        query_embedding = embedding_service.create_embedding(request.message)
        if not query_embedding:
            raise HTTPException(status_code=500, detail="임베딩 생성 실패")
        
        context_chunks = vector_db.similarity_search(
            query_embedding=query_embedding,
            limit=Config.MAX_SEARCH_RESULTS,
            threshold=Config.SIMILARITY_THRESHOLD
        )
        
        if request.emotion:
            response = ai_generator.generate_emotion_response(
                emotion=request.emotion,
                context=request.message,
                intensity=request.emotion_intensity
            )
        else:
            response = ai_generator.generate_game_response(
                user_message=request.message,
                context_chunks=context_chunks,
                character_info=request.character_info
            )
        
        similarity_scores = [chunk.get('similarity', 0) for chunk in context_chunks]
        
        return ChatResponse(
            response=response,
            context_used=context_chunks,
            similarity_scores=similarity_scores
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"채팅 처리 오류: {str(e)}")

@app.post("/upload-knowledge")
async def upload_knowledge(request: FileUploadRequest):
    try:
        embeddings_data = embedding_service.process_text_file(request.file_path)
        
        if not embeddings_data:
            raise HTTPException(status_code=400, detail="파일 처리 실패")
        
        vector_db.insert_embeddings(embeddings_data)
        
        return {
            "message": f"지식 베이스 업로드 완료",
            "file_path": request.file_path,
            "chunks_processed": len(embeddings_data),
            "character_id": request.character_id
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"지식 베이스 업로드 오류: {str(e)}")

@app.delete("/knowledge/{source_file}")
async def delete_knowledge(source_file: str):
    try:
        vector_db.delete_embeddings_by_source(source_file)
        return {"message": f"{source_file} 지식 베이스 삭제 완료"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"지식 베이스 삭제 오류: {str(e)}")

@app.get("/knowledge/sources")
async def get_knowledge_sources():
    try:
        sources = vector_db.get_all_sources()
        return {"sources": sources}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"소스 목록 조회 오류: {str(e)}")

@app.get("/health")
async def health_check():
    return {"status": "healthy", "message": "AI 게임 로직 서버 정상 동작"}

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)