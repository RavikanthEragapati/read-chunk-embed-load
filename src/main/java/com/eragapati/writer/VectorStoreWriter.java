package com.eragapati.writer;

import com.eragapati.vectorstore.BatchingSimpleVectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class VectorStoreWriter implements ItemWriter<Document> {

    private final BatchingSimpleVectorStore vectorStore;
    private final TextSplitter textSplitter;
    @Override
    public void write(Chunk<? extends Document> chunk) throws Exception {
        List<Document> docList = new ArrayList<>();
        for (Document doc : chunk) {
            docList.add(doc);
            log.info("Writing: {}", doc.getId());
        }
        vectorStore.add(textSplitter.apply(docList));

    }
}
