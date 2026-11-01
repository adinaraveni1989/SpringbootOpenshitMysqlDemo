package com.codecraftshop.openshift.mysql.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;

import com.codecraftshop.openshift.mysql.model.Student;
import com.codecraftshop.openshift.mysql.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StudentController.class)
class StudentControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StudentRepository studentRepository;

	@Test
	void listsStudents() throws Exception {
		when(studentRepository.findAll()).thenReturn(
				Arrays.asList(new Student(1L, "Ada", "London")));

		mockMvc.perform(get("/students"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("Ada")))
				.andExpect(jsonPath("$[0].address", is("London")));
	}

	@Test
	void createsStudent() throws Exception {
		when(studentRepository.save(any(Student.class)))
				.thenReturn(new Student(7L, "Ada", "London"));

		mockMvc.perform(post("/students")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Ada\",\"address\":\"London\"}"))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", "http://localhost/students/7"))
				.andExpect(jsonPath("$.id", is(7)))
				.andExpect(jsonPath("$.name", is("Ada")))
				.andExpect(jsonPath("$.address", is("London")));
	}

	@Test
	void getsStudentById() throws Exception {
		when(studentRepository.findById(7L))
				.thenReturn(Optional.of(new Student(7L, "Ada", "London")));

		mockMvc.perform(get("/students/7"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(7)))
				.andExpect(jsonPath("$.name", is("Ada")))
				.andExpect(jsonPath("$.address", is("London")));
	}

	@Test
	void returnsNotFoundWhenStudentDoesNotExist() throws Exception {
		when(studentRepository.findById(7L)).thenReturn(Optional.empty());

		mockMvc.perform(get("/students/7"))
				.andExpect(status().isNotFound());
	}

	@Test
	void deletesStudent() throws Exception {
		when(studentRepository.existsById(7L)).thenReturn(true);

		mockMvc.perform(delete("/students/7"))
				.andExpect(status().isNoContent());

		verify(studentRepository).deleteById(7L);
	}

	@Test
	void returnsNotFoundWhenDeletingMissingStudent() throws Exception {
		when(studentRepository.existsById(7L)).thenReturn(false);

		mockMvc.perform(delete("/students/7"))
				.andExpect(status().isNotFound());
	}
}
