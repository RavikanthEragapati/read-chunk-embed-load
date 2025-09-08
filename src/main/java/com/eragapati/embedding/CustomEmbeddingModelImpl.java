package com.eragapati.embedding;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.AbstractEmbeddingModel;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomEmbeddingModelImpl extends AbstractEmbeddingModel {

    private final RestTemplate restTemplate;
    private final String endpointUrl;
    private final String modelName;

    public CustomEmbeddingModelImpl(RestTemplate restTemplate, String endpointUrl, String modelName) {
        this.restTemplate = restTemplate;
        this.endpointUrl = endpointUrl;
        this.modelName = modelName;
    }

    @Override
    public float[] embed(Document document) {
        return embed(document.getText());
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        Map<String, Object> body = Map.of(
                "model", modelName,
                "input", request.getInstructions()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(endpointUrl, httpEntity, Map.class);

        List<List<Double>>  embeddingVector = (List<List<Double>>) response.getBody().get("embeddings");
        List<Embedding> embeddingList = new ArrayList<>();

        float[] embeddingVectorArr;
        for(int j=0; j< embeddingVector.size(); j++){
            embeddingVectorArr = new float[embeddingVector.get(j).size()];
            for (int i = 0; i < embeddingVector.get(j).size(); i++) {
                embeddingVectorArr[i] = embeddingVector.get(j).get(i).floatValue();
            }
            embeddingList.add(new Embedding(embeddingVectorArr,j));
        }

        return new EmbeddingResponse(embeddingList);
    }
}
