package com.datewithai.global.config;

import com.datewithai.domain.character.entity.Character;
import com.datewithai.domain.character.service.CharacterService;
import com.datewithai.domain.character.repository.CharacterRepository;
import com.datewithai.domain.user.entity.User;
import com.datewithai.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final CharacterService characterService;
    private final CharacterRepository characterRepository;
    private final UserService userService;
    
    @Autowired
    public DataInitializer(CharacterService characterService, CharacterRepository characterRepository, UserService userService) {
        this.characterService = characterService;
        this.characterRepository = characterRepository;
        this.userService = userService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        try {
            // 기본 사용자 생성
            if (userService.findByUsername("guest").isEmpty()) {
                createGuestUser();
            }
            
            // 우리가 원하는 캐릭터가 있는지 확인
            boolean hasHoshinoAi = characterRepository.findByName("호시노 아이").isPresent();
            boolean hasShinobu = characterRepository.findByName("시노부").isPresent();
            
            System.out.println("현재 캐릭터 수: " + characterRepository.count());
            System.out.println("호시노 아이 존재: " + hasHoshinoAi);
            System.out.println("시노부 존재: " + hasShinobu);
            
            if (!hasHoshinoAi || !hasShinobu) {
                System.out.println("새 캐릭터 생성 중...");
                createInitialCharacters();
            }
        } catch (Exception e) {
            System.out.println("⚠️ 데이터 초기화 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            System.out.println("기존 데이터를 사용합니다.");
        }
    }
    
    private void createInitialCharacters() {
        // 호시노 아이 캐릭터
        Character hoshinoAi = new Character();
        hoshinoAi.setName("호시노 아이");
        hoshinoAi.setAge(16);
        hoshinoAi.setOccupation("아이돌");
        hoshinoAi.setDescription("B-KOMACHI의 센터로 활동하는 아이돌. 항상 밝고 에너지 넘치는 성격으로 팬들에게 사랑받고 있다. '아이돌은 거짓말이 일'이라고 말하며 프로 의식이 강하다.");
        hoshinoAi.setPersonality("밝고 활발하며 프로 의식이 강함. 팬들을 사랑하고 무대에서 빛나는 것을 좋아함. 때로는 진지한 면도 보임");
        hoshinoAi.setSpeakingStyle("밝고 친근한 말투, '~이야', '~네!' 같은 표현을 자주 사용");
        hoshinoAi.setProfileImageUrl("/images/hoshino-ai.jpg");
        characterService.save(hoshinoAi);
        
        // 시노부 캐릭터  
        Character shinobu = new Character();
        shinobu.setName("시노부");
        shinobu.setAge(598); 
        shinobu.setOccupation("전 뱀피어");
        shinobu.setDescription("키스 샷 아세롤라 오리온 하트 언더 블레이드라는 긴 이름을 가진 전 뱀피어. 현재는 힘을 잃고 어린 소녀의 모습을 하고 있다. 고풍스럽고 우아한 말투를 사용한다.");
        shinobu.setPersonality("고집이 세고 프라이드가 높음. 과거 뱀피어 왕이었던 자존심을 가지고 있으며, 때로는 차갑지만 은근히 정이 많음");
        shinobu.setSpeakingStyle("고풍스럽고 격식 있는 말투, '~이다', '~하느냐' 같은 고전적 표현 사용");
        shinobu.setProfileImageUrl("/images/shinobu.jpg");
        characterService.save(shinobu);
        
        System.out.println("✅ 초기 캐릭터 데이터가 생성되었습니다.");
        System.out.println("   - 호시노 아이 (아이돌)");
        System.out.println("   - 시노부 (전 뱀피어)");
    }
    
    private void createGuestUser() {
        User guest = new User();
        guest.setUsername("guest");
        guest.setPassword(""); // 빈 비밀번호 (로그인 기능 없음)
        guest.setEmail("guest@datewithai.com");
        userService.save(guest);
        System.out.println("✅ 게스트 사용자가 생성되었습니다.");
    }
}