package com.itzixi.service;

import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.List;

public interface OllamaService {

    public Object aiOllamaChat(String msg);

    public Flux<ChatResponse> aiOllamaStream1(String msg);

    public List<String> aiOllamaStream2(String msg);

    public void doDoctorStreamV3(String userName, String message);

}
