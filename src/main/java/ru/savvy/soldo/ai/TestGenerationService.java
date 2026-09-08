package ru.savvy.soldo.ai;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@AllArgsConstructor
public class TestGenerationService {

    private final ChatModel chatModel;

    /**
     * Генерирует тест на основе скилла (системный промпт) и запроса пользователя.
     */
    public String generateTest(String skillContent, String userRequest, String fileName) throws IOException {
        // 1. Собираем промпт
        var systemMsg = new SystemMessage(skillContent);
        var userMsg = new UserMessage("Сгенерируй тест для следующего сценария:\n" + userRequest);
        var prompt = new Prompt(systemMsg, userMsg);

        // 2. Вызываем DeepSeek
        String rawResponse = chatModel.call(prompt)
                .getResult()
                .getOutput()
                .getText();

        // 3. Очищаем от ```java
        String cleanCode = cleanGeneratedCode(rawResponse);

        // 4. Сохраняем в файл
        Path testDir = Paths.get("src/test/java/ru/savvy/soldo/generated");
        Files.createDirectories(testDir);
        Path filePath = testDir.resolve(fileName + ".java");
        Files.writeString(filePath, cleanCode);

        System.out.println("✅ Тест сохранён: " + filePath.toAbsolutePath());
        return cleanCode;
    }

    private String cleanGeneratedCode(String raw) {
        return raw.replaceAll("(?s)```java\\s*", "")
                .replaceAll("(?s)```\\s*$", "")
                .trim();
    }
}