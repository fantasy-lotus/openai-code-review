package com.lotusluna.review;

import com.lotusluna.review.model.Prompt;
import com.lotusluna.review.provider.AnthropicProvider;
import com.lotusluna.review.provider.LLMProvider;
import com.lotusluna.review.provider.SiliconFlowProvider;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DiffCodeReview {
    private static final String CONFIG_PATH = "config.properties";
    private static final String SYSTEM_PROMPT = "You are a senior architect, proficient in various scenario solutions, architecture design, and programming languages.";

    public static void main(String[] args) throws Exception {
        // 1. 读取检出代码
        ProcessBuilder processBuilder = new ProcessBuilder("git", "diff", "--unified=0");
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder diffCode = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            diffCode.append(line);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("git diff failed");
        }

        // 2. 代码评审
        String providerName = loadProviderFromConfig();
        LLMProvider provider = createProvider(providerName);

        List<Prompt> messages = new ArrayList<>();
        messages.add(new Prompt("user", "Please review the following git diff and provide your feedback and suggestions:"));
        messages.add(new Prompt("user", diffCode.toString()));

        String systemPrompt = getSystemPrompt();
        String info = provider.call(messages, systemPrompt);
        if (info == null || info.isEmpty()) {
            throw new RuntimeException("code review failed");
        }
        System.out.println(info);
    }

    private static String loadProviderFromConfig() {
        Properties props = new Properties();
        try (InputStream is = DiffCodeReview.class.getClassLoader().getResourceAsStream(CONFIG_PATH)) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            // use default
        }
        return props.getProperty("provider", "siliconflow");
    }

    private static LLMProvider createProvider(String providerName) {
        if ("anthropic".equalsIgnoreCase(providerName)) {
            return new AnthropicProvider();
        }
        return new SiliconFlowProvider();
    }

    private static String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }
}