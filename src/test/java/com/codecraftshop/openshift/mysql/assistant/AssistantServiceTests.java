package com.codecraftshop.openshift.mysql.assistant;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

class AssistantServiceTests {

	private static final String API_URL = "https://api.openai.com/v1/chat/completions";

	@Test
	void sendsQuestionToOpenAiAndReturnsReply() {
		RestTemplate restTemplate = new RestTemplate();
		MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
		server.expect(requestTo(API_URL))
				.andExpect(method(HttpMethod.POST))
				.andExpect(header("Authorization", "Bearer test-key"))
				.andExpect(jsonPath("$.model", is("gpt-4o-mini")))
				.andExpect(jsonPath("$.messages[1].content", is("How do I find a student?")))
				.andRespond(withSuccess("{\"choices\":[{\"message\":{\"content\":\"Use the student ID form.\"}}]}",
						MediaType.APPLICATION_JSON));
		AssistantService service = new AssistantService(restTemplate, "test-key", "gpt-4o-mini", API_URL);

		assertEquals("Use the student ID form.", service.answer("How do I find a student?"));
		server.verify();
	}

	@Test
	void reportsMissingApiKeyAsUnavailable() {
		AssistantService service = new AssistantService(new RestTemplate(), "", "gpt-4o-mini", API_URL);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> service.answer("How do I find a student?"));

		assertEquals(503, exception.getStatus().value());
	}

	@Test
	void reportsProviderErrorsAsBadGateway() {
		RestTemplate restTemplate = new RestTemplate();
		MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
		server.expect(requestTo(API_URL))
				.andRespond(withServerError());
		AssistantService service = new AssistantService(restTemplate, "test-key", "gpt-4o-mini", API_URL);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
				() -> service.answer("How do I find a student?"));

		assertEquals(502, exception.getStatus().value());
		server.verify();
	}
}
