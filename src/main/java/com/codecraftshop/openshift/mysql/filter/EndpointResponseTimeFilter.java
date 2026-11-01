package com.codecraftshop.openshift.mysql.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class EndpointResponseTimeFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(EndpointResponseTimeFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		long startTime = System.nanoTime();
		try {
			filterChain.doFilter(request, response);
		} finally {
			double elapsedMilliseconds = (System.nanoTime() - startTime) / 1_000_000.0;
			LOGGER.info("{} {} completed with status {} in {} ms",
					request.getMethod(), request.getRequestURI(), response.getStatus(), elapsedMilliseconds);
		}
	}
}
