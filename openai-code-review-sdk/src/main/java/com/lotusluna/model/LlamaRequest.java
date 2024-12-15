package com.lotusluna.model;

import lombok.Data;

import java.util.List;

@Data
public class LlamaRequest {
    private String model;
    private List<Prompt> messages;
}
