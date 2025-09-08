package com.eragapati.reader;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;

import java.util.Iterator;
import java.util.List;

public class TikaItemReader implements ResourceAwareItemReaderItemStream<Document> {

    private Resource resource;
    private Iterator<Document> docIterator;

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public Document read() {
        if (resource == null) {
            return null;
        }
        if (docIterator == null) {
            TikaDocumentReader tikaReader = new TikaDocumentReader(resource);
            List<Document> docs = tikaReader.get(); // extract all docs from this file
            docIterator = docs.iterator();
        }
        return docIterator.hasNext() ? docIterator.next() : null;
    }

    @Override
    public void open(org.springframework.batch.item.ExecutionContext executionContext) throws ItemStreamException {
        // no-op
    }

    @Override
    public void update(org.springframework.batch.item.ExecutionContext executionContext) throws ItemStreamException {
        // no-op
    }

    @Override
    public void close() throws ItemStreamException {
        docIterator = null;
        resource = null;
    }
}
