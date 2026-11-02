package com.codecraftshop.openshift.mysql.controller.dto;

import com.codecraftshop.openshift.mysql.model.Student;

public class StudentResponse {

	private final Long id;
	private final String name;
	private final String address;
	private final int age;
	private final CollegeResponse college;

	public StudentResponse(Student student) {
		this.id = student.getId();
		this.name = student.getName();
		this.address = student.getAddress();
		this.age = student.getAge();
		this.college = student.getCollege() == null ? null : new CollegeResponse(student.getCollege());
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public int getAge() {
		return age;
	}

	public CollegeResponse getCollege() {
		return college;
	}
}
