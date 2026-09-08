package ru.savvy.soldo.ai;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class SkillLoader {

    private static final String SKILLS_DIR = "skills";
    private static final String DEFAULT_SKILL = "default.md";

    public String loadSkill(String skillName) throws IOException {
        // Если имя не указано, берем default
        String fileName = (skillName == null || skillName.isBlank()) ? DEFAULT_SKILL : skillName;
        // Если имя не заканчивается на .md, добавляем
        if (!fileName.endsWith(".md") && !fileName.endsWith(".txt")) {
            fileName = fileName + ".md";
        }

        Path skillPath = Paths.get(SKILLS_DIR, fileName);
        if (!Files.exists(skillPath)) {
            System.err.println("⚠️ Скилл не найден: " + skillPath.toAbsolutePath() + ". Использую встроенный скилл.");
            return getDefaultFallbackSkill();
        }
        return Files.readString(skillPath);
    }

    // Если файл не найден, чтобы не падать
    private String getDefaultFallbackSkill() {
        return "Ты — эксперт по Java и Spring Boot. Напиши JUnit + RestAssured тест для API.";
    }
}