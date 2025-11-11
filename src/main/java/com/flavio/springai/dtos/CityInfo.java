package com.flavio.springai.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record CityInfo(@JsonPropertyDescription("These are the attractions") String attractions,
                       @JsonPropertyDescription("This is the cuisine") String cuisine,
                       @JsonPropertyDescription("These are the phrases") String phrases,
                       @JsonPropertyDescription("These are tips") String tips,
                       @JsonPropertyDescription("This is the etiquette") String etiquette,
                       @JsonPropertyDescription("These are discos") String discos) {
}
