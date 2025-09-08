package com.eragapati.configuration;

import com.eragapati.embedding.CustomEmbeddingModelImpl;
import com.eragapati.listener.JobCompletionNotificationListener;
import com.eragapati.processor.DocumentItemProcessor;
import com.eragapati.reader.TikaItemReader;
import com.eragapati.vectorstore.BatchingSimpleVectorStore;
import com.eragapati.writer.VectorStoreWriter;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
public class BatchConfiguration {

    @Value("${embedding.base-url:http://localhost:11434}")
    private String embeddingBaseUrl;

    @Value("${embedding.uri:/api/embed}")
    private String embeddingUri;

    @Value("${embedding.model:llama3.2}")
    private String embeddingModel;

    @Value("${files.location}")
    private String resourceLocation;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CustomEmbeddingModelImpl customEmbeddingModel(RestTemplate restTemplate) {
        return new CustomEmbeddingModelImpl(
                restTemplate, embeddingBaseUrl+embeddingUri, embeddingModel
        );
    }

    @Bean
    TextSplitter textSplitter() {
        return new TokenTextSplitter();
    }

    @Bean
    public MultiResourceItemReader<org.springframework.ai.document.Document> multiResourceItemReader() throws IOException {
        MultiResourceItemReader<org.springframework.ai.document.Document> reader = new MultiResourceItemReader<>();

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(resourceLocation);

        reader.setResources(resources);
        reader.setDelegate(new TikaItemReader());
        return reader;
    }

    @Bean
    public DocumentItemProcessor processor() {
        return new DocumentItemProcessor();
    }

    @Bean
    public VectorStoreWriter vectorStoreWriter(BatchingSimpleVectorStore vectorStore, TextSplitter textSplitter) {
        return new VectorStoreWriter(vectorStore, textSplitter);
    }

    @Bean
    public Step processDocumentsStep(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                                     ItemReader<Document> reader,
                                     DocumentItemProcessor processor,
                                     VectorStoreWriter writer) {
        return new StepBuilder("processDocumentsStep", jobRepository)
                .<Document, Document>chunk(1, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job documentIngestionJob(JobRepository jobRepository, Step processDocumentsStep, JobCompletionNotificationListener listener) {
        return new JobBuilder("documentIngestionJob", jobRepository)
                .listener(listener)
                .start(processDocumentsStep)
                .build();
    }

}
