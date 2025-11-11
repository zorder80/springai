package com.flavio.springai.services;

import com.flavio.springai.dtos.CityInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;


@Service
@Slf4j
public class PrompTemplateChatService {

	private ChatClient chatClient;

	@Value("classpath:/prompt-templates/city-travel-prompt-template.st")
	private Resource cityTravelPromptTemplate;

	@Value("classpath:/prompt-templates/city-guide-prompt-template.st")
	private Resource cityGuidePromptTemplate;

	public PrompTemplateChatService(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	/*
	 * Generates travel guidance using prompt templates.
	 *
	 */
	public String call(String city, String month, String language, String budget) {
		// Load prompt templates from resources, system template is used for setting the behavior of the AI
		/*Viene usato per definire istruzioni generali che il modello AI deve seguire durante la generazione
		della risposta, come il ruolo dell’assistente o regole di comportamento.
		 */
		PromptTemplate sysTemplate = new SystemPromptTemplate(cityGuidePromptTemplate);
		PromptTemplate promptTemplate = new PromptTemplate(cityTravelPromptTemplate);
		Prompt prompt = promptTemplate
				.create(Map.of("city", city, "month", month, "language", language, "budget", budget));

		return chatClient.prompt(prompt).system(sysTemplate.getTemplate()).call().chatResponse().getResult().getOutput().getText();
	}

	/*
	 * Generates travel guidance using prompt templates.
	 *
	 */
	public String callWithJsonResponse(String city, String month, String language, String budget) {
		// Load prompt templates from resources, system template is used for setting the behavior of the AI
		/*Viene usato per definire istruzioni generali che il modello AI deve seguire durante la generazione
		della risposta, come il ruolo dell’assistente o regole di comportamento.
		 */
		PromptTemplate sysTemplate = new SystemPromptTemplate(cityGuidePromptTemplate);
		PromptTemplate promptTemplate = new PromptTemplate(cityTravelPromptTemplate);

		//bind response to CityInfo class with BeanOutputConverter, creates a json from the class
		BeanOutputConverter<CityInfo> converter = new BeanOutputConverter<>(CityInfo.class);
		String format = converter.getFormat();

		Prompt prompt = promptTemplate
				.create(Map.of("city", city, "month", month, "language", language, "budget", budget, "format", format));

		String jsonResp = chatClient.prompt(prompt).system(sysTemplate.getTemplate()).call().chatResponse().getResult().getOutput().getText();
		log.info("json response "+jsonResp);

		return Objects.requireNonNull(jsonResp);
	}

}
