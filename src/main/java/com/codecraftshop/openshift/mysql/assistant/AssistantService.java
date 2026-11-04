package com.codecraftshop.openshift.mysql.assistant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AssistantService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AssistantService.class);
	private static final String SYSTEM_PROMPT = "You are the help assistant for the Gates College student portal. "
			+ "Answer questions about using this application and its student-management features only. "
			+ "You cannot access the database or student records. Do not claim to look up, verify, or change records. "
			+ "If asked about a specific student's private information, explain that you cannot access it and "
			+ "direct the user to the portal's student lookup feature. Do not guess application behavior.";

	private final RestTemplate restTemplate;
	private final String apiKey;
	private final String model;
	private final String apiUrl;

	public AssistantService(RestTemplate restTemplate,
			@Value("${openai.api-key:}") String apiKey,
			@Value("${openai.model:gpt-4o-mini}") String model,
			@Value("${openai.api-url:https://api.openai.com/v1/chat/completions}") String apiUrl) {
		this.restTemplate = restTemplate;
		this.apiKey = apiKey;
		this.model = model;
		this.apiUrl = apiUrl;
	}

	public String answer(String question) {
		if (!StringUtils.hasText(apiKey)) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"Assistant is not configured. Set the OPENAI_API_KEY environment variable.");
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(apiKey);
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> payload = new HashMap<>();
		payload.put("model", model);
		payload.put("messages", Arrays.asList(
				message("system", SYSTEM_PROMPT),
				message("user", question)));
		payload.put("temperature", 0.2);
		payload.put("max_tokens", 400);

		try {
			JsonNode response = restTemplate.postForObject(apiUrl, new HttpEntity<>(payload, headers), JsonNode.class);
			String answer = response == null ? null
					: response.path("choices").path(0).path("message").path("content").asText(null);
			if (!StringUtils.hasText(answer)) {
				throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
						"Assistant provider returned an empty response.");
			}
			return answer.trim();
		} catch (HttpStatusCodeException exception) {
			LOGGER.warn("OpenAI API returned HTTP {}", exception.getStatusCode().value());
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
					"Assistant provider returned an error.");
		} catch (ResourceAccessException exception) {
			LOGGER.warn("Could not connect to the OpenAI API");
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
					"Assistant provider is temporarily unavailable.");
		} catch (RestClientException exception) {
			LOGGER.warn("OpenAI API returned an invalid response");
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
					"Assistant provider returned an invalid response.");
		}
	}

	private Map<String, String> message(String role, String content) {
		Map<String, String> message = new HashMap<>();
		message.put("role", role);
		message.put("content", content);
		return message;
	}
}
