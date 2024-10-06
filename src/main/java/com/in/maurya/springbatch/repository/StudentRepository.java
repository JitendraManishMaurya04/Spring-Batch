package com.in.maurya.springbatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.in.maurya.springbatch.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer>{

}
