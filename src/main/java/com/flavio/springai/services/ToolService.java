package com.flavio.springai.services;

import com.flavio.springai.tools.DucatiTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;


@Service
public class ToolService {

	private ChatClient chatClient;

	public ToolService(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	public String callAgent(String question){
		return chatClient.prompt(question).tools(new DucatiTool()).call().content();
	}

}
