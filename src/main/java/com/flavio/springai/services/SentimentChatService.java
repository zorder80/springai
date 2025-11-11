package com.flavio.springai.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@Slf4j
public class SentimentChatService {

	private ChatClient chatClient;

	@Value("classpath:/prompt-templates/sentiment.st")
	private Resource sentimentTemplate;

	public SentimentChatService(ChatClient chatClient) {
		this.chatClient = chatClient;
	}
	
	public String analyse(String question) {
		PromptTemplate promptTemplate = new PromptTemplate(sentimentTemplate);
		Prompt prompt = promptTemplate.create(Map.of("text", question));
		String response = chatClient.prompt(prompt).call().chatResponse().getResult().getOutput().getText();
		log.info("Sentiment analysis response: {}", response);
		return response;
	}

}
