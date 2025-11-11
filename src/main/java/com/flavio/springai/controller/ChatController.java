package com.flavio.springai.controller;

import com.flavio.springai.services.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.GeneralSecurityException;


@Controller
public class ChatController {

    private final SimpleChatService simpleChatService;
    private final RAGChatService ragChatService;
    private final PrompTemplateChatService promptTemplateChatService;
    private final SentimentChatService sentimentChatService;
    private final OpenAiModeration openAiModeration;
    private final ToolService toolService;
    private final BotChatService botChatService;

    @Autowired
    public ChatController(SimpleChatService simpleChatService,
                          RAGChatService ragChatService,
                          PrompTemplateChatService promptTemplateChatService,
                          OpenAiModeration openAiModeration,
                          SentimentChatService sentimentChatService,
                          ToolService toolService,
                          BotChatService botChatService) {
        this.simpleChatService = simpleChatService;
        this.ragChatService = ragChatService;
        this.promptTemplateChatService = promptTemplateChatService;
        this.openAiModeration = openAiModeration;
        this.sentimentChatService = sentimentChatService;
        this.toolService = toolService;
        this.botChatService = botChatService;
    }

    @Value("${spring.ai.openai.chat.options.model}")
    private String usedModel;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("usedModel", usedModel);
        return "index";
    }

    @GetMapping("/showSimpleChat")
    public String showSimpleChat() {
         return "simpleChat";
    }

    @PostMapping("/simpleChat")
    public String simpleChat(@RequestParam String query, Model model) {
    	model.addAttribute("question", query);
    	model.addAttribute("response", simpleChatService.generateAnswer(query));
        return "simpleChat";
    }

    @GetMapping("/showRAGRequest")
    public String showRAGRequest() {
        return "ragRequest";
    }

    @GetMapping("/showRAGGoogleRequest")
    public String showRAGGoogleRequest() {
        return "ragGoogleRequest";
    }

    @PostMapping("/ragRequest")
    public String ragRequest(@RequestParam String query, Model model) {
        model.addAttribute("response", ragChatService.call(query));
        return "ragRequest";
    }

    @PostMapping("/ragGoogleRequest")
    public String ragGoogleRequest(@RequestParam String query, Model model) throws GeneralSecurityException, IOException {
        model.addAttribute("response", ragChatService.callFromGoogle(query));
        return "ragGoogleRequest";
    }

    @GetMapping("/showPromptTemplateChat")
    public String showChatPage() {
        return "promptTemplateChat";
    }

    @PostMapping("/promptTemplateChat")
    public String getChatResponse(@RequestParam String city, @RequestParam String month,
                                  @RequestParam String language, @RequestParam String budget, @RequestParam Boolean jsonYes, Model model) {
        String response;
        if(jsonYes){
            response = promptTemplateChatService.callWithJsonResponse(city,month,language,budget);
        }else {
            response = promptTemplateChatService.call(city, month, language, budget);
        }
        model.addAttribute("city",city);
        model.addAttribute("response",response);
        return "promptTemplateChat";
    }

    @GetMapping("/showSentiment")
    public String showSentiment() {
        return "sentimentAnalysis";
    }

    @PostMapping("/sentimentRequest")
    public String sentimentRequest(@RequestParam String query, Model model) {
        model.addAttribute("query", query);
        model.addAttribute("response", sentimentChatService.analyse(query));
        return "sentimentAnalysis";
    }

    @GetMapping("/showModeration")
    public String showModeration() {
        return "moderation";
    }

    @PostMapping("/moderationRequest")
    public String moderationRequest(@RequestParam String query, Model model) {
        model.addAttribute("response", openAiModeration.moderate(query));
        model.addAttribute("query", query);
        return "moderation";
    }

    @GetMapping("/showTools")
    public String showTools() {
        return "tools";
    }

    @PostMapping("/tools")
    public String tools(@RequestParam String query, Model model) {
        model.addAttribute("question", query);
        model.addAttribute("response", toolService.callAgent(query));
        return "tools";
    }

    @GetMapping("/showBotChat")
    public String showBotChat() {
        return "botChat";
    }

    @PostMapping("/botChat")
    public String botChat(@RequestParam String query, Model model) {
        model.addAttribute("question", query);
        model.addAttribute("response", botChatService.chat(query));
        model.addAttribute("history", botChatService.getBotChatHistory());
        return "botChat";
    }

    @GetMapping("/resetBotChat")
    public String resetBotChat(Model model) {
        botChatService.resetBotChatHistory();
        model.addAttribute("history", null);
        return "botChat";
    }

}