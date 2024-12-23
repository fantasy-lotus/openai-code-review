package com.lotusluna.review;

import cn.hutool.json.JSONUtil;
import com.lotusluna.review.model.Prompt;
import com.lotusluna.review.model.QwenRequest;
import com.lotusluna.review.model.QwenResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DiffCodeReview {
    private static final String TOKEN = "sk-yqzobraqqlhwoilrnvckajfjkobwnauasdazieturopcvsez";
    public static void main(String[] args) throws Exception {
        // 1. 读取检出代码
        ProcessBuilder processBuilder = new ProcessBuilder("git","diff","--unified=0");
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder diffCode = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            diffCode.append(line);
        }

        int exitCode = process.waitFor();
        if(exitCode != 0) {
            throw new RuntimeException("git diff failed");
        }

        // 2. meta llama 代码评审
        String info = codeReview(diffCode.toString());
        if(info == null || info.isEmpty()){
            throw new RuntimeException("code review failed");
        }
        System.out.println(info);
    }

    private static String codeReview(String diffCode) throws Exception {
        URL url = new URL("https://api.siliconflow.cn/v1/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + TOKEN);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        QwenRequest request = new QwenRequest();
        request.setModel("Pro/Qwen/Qwen2.5-Coder-7B-Instruct");
        request.setMessages(new ArrayList<Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;
            {
                add(new Prompt("user", "你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言，请您根据git diff记录，对代码做出评审,并给出建议。代码如下:"));
                add(new Prompt("user", diffCode));
            }
        });

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = JSONUtil.toJsonStr(request).getBytes(StandardCharsets.UTF_8);
            os.write(input);
        }catch(Exception e){
            throw new RuntimeException("code review failed");
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;

        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        connection.disconnect();
        QwenResponse response = JSONUtil.toBean(content.toString(), QwenResponse.class);
        return response.getChoices().get(0).getMessage().getContent();
    }
}
