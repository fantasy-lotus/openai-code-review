package com.lotusluna.review.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Prompt {
    private String role;
    private String content;

    public Prompt() {}

    public Prompt(@NonNull String role, @NonNull String content) {
        this.role = role;
        this.content = content;
    }
}
