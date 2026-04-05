package com.lotusluna.review.model;

import lombok.Data;

@Data
public class AnthropicMessage {
    private String role;
    private String content;
}