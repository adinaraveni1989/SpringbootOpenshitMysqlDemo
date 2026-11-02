package com.codecraftshop.openshift.mysql.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.codecraftshop.openshift.mysql.controller.dto.StudentResponse;
import com.codecraftshop.openshift.mysql.model.College;
import com.codecraftshop.openshift.mysql.model.Student;
import com.codecraftshop.openshift.mysql.repository.CollegeRepository;
import com.codecraftshop.openshift.mysql.repository.StudentRepository;

@RestController
@RequestMapping("/students")
public class StudentController {

	private static final Logger LOGGER = LoggerFactory.getLogger(StudentController.class);

	private final StudentRepository studentRepository;
	private final CollegeRepository collegeRepository;

	public StudentController(StudentRepository studentRepository, CollegeRepository collegeRepository) {
		this.studentRepository = studentRepository;
		this.collegeRepository = collegeRepository;
	}

	@GetMapping
	public List<Student> getStudents() {
		LOGGER.info("Fetching all students");
		List<Student> students = (List<Student>) studentRepository.findAll();
		LOGGER.debug("Fetched {} students", students.size());
		return students;
	}

	@PostMapping
	@Transactional
	public ResponseEntity<Student> addStudent(@RequestBody Student student) {
		LOGGER.info("Creating student");
		if (student.getCollege() == null) {
			LOGGER.debug("Student was not created because college details are missing");
			return ResponseEntity.badRequest().build();
		}
		Optional<College> college = findOrCreateCollege(student.getCollege());
		if (!college.isPresent()) {
			LOGGER.debug("Student was not created because college id {} was not found",
					student.getCollege().getId());
			return ResponseEntity.notFound().build();
		}
		student.setCollege(college.get());
		Student savedStudent = studentRepository.save(student);
		LOGGER.debug("Created student with id {}", savedStudent.getId());
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedStudent.getId())
				.toUri();
		return ResponseEntity.created(location).body(savedStudent);
	}

	@GetMapping("/{id}")
	public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
		LOGGER.info("Fetching student with id {}", id);
		Optional<Student> student = studentRepository.findById(id);
		LOGGER.debug("Student with id {} {}", id, student.isPresent() ? "was found" : "was not found");
		return student.map(found -> ResponseEntity.ok(new StudentResponse(found)))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id, @RequestBody Student student) {
		LOGGER.info("Updating student with id {}", id);
		if (!studentRepository.existsById(id)) {
			LOGGER.debug("Student with id {} was not found for update", id);
			return ResponseEntity.notFound().build();
		}
		if (student.getAge() <= 18) {
			LOGGER.debug("Student with id {} was not updated because age {} is not greater than 18",
					id, student.getAge());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		if (student.getCollege() == null) {
			LOGGER.debug("Student with id {} was not updated because college details are missing", id);
			return ResponseEntity.badRequest().build();
		}
		Optional<College> college = findOrCreateCollege(student.getCollege());
		if (!college.isPresent()) {
			LOGGER.debug("Student with id {} was not updated because college id {} was not found",
					id, student.getCollege().getId());
			return ResponseEntity.notFound().build();
		}

		student.setId(id);
		student.setCollege(college.get());
		Student updatedStudent = studentRepository.save(student);
		LOGGER.debug("Updated student with id {}", id);
		return ResponseEntity.ok(new StudentResponse(updatedStudent));
	}

	private Optional<College> findOrCreateCollege(College college) {
		if (college.getId() == null) {
			return Optional.of(collegeRepository.save(college));
		}
		return collegeRepository.findById(college.getId());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
		LOGGER.info("Deleting student with id {}", id);
		if (!studentRepository.existsById(id)) {
			LOGGER.debug("Student with id {} was not found for deletion", id);
			return ResponseEntity.notFound().build();
		}
		studentRepository.deleteById(id);
		LOGGER.debug("Deleted student with id {}", id);
		return ResponseEntity.noContent().build();
	}
}
