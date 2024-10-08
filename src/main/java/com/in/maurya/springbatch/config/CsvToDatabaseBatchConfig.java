package com.in.maurya.springbatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.in.maurya.springbatch.entity.Student;
import com.in.maurya.springbatch.processor.CsvToDbStudentProcessor;
import com.in.maurya.springbatch.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CsvToDatabaseBatchConfig {
	
	private final JobRepository jobRepo;
	private final PlatformTransactionManager platformTransactionManager;
	private final StudentRepository studentRepo;
	private final StepBuilderFactory stepBuilderFactory;
	private final JobBuilderFactory jobBuilderFactory;
	
	@Value("${csv.import.task.chunk.size}")
	private int csvImportTaskChunkSize;
	@Value("${csv.import.task.executor.thread.size}")
	private int csvImportTaskThreadSize;
	
	@Bean
	public FlatFileItemReader<Student> csvItemReader(){
		FlatFileItemReader<Student> itemReader = new FlatFileItemReader<>();
		itemReader.setResource(new FileSystemResource("src/main/resources/InputFiles/students.csv"));
		itemReader.setName("csvReader");
		itemReader.setLinesToSkip(1);
		itemReader.setLineMapper(lineMapper());
		return itemReader;
	}

	private LineMapper<Student> lineMapper() {
		DefaultLineMapper<Student> lineMapper = new DefaultLineMapper<>();
		
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id","firstname","lastname","age");
		
		BeanWrapperFieldSetMapper<Student> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Student.class);
		
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		
		return lineMapper;
	}
	
	@Bean
	public CsvToDbStudentProcessor csvToDbProcessor() {
		return new CsvToDbStudentProcessor();
	}
	
	@Bean
	public RepositoryItemWriter<Student> dbItemWriter(){
		RepositoryItemWriter<Student> itemWriter = new RepositoryItemWriter<>();
		itemWriter.setRepository(studentRepo);
		itemWriter.setMethodName("save");
		return itemWriter;
	}
	
	@Bean 
	public Step csvToDBStep() {
		return stepBuilderFactory.get("csvToDBImportStep")
				.<Student,Student>chunk(csvImportTaskChunkSize)
				.reader(csvItemReader())
				.processor(csvToDbProcessor())
				.writer(dbItemWriter())
				.transactionManager(platformTransactionManager)
				.repository(jobRepo)
				.taskExecutor(taskExecutor())
				.build();
	}
	
	@Bean
	public Job runCsvToDBJob() {
		return jobBuilderFactory.get("csvToDBStudentsJob")
				.repository(jobRepo)
				.start(csvToDBStep())
				.build();
	}
	
	@Bean 
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
		asyncTaskExecutor.setConcurrencyLimit(csvImportTaskThreadSize);
		return asyncTaskExecutor;
	}

}
