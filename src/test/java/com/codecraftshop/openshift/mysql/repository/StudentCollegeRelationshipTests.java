package com.codecraftshop.openshift.mysql.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.persistence.EntityManager;

import com.codecraftshop.openshift.mysql.model.College;
import com.codecraftshop.openshift.mysql.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class StudentCollegeRelationshipTests {

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private CollegeRepository collegeRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	void persistsCollegeWithStudentsAndLoadsOneToManyRelationship() {
		College college = collegeRepository.save(new College(null, "Example College", "Oxford"));
		studentRepository.save(new Student(null, "Ada", "London", 25, college));
		studentRepository.save(new Student(null, "Grace", "New York", 30, college));
		entityManager.flush();

		entityManager.clear();

		Student savedStudent = studentRepository.findAll().iterator().next();
		assertNotNull(savedStudent.getCollege().getId());
		assertEquals("Example College", savedStudent.getCollege().getName());

		College savedCollege = entityManager.find(College.class, savedStudent.getCollege().getId());
		assertEquals(2, savedCollege.getStudents().size());
	}
}
