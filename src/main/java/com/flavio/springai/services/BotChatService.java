package com.flavio.springai.services;

import com.flavio.springai.dtos.BotChatHistory;
import com.flavio.springai.dtos.BotChatMessage;
import com.flavio.springai.dtos.RagFromGoogleResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class BotChatService {

	private final ChatClient chatClient;

	private final VectorStore vectorStore;

	private BotChatHistory botChatHistory;

	@Value("classpath:/prompt-templates/bot-prompt-template.st")
	private Resource botPromptTemplate;

	public BotChatService(ChatClient chatClient,
                          VectorStore vectorStore) {
		this.chatClient = chatClient;
		this.vectorStore = vectorStore;
	}

	@PostConstruct
	private void postContstruct() {
		botChatHistory = new BotChatHistory(new ArrayList<>());
	}

	/**
	 * Performs a RAG (Retrieval-Augmented Generation) chat call by enriching the user's query with data from the vector store to perform a bot chat.
	 * @param query
	 * @return
	 */
	public String chat(String query) {
		String context = retrieveContext(query, 3).stream()
				.map(Document::getText)
				.collect(Collectors.joining("\n"));

		log.info("create LLM prompt with context taken from previous messages");
		PromptTemplate promptTemplate = new PromptTemplate(botPromptTemplate);
		Prompt prompt = promptTemplate.create(Map.of("query", query, "context", context));
		log.info("prompt "+prompt.getContents());

		String answer = chatClient.prompt(prompt).call().chatResponse().getResult().getOutput().getText();

		//salva la domanda e la risposta nel vector store
		saveToMemory(query, answer);

		//add history
		botChatHistory.messages().add(new BotChatMessage(query, answer));

		return answer;
	}

	/*
	 * save the user message and the answer to the vector store for store context
	 */
	private void saveToMemory(String userMessage, String answer) {
		log.info("save to memory: " + userMessage + " -> " + answer);
		Document doc = new Document("User: " + userMessage + ". Answer: " + answer);
		vectorStore.add(List.of(doc));
	}

	/*
	 * retrieve context from vector store
	 */
	public List<Document> retrieveContext(String userMessage, int maxResults) {
		log.info("retrieve relevant docs for context: " + userMessage);
		return vectorStore.similaritySearch(userMessage).stream().limit(maxResults).collect(Collectors.toList());
	}

	public BotChatHistory getBotChatHistory() {
		return botChatHistory;
	}

	public void resetBotChatHistory() {
		botChatHistory = new BotChatHistory(new ArrayList<>());
	}

}
