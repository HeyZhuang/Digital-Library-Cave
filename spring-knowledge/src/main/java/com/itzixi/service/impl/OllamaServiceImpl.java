package com.itzixi.service.impl;

import com.itzixi.service.ChatRecordService;
import com.itzixi.service.OllamaService;
import com.itzixi.utils.ChatTypeEnum;
import com.itzixi.utils.SSEMsgType;
import com.itzixi.utils.SSEServer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName OllamaServiceImpl
 * @Author 陈壮壮
 * @Version 1.0
 * @Description OllamaServiceImpl
 **/
@Slf4j
@Service
public class OllamaServiceImpl implements OllamaService {

    @Resource
    private OllamaChatModel ollamaChatModel;

    @Resource
    private ChatRecordService chatRecordService;

    @Override
    public Object aiOllamaChat(String msg) {
        return ollamaChatModel.call(msg);
    }

    @Override
    public Flux<ChatResponse> aiOllamaStream1(String msg) {
        // 代码执行到此处的时间  22:00:00 - 开始时间

        Prompt prompt = new Prompt(new UserMessage(msg));
        Flux<ChatResponse> streamResponse = ollamaChatModel.stream(prompt);

        // 代码执行到此处的时间  22:01:30 - 结束时间
        // 两个时间的时间差为1分30秒，则总计90秒

        return streamResponse;
    }

    @Override
    public List<String> aiOllamaStream2(String msg) {
        Prompt prompt = new Prompt(new UserMessage(msg));
        Flux<ChatResponse> streamResponse = ollamaChatModel.stream(prompt);

        List<String> list = streamResponse.toStream().map(chatResponse -> {
            String content = chatResponse.getResult().getOutput().getText();
//            System.out.println(content);
            log.info(content);
            return content;
        }).collect(Collectors.toList());

        return list;
    }

    @Override
    public void doDoctorStreamV3(String userName, String message) {

        // 保存用户发送的记录到数据库
        chatRecordService.saveChatRecord(userName, message, ChatTypeEnum.USER);

        Prompt prompt = new Prompt(new UserMessage(message));
        Flux<ChatResponse> streamResponse = ollamaChatModel.stream(prompt);

        List<String> list = streamResponse.toStream().map(chatResponse -> {
            String content = chatResponse.getResult().getOutput().getText();

            SSEServer.sendMessage(userName, content, SSEMsgType.ADD);

            log.info(content);
            return content;
        }).collect(Collectors.toList());

        SSEServer.sendMessage(userName, "GG", SSEMsgType.FINISH);

        // 保存AI回复的记录到数据库
        String htmlResult = "";
        for (String s : list) {
            htmlResult += s;
        }
        chatRecordService.saveChatRecord(userName, htmlResult, ChatTypeEnum.BOT);

    }

}
