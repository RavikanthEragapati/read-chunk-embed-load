package com.eragapati.listener;

import com.eragapati.vectorstore.BatchingSimpleVectorStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobCompletionNotificationListener implements JobExecutionListener {

    private final BatchingSimpleVectorStore simpleVectorStore;

    @Value("${vector-store.simple.offload.path:/src/main/resources/vectorstore_backup.json}")
    private String backupPath;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            simpleVectorStore.save(new File(backupPath));
            log.info("!!! JOB FINISHED !!!");
        }
    }
}
