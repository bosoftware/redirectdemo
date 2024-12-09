package com.example.demo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin("*")
@RestController
public class TestController {

	String url = "https://www.ozbargain.com.au/";

	@GetMapping("/proxy-html")
	public ResponseEntity<String> fetchHtml(@RequestHeader Map<String, String> rheaders) {
		System.out.println("proxy-html");
		printHeader(rheaders);

		RestTemplate restTemplate = new RestTemplate();

		// Add custom headers
		HttpHeaders headers = new HttpHeaders();
		headers.set("Custom-Header", "YourHeaderValue");

		HttpEntity<String> entity = new HttpEntity<>(headers);

		// Fetch the HTML content
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

		// Serve the HTML content back to the client
		return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(response.getBody());
	}

	@GetMapping("/proxy")
	public ResponseEntity<String> proxy(@RequestHeader Map<String, String> rheaders) {
		System.out.println("proxy");
		printHeader(rheaders);
		RestTemplate restTemplate = new RestTemplate();

		// Set custom headers
		HttpHeaders headers = new HttpHeaders();
		headers.set("Custom-Header", "YourHeaderValue");

		HttpEntity<String> entity = new HttpEntity<>(headers);

		// Forward the request
		return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
	}

	@RequestMapping(value = "/redirect", method = RequestMethod.GET)
	public void method(HttpServletResponse httpServletResponse, @RequestHeader Map<String, String> rheaders) {
		System.out.println("redirect");
		printHeader(rheaders);
		httpServletResponse.setHeader("Location", url);
		httpServletResponse.setStatus(302);
	}

	@RequestMapping("/to-be-redirected")
	public ResponseEntity<Object> redirectToExternalUrl(@RequestHeader Map<String, String> rheaders)
			throws URISyntaxException {
		System.out.println("to-be-redirected");
		printHeader(rheaders);
		URI yahoo = new URI(url);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("test", "abc");
		httpHeaders.setLocation(yahoo);
		return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
	}

	private void printHeader(Map<String, String> headers) {
		headers.forEach((key, value) -> {
			System.out.println(String.format("Header '%s' = %s", key, value));
		});
	}
}
