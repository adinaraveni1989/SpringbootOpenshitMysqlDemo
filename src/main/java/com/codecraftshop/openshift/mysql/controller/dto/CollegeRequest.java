package com.codecraftshop.openshift.mysql.controller.dto;

import com.codecraftshop.openshift.mysql.model.College;

public class CollegeRequest {

	private Long id;
	private String name;
	private String address;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public College toCollege() {
		return new College(id, name, address);
	}
}
