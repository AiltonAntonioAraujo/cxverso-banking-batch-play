package br.org.caixa.banking.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchJobController {

    private final JobLauncher jobLauncher;
    private final Job transactionProcessingJob;


    @PostMapping("/transactions/run")
    public ResponseEntity<Map<String, Object>> runTransactionJob(
            @RequestParam(required = false) String inputFile) {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .addString("input.file", inputFile != null ? inputFile : "")
                    .toJobParameters();

            var execution = jobLauncher.run(transactionProcessingJob, params);
            return ResponseEntity.ok(Map.of(
                    "jobName", "transactionProcessingJob",
                    "executionId", execution.getId(),
                    "status", execution.getStatus().toString()
            ));
        } catch (Exception e) {
            log.error("Failed to run transactionProcessingJob", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
