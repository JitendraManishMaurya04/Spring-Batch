package com.in.maurya.springbatch.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;

import com.in.maurya.springbatch.entity.Student;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DatabaseToCsvBatchConfig {
	
	private final StepBuilderFactory stepBuilderFactory;
	private final JobBuilderFactory jobBuilderFactory;
	private final DataSource dataSource;
	
	@Value("${csv.import.task.chunk.size}")
	private int csvImportTaskChunkSize;
	
	
	@Bean
	public JdbcCursorItemReader<Student> dbItemReader(){
		JdbcCursorItemReader<Student> itemReader = new JdbcCursorItemReader<>();
		itemReader.setDataSource(dataSource);
		itemReader.setSql("Select id,firstname,lastname,age from student");
		itemReader.setRowMapper(new RowMapper<Student>() {

			@Override
			public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
				Student student = new Student();
				student.setId(rs.getInt("id"));
				student.setFirstname(rs.getString("firstname"));
				student.setLastname(rs.getString("lastname"));
				student.setAge(rs.getInt("age"));
				return student;
			}
			
		});
		return itemReader;
	}

	@Bean FlatFileItemWriter<Student> csvItemWriter(){
		FlatFileItemWriter<Student> itemWriter = new FlatFileItemWriter<>();
		itemWriter.setResource(new FileSystemResource("src/main/resources/StudentExtract.csv"));
		DelimitedLineAggregator<Student> aggregator = new DelimitedLineAggregator<>();
		BeanWrapperFieldExtractor<Student> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(new String[] {"id","firstname","lastname","age"});
		aggregator.setFieldExtractor(fieldExtractor);
		itemWriter.setLineAggregator(aggregator);
		return itemWriter;
	}
	
	@Bean 
	public Step dbToCsvStep() {
		return stepBuilderFactory.get("dbDataExtract")
				.<Student,Student>chunk(csvImportTaskChunkSize)
				.reader(dbItemReader())
				.writer(csvItemWriter())
				.build();
	}
	
	@Bean
	public Job runDbToCsvJob() {
		return jobBuilderFactory.get("extractStudents")
				.incrementer(new RunIdIncrementer())
				.start(dbToCsvStep())
				.build();
	}
	
}
