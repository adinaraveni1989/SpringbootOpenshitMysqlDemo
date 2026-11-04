package com.codecraftshop.openshift.mysql.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codecraftshop.openshift.mysql.assistant.AssistantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AssistantController.class)
class AssistantControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AssistantService assistantService;

	@Test
	void returnsAssistantReply() throws Exception {
		when(assistantService.answer("How do I find a student?"))
				.thenReturn("Enter the student's ID on the welcome page.");

		mockMvc.perform(post("/api/assistant/chat")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"message\":\"How do I find a student?\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.reply", is("Enter the student's ID on the welcome page.")));

		verify(assistantService).answer("How do I find a student?");
	}

	@Test
	void rejectsBlankQuestion() throws Exception {
		mockMvc.perform(post("/api/assistant/chat")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"message\":\"  \"}"))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(assistantService);
	}

	@Test
	void rejectsQuestionLongerThanOneThousandCharacters() throws Exception {
		String question = new String(new char[1001]).replace('\0', 'a');

		mockMvc.perform(post("/api/assistant/chat")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"message\":\"" + question + "\"}"))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(assistantService);
	}
}
