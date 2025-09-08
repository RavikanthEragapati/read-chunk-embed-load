package com.eragapati.configuration;

import com.eragapati.vectorstore.BatchingSimpleVectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InMemoryVectorStoreConfig {

    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean
    public BatchingSimpleVectorStore batchingSimpleVectorStore(EmbeddingModel embeddingModel){
        return new BatchingSimpleVectorStore(SimpleVectorStore.builder(embeddingModel));
    }
}
