package com.flavio.springai.controller.embeddings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.flavio.springai.services.SimpleChatService;

@Controller
public class EmbeddingDemo {

	@Autowired
	private SimpleChatService service;
	
	@GetMapping("/showEmbedding")
	public String showEmbedDemo() {
		return "embedDemo";

	}

	@PostMapping("/embedding")
	public String embed(@RequestParam String text,Model model) {
		float[] resposne = service.embed(text);
		model.addAttribute("response",resposne);
		return "embedDemo";

	}
	

}