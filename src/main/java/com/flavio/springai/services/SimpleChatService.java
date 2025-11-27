package com.flavio.springai.services;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SimpleChatService {
	
	private ChatClient chatClient;
	
	@Autowired
	private EmbeddingModel embeddingModel;

	@Autowired
	private VectorStore vectorStore;

	public SimpleChatService(ChatClient chatClient) {
		this.chatClient = chatClient;
	}
	
	public String generateAnswer(String question) {
		return chatClient.prompt(question).call().chatResponse().getResult().getOutput().getText();
	}

	
	public ChatResponse generateAnswerWithRoles(String question) {

		return chatClient.prompt().system("You are a helpful assistant that can botMessage any userMessage.")
				.user(question)
				.call().chatResponse();
	}

	
	public float[] embed(String text) {
		return embeddingModel.embed(text);
	}
	
	public double findSimilarity(String text1, String text2) {
		List<float[]> response = embeddingModel.embed(List.of(text1, text2));
		return cosineSimilarity(response.get(0), response.get(1));
	}
	
	private double cosineSimilarity(float[] vectorA, float[] vectorB) {
		if (vectorA.length != vectorB.length) {
			throw new IllegalArgumentException("Vectors must be of the same length");
		}

		// Initialize variables for dot product and magnitudes
		double dotProduct = 0.0;
		double magnitudeA = 0.0;
		double magnitudeB = 0.0;

		// Calculate dot product and magnitudes
		for (int i = 0; i < vectorA.length; i++) {
			dotProduct += vectorA[i] * vectorB[i];
			magnitudeA += vectorA[i] * vectorA[i];
			magnitudeB += vectorB[i] * vectorB[i];
		}

		// Calculate and return cosine similarity
		return dotProduct / (Math.sqrt(magnitudeA) * Math.sqrt(magnitudeB));
	}
	
	public List<Document> searchJobs(String query) {
		SearchRequest searchRequest;
		searchRequest = SearchRequest.builder()
				.query(query)                 // La query in linguaggio naturale
				.topK(3)                       // Numero di documenti da restituire (top-k)
				.similarityThreshold(0.7) // Soglia di somiglianza (es. 0.7 = 70%)
				.build();
		// 2. Esecuzione della ricerca
		return vectorStore.similaritySearch(searchRequest);
//        return vectorStore.similaritySearch(query);
	}


	public String answer(String query) {
		//enriches the prompt with relevant documents from the vector store
		return chatClient.prompt(query).advisors(new QuestionAnswerAdvisor(vectorStore)).call().content();
	}
	
	public String getStockPrice(String company) {

//		Prompt prompt = new Prompt("Get stock symbol and stock price for " + company,
//				OllamaOptions.builder().
////						. ("stockRetrievalFunction").
//				build());
//		CallResponseSpec response = chatClient.prompt(prompt).call();
//		return response.content();
		return "Not implemented yet.";
	}
	
	

}
