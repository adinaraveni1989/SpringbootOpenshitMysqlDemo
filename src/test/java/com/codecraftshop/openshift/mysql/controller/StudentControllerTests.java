package com.codecraftshop.openshift.mysql.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
				Arrays.asList(new Student(1L, "Ada", "London", 25)));

		mockMvc.perform(get("/students"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("Ada")))
				.andExpect(jsonPath("$[0].address", is("London")))
				.andExpect(jsonPath("$[0].age", is(25)));
	}

	@Test
	void createsStudent() throws Exception {
		when(studentRepository.save(any(Student.class)))
				.thenReturn(new Student(7L, "Ada", "London", 25));

		mockMvc.perform(post("/students")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Ada\",\"address\":\"London\",\"age\":25}"))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", "http://localhost/students/7"))
				.andExpect(jsonPath("$.id", is(7)))
				.andExpect(jsonPath("$.name", is("Ada")))
				.andExpect(jsonPath("$.address", is("London")))
				.andExpect(jsonPath("$.age", is(25)));
	}

	@Test
	void getsStudentById() throws Exception {
		when(studentRepository.findById(7L))
				.thenReturn(Optional.of(new Student(7L, "Ada", "London", 25)));

		mockMvc.perform(get("/students/7"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(7)))
				.andExpect(jsonPath("$.name", is("Ada")))
				.andExpect(jsonPath("$.address", is("London")))
				.andExpect(jsonPath("$.age", is(25)));
	}

	@Test
	void returnsNotFoundWhenStudentDoesNotExist() throws Exception {
		when(studentRepository.findById(7L)).thenReturn(Optional.empty());

		mockMvc.perform(get("/students/7"))
				.andExpect(status().isNotFound());
	}

	@Test
	void updatesStudentWhenOlderThanEighteen() throws Exception {
		when(studentRepository.existsById(7L)).thenReturn(true);
		when(studentRepository.save(any(Student.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		mockMvc.perform(put("/students/7")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Ada Lovelace\",\"address\":\"London\",\"age\":19}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(7)))
				.andExpect(jsonPath("$.name", is("Ada Lovelace")))
				.andExpect(jsonPath("$.address", is("London")))
				.andExpect(jsonPath("$.age", is(19)));
	}

	@Test
	void rejectsUpdateWhenStudentIsEighteen() throws Exception {
		when(studentRepository.existsById(7L)).thenReturn(true);

		mockMvc.perform(put("/students/7")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Ada\",\"address\":\"London\",\"age\":18}"))
				.andExpect(status().isForbidden());

		verify(studentRepository, never()).save(any(Student.class));
	}

	@Test
	void rejectsUpdateWhenStudentIsYoungerThanEighteen() throws Exception {
		when(studentRepository.existsById(7L)).thenReturn(true);

		mockMvc.perform(put("/students/7")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Ada\",\"address\":\"London\",\"age\":17}"))
				.andExpect(status().isForbidden());

		verify(studentRepository, never()).save(any(Student.class));
	}

	@Test
	void returnsNotFoundWhenUpdatingMissingStudent() throws Exception {
		when(studentRepository.existsById(7L)).thenReturn(false);

		mockMvc.perform(put("/students/7")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Ada\",\"address\":\"London\",\"age\":25}"))
				.andExpect(status().isNotFound());

		verify(studentRepository, never()).save(any(Student.class));
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
