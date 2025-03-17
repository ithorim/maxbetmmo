package com.tihomir.maxbet.config;

import com.tihomir.maxbet.entity.CharacterClass;
import com.tihomir.maxbet.entity.Item;
import com.tihomir.maxbet.entity.Character;
import com.tihomir.maxbet.repository.CharacterClassRepository;
import com.tihomir.maxbet.repository.CharacterRepository;
import com.tihomir.maxbet.repository.ItemRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DbSeeder implements CommandLineRunner {
    private final CharacterRepository characterRepository;
    private final CharacterClassRepository characterClassRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // only seed if db is empty
        if (characterRepository.count() > 0) {
            System.out.println("Database already has data, skipping seeding");
            return;
        }

        // create character classes
        // warrior
        CharacterClass warrior = CharacterClass.builder()
                .name("Warrior")
                .description("Melee fighter")
                .build();
        characterClassRepository.save(warrior);

        // mage
        CharacterClass mage = CharacterClass.builder()
                .name("Mage")
                .description("Spell caster")
                .build();
        characterClassRepository.save(mage);

        // rogue
        CharacterClass rogue = CharacterClass.builder()
                .name("Rogue")
                .description("Stealthy assassin")
                .build();
        characterClassRepository.save(rogue);

        // priest
        CharacterClass priest = CharacterClass.builder()
                .name("Priest")
                .description("Healer of Light")
                .build();
        characterClassRepository.save(priest);

        // create characters
        Character tihomir = Character.builder()
                .name("Tihomir")
                .health(100)
                .mana(0)
                .baseStrength(10)
                .baseAgility(5)
                .baseIntelligence(0)
                .baseFaith(10)
                .characterClass(warrior)
                .build();
        characterRepository.save(tihomir);

        Character tihiubica = Character.builder()
                .name("Tihiubica")
                .health(100)
                .mana(0)
                .baseStrength(0)
                .baseAgility(15)
                .baseIntelligence(0)
                .baseFaith(5)
                .characterClass(rogue)
                .build();
        characterRepository.save(tihiubica);

        Character gandalf = Character.builder()
                .name("Gandalf")
                .health(80)
                .mana(120)
                .baseStrength(5)
                .baseAgility(6)
                .baseIntelligence(12)
                .baseFaith(10)
                .characterClass(mage)
                .build();
        characterRepository.save(gandalf);

        // create items and assign them to characters
        Item sword = Item.builder()
                .name("Sword")
                .description("A sharp blade")
                .bonusStrength(5)
                .bonusAgility(2)
                .bonusIntelligence(0)
                .bonusFaith(0)
                .build();

        Item staff = Item.builder()
                .name("Staff")
                .description("A magical staff")
                .bonusStrength(1)
                .bonusAgility(1)
                .bonusIntelligence(5)
                .bonusFaith(3)
                .build();

        Item shield = Item.builder()
                .name("Shield")
                .description("A sturdy shield")
                .bonusStrength(3)
                .bonusAgility(0)
                .bonusIntelligence(0)
                .bonusFaith(2)
                .build();

        // save the items
        itemRepository.saveAll(List.of(sword, staff, shield));
        // need to getItems first so that I don't break JPA relationship tracking
        // new ArrayList<>(List.of(shield)) would surely break it
        // .add w/o .getItems might bypass it
        // better to be safe like this
        tihomir.getItems().add(shield);
        tihiubica.getItems().add(sword);
        gandalf.getItems().add(staff);

        // save chars again to update their items
        characterRepository.saveAll(List.of(tihomir, tihiubica, gandalf));
        System.out.println("Database seeded successfully");
    }
}
