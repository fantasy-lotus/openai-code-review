package com.lotusluna.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Prompt {
    @NonNull
    private String role;
    @NonNull
    private String content;
}
