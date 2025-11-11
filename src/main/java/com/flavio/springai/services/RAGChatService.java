package com.flavio.springai.services;

import com.flavio.springai.dtos.GoogleSearchResponse;
import com.flavio.springai.dtos.RagFromGoogleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class RAGChatService {

	private final ChatClient chatClient;

	private final VectorStore vectorStore;

	private final GoogleSearchService googleSearchService;

	@Value("classpath:/prompt-templates/rag-prompt-template.st")
	private Resource ragPromptTemplate;

	public RAGChatService(ChatClient chatClient,
						  VectorStore vectorStore,
						  GoogleSearchService googleSearchService) {
		this.chatClient = chatClient;
		this.vectorStore = vectorStore;
		this.googleSearchService = googleSearchService;
	}

	/**
	 * Performs a RAG (Retrieval-Augmented Generation) chat call by enriching the user's query with data from the vector store.
	 * @param query
	 * @return
	 */
	public String call(String query) {
		/*
		il modello AI utiliza sia i documenti recuperati dal vector store (letti precedentemente da un file txt) che la sua conoscenza pre-addestrata per:
        Comprendere meglio la domanda
        Integrare le informazioni del vector store con la sua conoscenza generale
        Rispondere anche se i documenti recuperati sono insufficienti
        Per forzare il modello a usare solo i dati del vector store, dovresti includere istruzioni esplicite nel system-message.st o nel rag-prompt-template.st, ad esempio:
        "Rispondi SOLO basandoti sui documenti forniti. Non utilizzare conoscenze esterne. Se la risposta non è nei documenti, rispondi "Non ho informazioni sufficienti".
		 */
		return chatClient.prompt(query).advisors(new QuestionAnswerAdvisor(vectorStore)).call().content();
	}

	/**
	 * Performs a RAG (Retrieval-Augmented Generation) chat call by enriching the user's query with data from the vector store (taken by googleapis search).
	 * @param query
	 * @return
	 */
	public RagFromGoogleResponse callFromGoogle(String query) throws GeneralSecurityException, IOException {
		/*
		"in questo caso la AI fa una ricerca su googleapis per popolare il vector store con i dati più aggiornati, poi integra la sua conoscenza con i dati appena inseriti nel vector store.
		Per forzare il modello a usare solo i dati del vector store, dovresti includere istruzioni esplicite nel system-message.st o nel rag-prompt-template.st, ad esempio:
		"Rispondi SOLO basandoti sul contesto fornito. Non utilizzare conoscenze esterne. Se la risposta non è nel contesto, rispondi "Non ho informazioni sufficienti".
		 */
		List<Document> searchResults = googleSearchService.searchAndRetrieve(query);
		log.info("storing " + searchResults.size() + " docs into Vector Store...");
		vectorStore.add(searchResults);

		log.info("retrieve revelant docs " + query);
		List<Document> relevantDocuments = vectorStore.similaritySearch(query);

		log.info("format documents for LLM prompt");
		String context = relevantDocuments.stream().map(doc -> doc.getText()).collect(Collectors.joining("<br>"));

		log.info("create LLM prompt with context taken from google search results");
		PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
		Prompt prompt = promptTemplate.create(Map.of("query", query, "context", context));
		log.info("prompt "+prompt.getContents());

		String resp = chatClient.prompt(prompt).call().chatResponse().getResult().getOutput().getText();
		return new RagFromGoogleResponse(context, resp);
	}


}
