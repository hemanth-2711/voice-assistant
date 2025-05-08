package com.example.voice_assistant.config;

import com.google.cloud.vertexai.VertexAI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {
    @Bean
    public VertexAI vertexAI() {
        // Replace with your actual project ID
        return new VertexAI("voice-assistant-project-459004", "us-central1");
    }
}
