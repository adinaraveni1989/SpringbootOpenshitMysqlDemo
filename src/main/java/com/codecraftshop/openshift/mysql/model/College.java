package com.codecraftshop.openshift.mysql.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "college")
public class College extends NamedAddressEntity {

	@JsonIgnore
	@OneToMany(mappedBy = "college")
	private List<Student> students = new ArrayList<>();

	public List<Student> getStudents() {
		return students;
	}

	public College() {
	}

	public College(Long id, String name, String address) {
		super(id, name, address);
	}
}
