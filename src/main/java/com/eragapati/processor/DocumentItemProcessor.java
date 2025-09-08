package com.eragapati.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class DocumentItemProcessor implements ItemProcessor<Document, Document> {

    @Override
    public Document process(final Document document) {
        log.info("Processing: {}", document.getId());

        return document;
    }

}
