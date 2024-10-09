package com.in.maurya.springbatch.config;



import java.io.IOException;
import java.io.Writer;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.in.maurya.springbatch.entity.Student;
import com.in.maurya.springbatch.processor.CsvToCsvStudentProcessor;
import com.in.maurya.springbatch.utility.Utility;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MultipleCsvToSingleCsvBatchConfig {
	
	private final StepBuilderFactory stepBuilderFactory;
	private final JobBuilderFactory jobBuilderFactory;
	private final Utility utility;

	@Value("${csv.import.task.chunk.size}")
	private int csvImportTaskChunkSize;
	private Resource[] resources;

	
	@Bean
	public FlatFileItemReader<Student> multipleCsvItemReader(){
		FlatFileItemReader<Student> itemReader = new FlatFileItemReader<>();
		itemReader.setLineMapper(utility.lineMapper());
		itemReader.setLinesToSkip(1);
		return itemReader;
	}
	
	@Bean
	public MultiResourceItemReader<Student> multiResourceItemReader(){
	  ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
	    try{
	        resources = patternResolver.getResources("file:" + "src/main/resources/InputFiles/students_*.csv");
	    }
	    catch(IOException ex){
	       System.out.println(ex.getMessage());
	    }
		MultiResourceItemReader<Student> multiResourceItemReader = new MultiResourceItemReader<>();
		multiResourceItemReader.setResources(resources);
		multiResourceItemReader.setName("MultipleCsvReader");
		multiResourceItemReader.setDelegate(multipleCsvItemReader());
		return multiResourceItemReader;
	}
	
	@Bean FlatFileItemWriter<Student> mutipleCsvToSingleCsvWriter(){
		FlatFileItemWriter<Student> itemWriter = new FlatFileItemWriter<>();
		itemWriter.setResource(new FileSystemResource("src/main/resources/OutputFiles/CombinedStudentExtract.csv"));
		itemWriter.setAppendAllowed(true);
		DelimitedLineAggregator<Student> aggregator = new DelimitedLineAggregator<>();
		BeanWrapperFieldExtractor<Student> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(new String[] {"id","firstname","lastname","age"});
		aggregator.setFieldExtractor(fieldExtractor);
		itemWriter.setHeaderCallback(new FlatFileHeaderCallback() {
		    @Override
		    public void writeHeader(Writer writer) throws IOException {
		        writer.write("id,firstname,lastname,age");
		    }
		});
		itemWriter.setLineAggregator(aggregator);
		return itemWriter;
	}
	
	@Bean 
	public Step multipleCsvToSingleCsvStep(CsvToCsvStudentProcessor csvToCsvProcessor) {
		return stepBuilderFactory.get("MultiplCsvToSingleCsvImportStep")
				.<Student,Student>chunk(csvImportTaskChunkSize)
				.reader(multiResourceItemReader())
				.processor(csvToCsvProcessor)
				.writer(mutipleCsvToSingleCsvWriter())
				.build();
	}
	
	@Bean
	public Job runMultipleCsvToSingleCsvJob(@Autowired CsvToCsvStudentProcessor csvToCsvProcessor) {
		return jobBuilderFactory.get("MultiplCsvToSingleCsvStudentsJob")
				.incrementer(new RunIdIncrementer())
				.flow(multipleCsvToSingleCsvStep(csvToCsvProcessor))
				.end()
				.build();
	}


}
