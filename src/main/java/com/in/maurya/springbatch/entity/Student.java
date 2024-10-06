package com.in.maurya.springbatch.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Student {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	private String firstname;
	
	private String lastname;
	
	private int age;

}
