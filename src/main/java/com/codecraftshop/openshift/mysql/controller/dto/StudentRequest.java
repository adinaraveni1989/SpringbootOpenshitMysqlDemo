package com.codecraftshop.openshift.mysql.controller.dto;

import com.codecraftshop.openshift.mysql.model.Student;

public class StudentRequest {

	public String name;
	public String address;
	public int age;
	public CollegeRequest college;

	public Student toStudent() {
		Student student = new Student();
		student.setName(this.name);
		student.setAddress(this.address);
		student.setAge(this.age);
		if (college != null) {
			student.setCollege(college.toCollege());
		}
		return student;
	}
}
