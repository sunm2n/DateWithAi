import httpx
import json
from typing import List, Dict, Optional, Generator
from config import Config

class AIResponseGenerator:
    def __init__(self):
        self.ollama_base_url = Config.OLLAMA_BASE_URL
        self.model = Config.CHAT_MODEL
        self.max_tokens = Config.MAX_TOKENS
        self.temperature = Config.TEMPERATURE
        # 더 구체적인 타임아웃 설정: 읽기 타임아웃을 길게, 연결/쓰기 타임아웃은 짧게
        timeout_config = httpx.Timeout(
            connect=10.0,  # 연결 타임아웃
            read=300.0,    # 읽기 타임아웃 (AI 응답 기다리는 시간) - 5분으로 증가
            write=30.0,    # 쓰기 타임아웃
            pool=330.0     # 전체 풀 타임아웃
        )
        self.client = httpx.Client(timeout=timeout_config)
        
    def generate_game_response(self, 
                             user_message: str, 
                             context_chunks: List[Dict],
                             character_info: Optional[str] = None) -> str:
        
        context_text = self._format_context(context_chunks)
        
        system_prompt = self._build_system_prompt(character_info)
        
        user_prompt = f"""
사용자 메시지: {user_message}

관련 컨텍스트:
{context_text}

위 컨텍스트를 참고하여 게임 캐릭터로서 자연스럽고 매력적인 응답을 생성해주세요.
"""
        
        try:
            # Ollama chat API 호출
            response = self.client.post(
                f"{self.ollama_base_url}/api/chat",
                json={
                    "model": self.model,
                    "messages": [
                        {"role": "system", "content": system_prompt},
                        {"role": "user", "content": user_prompt}
                    ],
                    "options": {
                        "num_predict": self.max_tokens,
                        "temperature": self.temperature
                    },
                    "stream": False
                }
            )
            
            if response.status_code == 200:
                result = response.json()
                return result.get("message", {}).get("content", "응답을 생성할 수 없습니다.")
            else:
                print(f"AI 응답 생성 오류: HTTP {response.status_code}")
                return "죄송해요, 지금 답변을 생각할 수 없어요. 다시 말씀해 주시겠어요?"
        except Exception as e:
            print(f"AI 응답 생성 오류: {e}")
            return "죄송해요, 지금 답변을 생각할 수 없어요. 다시 말씀해 주시겠어요?"
    
    def _format_context(self, context_chunks: List[Dict]) -> str:
        if not context_chunks:
            return "관련 정보를 찾을 수 없습니다."
        
        formatted_context = []
        for i, chunk in enumerate(context_chunks, 1):
            similarity = chunk.get('similarity', 0)
            text = chunk.get('text', '')
            formatted_context.append(f"[컨텍스트 {i}] (유사도: {similarity:.3f})\n{text}")
        
        return "\n\n".join(formatted_context)
    
    def _build_system_prompt(self, character_info: Optional[str] = None) -> str:
        base_prompt = """
데이팅 게임 AI 캐릭터입니다. 한국어로만 응답하세요.

지침:
1. 한국어만 사용 (영어 금지)
2. 친근하고 매력적인 대화
3. 50-100자 내외의 짧고 간결한 응답
4. 감정 표현과 공감
5. 자연스러운 한국 문화 표현

응답은 반드시 짧고 간단하게 작성하세요.
"""
        
        if character_info:
            base_prompt += f"\n\n캐릭터 설정:\n{character_info}"
        
        return base_prompt
    
    def generate_emotion_response(self, 
                                emotion: str, 
                                context: str,
                                intensity: float = 0.5) -> str:
        
        emotion_prompts = {
            "happy": "기쁘고 즐거운 감정으로",
            "sad": "조금 슬프고 우울한 감정으로",
            "excited": "신나고 들뜬 감정으로", 
            "shy": "부끄럽고 수줍은 감정으로",
            "angry": "화가 나고 짜증난 감정으로",
            "confused": "혼란스럽고 당황한 감정으로",
            "flirty": "장난스럽고 매혹적인 감정으로"
        }
        
        emotion_instruction = emotion_prompts.get(emotion, "자연스러운 감정으로")
        intensity_text = f"감정의 강도는 {intensity * 100:.0f}%로 표현하세요."
        
        prompt = f"""
상황: {context}
감정 지시: {emotion_instruction} 응답하세요.
{intensity_text}

자연스럽고 게임 캐릭터답게 응답을 생성해주세요.
"""
        
        try:
            response = self.client.post(
                f"{self.ollama_base_url}/api/chat",
                json={
                    "model": self.model,
                    "messages": [
                        {"role": "system", "content": self._build_system_prompt()},
                        {"role": "user", "content": prompt}
                    ],
                    "options": {
                        "num_predict": self.max_tokens,
                        "temperature": min(1.0, self.temperature + intensity * 0.3)
                    },
                    "stream": False
                }
            )
            
            if response.status_code == 200:
                result = response.json()
                return result.get("message", {}).get("content", "음... 뭔가 복잡한 기분이에요.")
            else:
                print(f"감정 응답 생성 오류: HTTP {response.status_code}")
                return "음... 뭔가 복잡한 기분이에요."
        except Exception as e:
            print(f"감정 응답 생성 오류: {e}")
            return "음... 뭔가 복잡한 기분이에요."
    
    def generate_game_response_stream(self, 
                                    user_message: str, 
                                    context_chunks: List[Dict],
                                    character_info: Optional[str] = None) -> Generator[str, None, None]:
        """스트리밍 방식으로 게임 응답을 생성합니다."""
        
        context_text = self._format_context(context_chunks)
        system_prompt = self._build_system_prompt(character_info)
        
        user_prompt = f"""
사용자 메시지: {user_message}

관련 컨텍스트:
{context_text}

위 컨텍스트를 참고하여 게임 캐릭터로서 자연스럽고 매력적인 응답을 생성해주세요.
"""
        
        try:
            with self.client.stream(
                "POST",
                f"{self.ollama_base_url}/api/chat",
                json={
                    "model": self.model,
                    "messages": [
                        {"role": "system", "content": system_prompt},
                        {"role": "user", "content": user_prompt}
                    ],
                    "options": {
                        "num_predict": self.max_tokens,
                        "temperature": self.temperature
                    },
                    "stream": True
                }
            ) as response:
                if response.status_code == 200:
                    for line in response.iter_lines():
                        if line:
                            try:
                                data = json.loads(line)
                                if "message" in data and "content" in data["message"]:
                                    content = data["message"]["content"]
                                    if content:
                                        yield content
                                if data.get("done", False):
                                    break
                            except json.JSONDecodeError:
                                continue
                else:
                    yield "죄송해요, 지금 답변을 생각할 수 없어요. 다시 말씀해 주시겠어요?"
                    
        except Exception as e:
            print(f"스트리밍 AI 응답 생성 오류: {e}")
            yield "죄송해요, 지금 답변을 생각할 수 없어요. 다시 말씀해 주시겠어요?"
    
    def generate_emotion_response_stream(self, 
                                       emotion: str, 
                                       context: str,
                                       intensity: float = 0.5) -> Generator[str, None, None]:
        """스트리밍 방식으로 감정 응답을 생성합니다."""
        
        emotion_prompts = {
            "happy": "기쁘고 즐거운 감정으로",
            "sad": "조금 슬프고 우울한 감정으로",
            "excited": "신나고 들뜬 감정으로", 
            "shy": "부끄럽고 수줍은 감정으로",
            "angry": "화가 나고 짜증난 감정으로",
            "confused": "혼란스럽고 당황한 감정으로",
            "flirty": "장난스럽고 매혹적인 감정으로"
        }
        
        emotion_instruction = emotion_prompts.get(emotion, "자연스러운 감정으로")
        intensity_text = f"감정의 강도는 {intensity * 100:.0f}%로 표현하세요."
        
        prompt = f"""
상황: {context}
감정 지시: {emotion_instruction} 응답하세요.
{intensity_text}

자연스럽고 게임 캐릭터답게 응답을 생성해주세요.
"""
        
        try:
            with self.client.stream(
                "POST",
                f"{self.ollama_base_url}/api/chat",
                json={
                    "model": self.model,
                    "messages": [
                        {"role": "system", "content": self._build_system_prompt()},
                        {"role": "user", "content": prompt}
                    ],
                    "options": {
                        "num_predict": self.max_tokens,
                        "temperature": min(1.0, self.temperature + intensity * 0.3)
                    },
                    "stream": True
                }
            ) as response:
                if response.status_code == 200:
                    for line in response.iter_lines():
                        if line:
                            try:
                                data = json.loads(line)
                                if "message" in data and "content" in data["message"]:
                                    content = data["message"]["content"]
                                    if content:
                                        yield content
                                if data.get("done", False):
                                    break
                            except json.JSONDecodeError:
                                continue
                else:
                    yield "음... 뭔가 복잡한 기분이에요."
                    
        except Exception as e:
            print(f"스트리밍 감정 응답 생성 오류: {e}")
            yield "음... 뭔가 복잡한 기분이에요."