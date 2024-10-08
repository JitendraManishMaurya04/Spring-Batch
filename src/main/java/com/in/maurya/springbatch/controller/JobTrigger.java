package com.in.maurya.springbatch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobTrigger {
	
	private final JobLauncher jobLauncher;
	private final Job runCsvToDBJob;
	private final Job runDbToCsvJob;
	private final Job runCsvToCsvJob;
	
	@PostMapping("/csvToDb/students")
	public ResponseEntity<String> importCsvToDbJob() {
		return runJob(runCsvToDBJob);
	}
	
	@GetMapping("/dbToCsv/students")
	public ResponseEntity<String> extractDbToCsvJob() {
		return runJob(runDbToCsvJob);
	}
	
	@GetMapping("/csvToCsv/students")
	public ResponseEntity<String> extractCsvToCsvJob() {
		return runJob(runCsvToCsvJob);
	}


	private ResponseEntity<String> runJob(Job job) {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("jobName", job.getName())
				.addLong("startAt",System.currentTimeMillis())
				.toJobParameters();
		
		try {
			JobExecution jobExecution = jobLauncher.run(job, jobParameters);
			   return ResponseEntity.ok("Batch Job " + job.getName() 
               + " started with JobExecutionId: " + jobExecution.getId());
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                     .body("Failed to start Batch Job: " + e.getMessage());
		}
	}
	
	
	

}
