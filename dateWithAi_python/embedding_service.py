import httpx
import json
from typing import List, Optional
from config import Config

class EmbeddingService:
    def __init__(self):
        self.ollama_base_url = Config.OLLAMA_BASE_URL
        self.model = Config.EMBEDDING_MODEL
        self.client = httpx.Client(timeout=60.0)
        
    def count_tokens(self, text: str) -> int:
        # 대략적인 토큰 수 계산 (단어 기반)
        return len(text.split())
    
    def chunk_text(self, text: str, max_tokens: int = 8000) -> List[str]:
        chunks = []
        sentences = text.split('.')
        current_chunk = ""
        
        for sentence in sentences:
            sentence = sentence.strip() + '.'
            if self.count_tokens(current_chunk + sentence) <= max_tokens:
                current_chunk += sentence
            else:
                if current_chunk:
                    chunks.append(current_chunk.strip())
                current_chunk = sentence
        
        if current_chunk:
            chunks.append(current_chunk.strip())
        
        return chunks
    
    def create_embedding(self, text: str) -> List[float]:
        try:
            response = self.client.post(
                f"{self.ollama_base_url}/api/embeddings",
                json={
                    "model": self.model,
                    "prompt": text
                }
            )
            if response.status_code == 200:
                result = response.json()
                return result.get("embedding", [])
            else:
                print(f"임베딩 생성 오류: HTTP {response.status_code}")
                return None
        except Exception as e:
            print(f"임베딩 생성 오류: {e}")
            return None
    
    def create_embeddings_batch(self, texts: List[str]) -> List[List[float]]:
        embeddings = []
        for text in texts:
            embedding = self.create_embedding(text)
            if embedding:
                embeddings.append(embedding)
            else:
                print(f"배치 임베딩 처리 중 오류: {text[:50]}...")
        return embeddings
    
    def process_text_file(self, file_path: str) -> List[dict]:
        try:
            with open(file_path, 'r', encoding='utf-8') as file:
                content = file.read()
            
            chunks = self.chunk_text(content)
            embeddings = []
            
            for i, chunk in enumerate(chunks):
                embedding = self.create_embedding(chunk)
                if embedding:
                    embeddings.append({
                        'chunk_id': i,
                        'text': chunk,
                        'embedding': embedding,
                        'source_file': file_path
                    })
            
            return embeddings
        except Exception as e:
            print(f"텍스트 파일 처리 오류: {e}")
            return []