package com.flavio.springai.dtos;

import java.util.List;

public record BotChatHistory(List<BotChatMessage> messages) {
}
