package com.codecraftshop.openshift.mysql.controller.dto;

import com.codecraftshop.openshift.mysql.model.College;

public class CollegeResponse {

	private final Long id;
	private final String name;
	private final String address;

	public CollegeResponse(College college) {
		this.id = college.getId();
		this.name = college.getName();
		this.address = college.getAddress();
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
}
