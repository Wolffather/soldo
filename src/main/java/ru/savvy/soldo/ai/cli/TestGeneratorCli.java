package ru.savvy.soldo.ai.cli;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import ru.savvy.soldo.SoldoApplication;
import ru.savvy.soldo.ai.SkillLoader;
import ru.savvy.soldo.ai.TestGenerationService;

public class TestGeneratorCli {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("❌ Укажи сценарий в аргументах командной строки.");
            System.err.println("Пример: java -cp target/soldo.jar ru.savvy.soldo.ai.cli.TestGeneratorCli \"GET /events\"");
            System.exit(1);
        }

        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(SoldoApplication.class)
                .web(WebApplicationType.NONE)
                .run(args)) {

            String userRequest = String.join(" ", args);
            String skillName = "api";
            String fileName = "GeneratedTest_" + System.currentTimeMillis();

            SkillLoader skillLoader = context.getBean(SkillLoader.class);
            TestGenerationService testGenerationService = context.getBean(TestGenerationService.class);

            String skillContent = skillLoader.loadSkill(skillName);
            testGenerationService.generateTest(skillContent, userRequest, fileName);

            System.out.println("✅ Тест сохранён!");
        }
    }
}