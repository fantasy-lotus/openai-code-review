package com.lotusluna.model;

import lombok.Data;

import java.util.List;

@Data
public class QwenRequest {
    private String model;
    private List<Prompt> messages;
}
