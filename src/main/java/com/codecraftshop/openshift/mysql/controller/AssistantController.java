package com.codecraftshop.openshift.mysql.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.codecraftshop.openshift.mysql.assistant.AssistantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

	private final AssistantService assistantService;

	public AssistantController(AssistantService assistantService) {
		this.assistantService = assistantService;
	}

	@PostMapping("/chat")
	public ResponseEntity<AssistantReply> chat(@Valid @RequestBody AssistantQuestion question) {
		return ResponseEntity.ok(new AssistantReply(assistantService.answer(question.getMessage())));
	}

	public static class AssistantQuestion {

		@NotBlank
		@Size(max = 1000)
		private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	public static class AssistantReply {

		private final String reply;

		public AssistantReply(String reply) {
			this.reply = reply;
		}

		public String getReply() {
			return reply;
		}
	}
}
