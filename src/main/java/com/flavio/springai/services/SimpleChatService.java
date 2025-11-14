package com.flavio.springai.services;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SimpleChatService {
	
	private ChatClient chatClient;
	
	public SimpleChatService(ChatClient chatClient) {
		this.chatClient = chatClient;
	}
	
	public String generateAnswer(String question) {
		return chatClient.prompt(question).call().chatResponse().getResult().getOutput().getText();
	}

}
