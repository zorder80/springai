package com.flavio.springai.dtos;

import java.util.List;

public record RagFromGoogleResponse(String googleSearchResponse, String llmResponse) {}