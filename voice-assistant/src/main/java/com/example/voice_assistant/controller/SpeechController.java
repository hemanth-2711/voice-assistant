package com.example.voice_assistant.controller;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.voice_assistant.service.GeminiService;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class SpeechController {

    private final GeminiService geminiService;

    public SpeechController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/transcribe")
    public ResponseEntity<Map<String, String>> transcribeAudio(@RequestBody byte[] audioBytes) {
        Map<String, String> response = new HashMap<>();

        try (SpeechClient speechClient = SpeechClient.create()) {
            // Step 1: Speech-to-Text conversion
            RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setLanguageCode("en-US")
                .setSampleRateHertz(44100)
                .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(ByteString.copyFrom(audioBytes))
                .build();

            RecognizeResponse apiResponse = speechClient.recognize(config, audio);

            String transcription = apiResponse.getResultsList().stream()
                .filter(result -> !result.getAlternativesList().isEmpty())
                .map(result -> result.getAlternatives(0).getTranscript())
                .collect(Collectors.joining("\n"));

            // Step 2: Process with Gemini AI
            String geminiResponse = geminiService.processQuery(transcription);
            
            // Step 3: Return final response
            response.put("response", geminiResponse);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String errorMessage = "Processing failed: " + e.getMessage();
            response.put("error", errorMessage);
            
            // Differentiate error types
            if (e.getMessage().contains("INVALID_ARGUMENT")) {
                return ResponseEntity.badRequest().body(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }
    }
}
