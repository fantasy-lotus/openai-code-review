package com.lotusluna.review.model;

import lombok.Data;
import java.util.List;

@Data
public class AnthropicRequest {
    private String model;
    private int max_tokens;
    private List<AnthropicMessage> messages;
    private String system;
}