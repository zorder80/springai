package com.flavio.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiModerationModel;
import org.springframework.ai.openai.OpenAiModerationOptions;
import org.springframework.ai.openai.api.OpenAiModerationApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
configurazione per la chat con memoria
 */
@Configuration
public class ChatConfiguration {

    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository repository) {
        // Usa memoria a finestra (ultimo N messaggi)
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(20)   // mantieni fino a 20 messaggi
                .build();
    }

    @Bean
    public ChatMemoryRepository inMemoryChatMemoryRepository() {
        // repository in memoria (non persistente)
        return new InMemoryChatMemoryRepository();
    }

    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory chatMemory) {
        // costruiamo l’advisor
        return MessageChatMemoryAdvisor.builder(chatMemory)
                .order(0)  // ordine degli advisor, se ne hai più di uno
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel,
                                 MessageChatMemoryAdvisor memoryAdvisor) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(memoryAdvisor)
                .build();
    }

}
