package com.in.maurya.springbatch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.in.maurya.springbatch.entity.Student;

public class CsvToCsvStudentProcessor implements ItemProcessor<Student, Student>{

	@Override
	public Student process(Student item) throws Exception {
		//Business logic
		final String firstname = item.getFirstname().toUpperCase();
		final String lastname = item.getLastname().toUpperCase();
		final Student student = new Student();
		student.setId(item.getId());
		student.setFirstname(firstname);
		student.setLastname(lastname);
		student.setAge(item.getAge());
		
		return student;
	}

}
