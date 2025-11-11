package com.flavio.springai.services;

import org.springframework.ai.moderation.Moderation;
import org.springframework.ai.moderation.ModerationPrompt;
import org.springframework.ai.moderation.ModerationResponse;
import org.springframework.ai.moderation.ModerationResult;
import org.springframework.ai.openai.OpenAiModerationModel;
import org.springframework.ai.openai.OpenAiModerationOptions;
import org.springframework.stereotype.Service;


@Service
public class OpenAiModeration {

	private OpenAiModerationModel openAiModerationModel;

	public OpenAiModeration(OpenAiModerationModel openAiModerationModel) {
		this.openAiModerationModel = openAiModerationModel;
	}
	
	public ModerationResult moderate(String text) {
		OpenAiModerationOptions moderationOptions = OpenAiModerationOptions.builder()
				.model("omni-moderation-latest")
				.build();
		ModerationPrompt moderationPrompt = new ModerationPrompt(text, moderationOptions);
		ModerationResponse response = openAiModerationModel.call(moderationPrompt);
		Moderation moderation = response.getResult().getOutput();
		return moderation.getResults().get(0);
	}


}
