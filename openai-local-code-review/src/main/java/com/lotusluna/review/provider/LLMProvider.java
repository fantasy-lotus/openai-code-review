package com.lotusluna.review.provider;

import java.util.List;
import com.lotusluna.review.model.Prompt;

public interface LLMProvider {
    String call(List<Prompt> messages, String systemPrompt) throws Exception;
}