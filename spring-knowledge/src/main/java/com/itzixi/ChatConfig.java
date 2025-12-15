//package com.itzixi;
//
//import jakarta.annotation.Resource;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.openai.OpenAiChatModel;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @ClassName ChatConfig
// * @Author 陈壮壮
// * @Version 1.0
// * @Description ChatConfig
// **/
//@Configuration
//public class ChatConfig {
//
//    @Resource
//    private OpenAiChatModel openAiChatModel;
//
//    @Bean
//    public ChatClient chatClient() {
//        return ChatClient
//                .builder(openAiChatModel)
//                .build();
//    }
//}