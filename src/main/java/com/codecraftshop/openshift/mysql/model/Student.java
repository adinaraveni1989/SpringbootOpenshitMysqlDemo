package com.codecraftshop.openshift.mysql.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="student")
public class Student extends NamedAddressEntity {

	private int age;

	@ManyToOne
	@JoinColumn(name = "college_id")
	private College college;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public College getCollege() {
		return college;
	}

	public void setCollege(College college) {
		this.college = college;
	}

	public Student() {
	}

	public Student(Long id, String name, String address) {
		this(id, name, address, 0, null);
	}

	public Student(Long id, String name, String address, int age) {
		this(id, name, address, age, null);
	}

	public Student(Long id, String name, String address, int age, College college) {
		super(id, name, address);
		this.age = age;
		this.college = college;
	}
	
	
}
