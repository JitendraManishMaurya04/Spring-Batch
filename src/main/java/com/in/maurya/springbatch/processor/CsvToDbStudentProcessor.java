package com.in.maurya.springbatch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.in.maurya.springbatch.entity.Student;

public class CsvToDbStudentProcessor implements ItemProcessor<Student, Student>{

	@Override
	public Student process(Student student) throws Exception {
		//Business logic
		student.setId(null);   //setting Id to null will allow hibernate to auto generate it own ID instead of using ID from CSV file
		return student;
	}

}
