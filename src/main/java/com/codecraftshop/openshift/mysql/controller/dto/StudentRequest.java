package com.codecraftshop.openshift.mysql.controller.dto;

import com.codecraftshop.openshift.mysql.model.Student;

public class StudentRequest {

	private String name;
	private String address;
	private int age;
	private CollegeRequest college;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public CollegeRequest getCollege() {
		return college;
	}

	public void setCollege(CollegeRequest college) {
		this.college = college;
	}

	public Student toStudent() {
		Student student = new Student();
		student.setName(name);
		student.setAddress(address);
		student.setAge(age);
		if (college != null) {
			student.setCollege(college.toCollege());
		}
		return student;
	}
}
