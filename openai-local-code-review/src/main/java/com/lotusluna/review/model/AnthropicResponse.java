package com.lotusluna.review.model;

import lombok.Data;
import java.util.List;

@Data
public class AnthropicResponse {
    private List<ContentBlock> content;
    private String stop_reason;

    @Data
    public static class ContentBlock {
        private String type;
        private String text;
    }
}