from game_ai import GameAI
import asyncio

async def example_usage():
    game_ai = GameAI()
    
    print("=== DateWithAI 게임 AI 예제 ===")
    
    print("1. 데이터베이스 초기화...")
    game_ai.initialize_database()
    
    print("2. 캐릭터 지식 업로드 예제...")
    
    print("3. AI 캐릭터와 대화 예제...")
    
    conversation_examples = [
        {
            "message": "안녕하세요! 처음 뵙겠습니다.",
            "character_info": "당신은 친근하고 유쾌한 20대 여성입니다. 카페에서 일하고 있고, 커피를 좋아합니다."
        },
        {
            "message": "오늘 날씨가 정말 좋네요!",
            "emotion": "happy",
            "emotion_intensity": 0.8
        },
        {
            "message": "혹시 시간 있으실 때 같이 커피 한 잔 하실래요?",
            "emotion": "shy",
            "emotion_intensity": 0.6
        }
    ]
    
    for i, example in enumerate(conversation_examples, 1):
        print(f"\n--- 대화 예제 {i} ---")
        print(f"사용자: {example['message']}")
        
        result = game_ai.chat_with_character(
            user_message=example['message'],
            character_id="example_character",
            character_info=example.get('character_info'),
            emotion=example.get('emotion'),
            emotion_intensity=example.get('emotion_intensity', 0.5)
        )
        
        if result['success']:
            print(f"AI 캐릭터: {result['response']}")
            print(f"컨텍스트 사용: {result['context_used']}개")
            print(f"평균 유사도: {result['avg_similarity']:.3f}")
        else:
            print(f"오류: {result['message']}")
    
    print("\n4. 대화 히스토리 조회...")
    history = game_ai.get_conversation_history("example_character")
    print(f"총 {len(history)}개의 대화 기록")
    
    print("\n5. 지식 베이스 통계...")
    stats = game_ai.get_character_knowledge_stats("example_character")
    print(f"캐릭터 지식 소스: {stats['knowledge_sources']}개")
    
    game_ai.close()
    print("\n=== 예제 완료 ===")

def simple_usage_example():
    game_ai = GameAI()
    game_ai.initialize_database()
    
    result = game_ai.chat_with_character(
        user_message="안녕하세요!",
        character_info="친근한 게임 캐릭터입니다."
    )
    
    print(f"AI 응답: {result['response']}")
    game_ai.close()

if __name__ == "__main__":
    print("간단한 사용 예제:")
    simple_usage_example()
    
    print("\n" + "="*50 + "\n")
    
    print("상세 예제 실행:")
    asyncio.run(example_usage())