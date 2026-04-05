package com.lotusluna.review.provider;

import cn.hutool.json.JSONUtil;
import com.lotusluna.review.model.Prompt;
import com.lotusluna.review.model.QwenRequest;
import com.lotusluna.review.model.QwenResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

public class SiliconFlowProvider implements LLMProvider {
    private final String apiKey;
    private final String model;

    public SiliconFlowProvider() {
        this.apiKey = System.getenv("SILICONFLOW_API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new IllegalStateException("SILICONFLOW_API_KEY environment variable is not set or is empty");
        }
        this.model = loadModel();
    }

    private String loadModel() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            // use default
        }
        return props.getProperty("siliconflow.model", "Pro/Qwen/Qwen2.5-Coder-7B-Instruct");
    }

    @Override
    public String call(List<Prompt> messages, String systemPrompt) throws Exception {
        URL url = new URL("https://api.siliconflow.cn/v1/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        QwenRequest request = new QwenRequest();
        request.setModel(model);
        request.setMessages(messages);

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

        QwenResponse response = JSONUtil.toBean(content, QwenResponse.class);
        List<QwenResponse.Choice> choices = response.getChoices();
        if (choices == null || choices.isEmpty()) {
            throw new IOException("Empty response choices from SiliconFlow API");
        }
        return choices.get(0).getMessage().getContent();
    }
}