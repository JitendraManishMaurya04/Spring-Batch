package com.in.maurya.springbatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.in.maurya.springbatch.entity.Student;
import com.in.maurya.springbatch.processor.CsvToCsvStudentProcessor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CsvToCsvBatchConfig {
	
	private final StepBuilderFactory stepBuilderFactory;
	private final JobBuilderFactory jobBuilderFactory;

	@Value("${csv.import.task.chunk.size}")
	private int csvImportTaskChunkSize;
	
	@Bean
	public CsvToCsvStudentProcessor csvToCsvProcessor() {
		return new CsvToCsvStudentProcessor();
	}
	
	@Bean 
	public Step csvToCsvStep(FlatFileItemReader<Student> csvItemReader,FlatFileItemWriter<Student> csvItemWriter) {
		return stepBuilderFactory.get("csvToCsvImportStep")
				.<Student,Student>chunk(csvImportTaskChunkSize)
				.reader(csvItemReader)
				.processor(csvToCsvProcessor())
				.writer(csvItemWriter)
				.build();
	}
	
	@Bean
	public Job runCsvToCsvJob(@Autowired FlatFileItemReader<Student> csvItemReader,@Autowired FlatFileItemWriter<Student> csvItemWriter) {
		return jobBuilderFactory.get("csvToCsvStudentsJob")
				.flow(csvToCsvStep(csvItemReader,csvItemWriter))
				.end()
				.build();
	}


}
