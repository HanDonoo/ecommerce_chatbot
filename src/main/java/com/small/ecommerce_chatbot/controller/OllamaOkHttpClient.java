package com.small.ecommerce_chatbot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OllamaOkHttpClient {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "deepseek-r1:1.5b";  // 可更改为不同版本的 DeepSeek
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)  // 连接超时时间
            .readTimeout(90, TimeUnit.SECONDS)     // 读取超时时间
            .writeTimeout(90, TimeUnit.SECONDS)    // 写入超时时间
            .build();

    /**
     * 发送请求到 Ollama 服务器，并返回 DeepSeek 模型的回答
     * @param prompt 用户输入的查询
     * @return DeepSeek 模型的响应
     */
    public static String queryOllama(String prompt) {
        RequestBody body = RequestBody.create(
                "{\"model\":\"" + MODEL_NAME + "\", \"prompt\":\"" + prompt + "\", \"stream\": false}",
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url(OLLAMA_URL)
                .post(body)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return parseResult(response.body().string());
            } else {
                return "Error: " + response.code() + " - " + response.message();
            }
        } catch (IOException e) {
            return "Request failed: " + e.getMessage();
        }
    }

    public static String parseResult(String result){
        JSONObject jsonObject = JSON.parseObject(result);

        String responseText = jsonObject.getString("response");

        // 处理无用的 HTML 标记
        responseText = responseText.replaceAll("<.*?>", "").trim();

        return responseText;
    }

}


