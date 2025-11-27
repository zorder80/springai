package com.flavio.springai.dtos;

import java.util.List;

public record BotChatAnswer(List<String> messages, String conversationId) {
}
