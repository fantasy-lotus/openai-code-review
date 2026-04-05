package com.lotusluna.review.provider;

import cn.hutool.json.JSONUtil;
import com.lotusluna.review.model.AnthropicMessage;
import com.lotusluna.review.model.AnthropicRequest;
import com.lotusluna.review.model.AnthropicResponse;
import com.lotusluna.review.model.Prompt;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AnthropicProvider implements LLMProvider {
    private static final String ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages";
    private final String apiKey;
    private final String model;
    private final int maxTokens;

    public AnthropicProvider() {
        this.apiKey = System.getenv("ANTHROPIC_API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new IllegalStateException("ANTHROPIC_API_KEY environment variable is not set or is empty");
        }
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            // use defaults
        }
        this.model = props.getProperty("anthropic.model", "claude-3-5-sonnet-20241022");
        this.maxTokens = Integer.parseInt(props.getProperty("anthropic.max_tokens", "1024"));
    }

    @Override
    public String call(List<Prompt> messages, String systemPrompt) throws Exception {
        URL url = new URL(ANTHROPIC_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("x-api-key", apiKey);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("anthropic-version", "2023-06-01");
        connection.setDoOutput(true);

        AnthropicRequest request = new AnthropicRequest();
        request.setModel(model);
        request.setMax_tokens(maxTokens);
        request.setSystem(systemPrompt);

        List<AnthropicMessage> anthropicMessages = new ArrayList<>();
        for (Prompt msg : messages) {
            AnthropicMessage am = new AnthropicMessage();
            am.setRole(msg.getRole());
            am.setContent(msg.getContent());
            anthropicMessages.add(am);
        }
        request.setMessages(anthropicMessages);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = JSONUtil.toJsonStr(request).getBytes(StandardCharsets.UTF_8);
            os.write(input);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("HTTP error " + responseCode + ": " + connection.getResponseMessage());
        }

        String content;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            StringBuilder contentBuilder = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                contentBuilder.append(inputLine);
            }
            content = contentBuilder.toString();
        }

        connection.disconnect();

        AnthropicResponse response = JSONUtil.toBean(content, AnthropicResponse.class);
        List<AnthropicResponse.ContentBlock> contentList = response.getContent();
        if (contentList == null || contentList.isEmpty()) {
            throw new IOException("Empty response content from Anthropic API");
        }
        return contentList.get(0).getText();
    }
}