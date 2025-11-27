package com.flavio.springai.services;

import com.flavio.springai.dtos.BotChatAnswer;
import com.flavio.springai.dtos.BotChatQuestion;
import com.flavio.springai.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class BotChatService {

	private final ChatClient chatClient;

	private final ChatMemory chatMemory;

	private final ChatMemoryRepository chatMemoryRepository;

	private MessageChatMemoryAdvisor advisor;

	@Value("${chat.conversation_id_key:conversation_id}")
	private String conversationIdKey;

	@Value("classpath:/prompt-templates/bot-prompt-template.st")
	private Resource botPromptTemplate;

	public BotChatService(ChatClient chatClient, ChatMemory chatMemory, ChatMemoryRepository chatMemoryRepository) {
		this.chatClient = chatClient;
		this.chatMemory = chatMemory;
		this.chatMemoryRepository = chatMemoryRepository;
	}

	/**
	 * performs a chat call with memory
	 *
	 * */
	public BotChatAnswer chat(BotChatQuestion chatQuestion) {
		String conversationId = chatQuestion.conversationId() == null || chatQuestion.conversationId().isEmpty() ?
				StringUtil.generateRandomString(10) : chatQuestion.conversationId();
		log.info("conversationId " + conversationId + " - sending user message: " + chatQuestion.userMessage());

		// Crea l'advisor dinamicamente con il conversationId specifico
		createConversation(conversationId);

		// chiama la chat con l'advisor
		String answer = chatClient.prompt()
				.user(chatQuestion.userMessage())
				.advisors(advisor)
				.call()
				.content();
		log.info(" -> bot answer: " + answer);

		//recupera tutti i messaggi della conversazione
		List<String> messages = new ArrayList<>();
		chatMemoryRepository.findByConversationId(conversationId).stream().forEach(conversation -> {
			if(conversation.getMessageType().equals(MessageType.USER)){
				messages.add("User: " + conversation.getText());
				log.info("User: " + conversation.getText());
			}else {
				messages.add("Bot: " + conversation.getText());
				log.info("Bot: " + conversation.getText());
			}
		});
		return new BotChatAnswer(messages, conversationId);
	}

	public void resetConversation(String conversationId) {
		chatMemoryRepository.deleteByConversationId(conversationId);
		log.info("Conversation with id " + conversationId + " has been reset.");
	}

	private void createConversation(String conversationId) {
		if(chatMemoryRepository.findByConversationId(conversationId).isEmpty()) {
			log.info("Creating new conversation with id: " + conversationId);
			advisor = MessageChatMemoryAdvisor.builder(chatMemory)
					.conversationId(conversationId)
					.order(0)
					.build();
		}
	}

}
