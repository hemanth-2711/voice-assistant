package com.example.voice_assistant.service;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final GenerativeModel model;

    public GeminiService(VertexAI vertexAI) {
        this.model = new GenerativeModel("gemini-2.0-flash-001", vertexAI);
    }

    public String processQuery(String userInput) throws Exception {
        GenerateContentResponse response = model.generateContent(
            ContentMaker.fromMultiModalData(
                "You are a voice assistant. Respond concisely.",
                userInput
            )
        );
        return ResponseHandler.getText(response);
    }
}

