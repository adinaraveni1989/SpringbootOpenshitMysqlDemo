package com.codecraftshop.openshift.mysql.controller.dto;

public class CollegeRequest {

	public Long id;
	public String name;
	public String address;

	public com.codecraftshop.openshift.mysql.model.College toCollege() {
		return new com.codecraftshop.openshift.mysql.model.College(id, name, address);
	}
}
