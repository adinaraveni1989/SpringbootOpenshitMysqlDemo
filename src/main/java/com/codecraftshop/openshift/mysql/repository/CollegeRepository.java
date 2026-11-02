package com.codecraftshop.openshift.mysql.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.codecraftshop.openshift.mysql.model.College;

@Repository
public interface CollegeRepository extends CrudRepository<College, Long> {
}
