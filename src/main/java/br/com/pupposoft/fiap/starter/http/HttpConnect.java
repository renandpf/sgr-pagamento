package br.com.pupposoft.fiap.starter.http;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import br.com.pupposoft.fiap.starter.http.dto.HttpConnectDto;
import br.com.pupposoft.fiap.starter.http.exception.HttpConnectorException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class HttpConnect implements HttpConnectGateway {

	private static final String AUTHORIZATION_KEY = "Authorization";
	private static final String CONTENT_TYPE_VALUE = "application/json";
	private static final String CONTENT_TYPE_KEY = "Content-Type";

	public String postWhithRequestBody(HttpConnectDto dto) {
		try {
			log.trace("Start dto={}", dto);//NOSONAR
			
			String url = getQueryParam(dto); 
			
			String token = getToken(dto);
			
			final WebClient webClient = WebClient.create();
			
			String response = 
					webClient.post()
					.uri(url)
					.body(Mono.just(dto.getRequestBody()), dto.getRequestBody().getClass())
					.header(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE)
					.header(AUTHORIZATION_KEY, token)
					.retrieve()
					.bodyToMono(String.class)
					.block();
			
			log.trace("End response={}", response);//NOSONAR
			
			return response;
		} catch (Exception e) {
			throw processException(e);
		}

	}

	public String get(HttpConnectDto dto) {
		try {
			log.trace("Start dto={}", dto);
			
			String url = getQueryParam(dto);
			
			final WebClient webClient = WebClient.create();
			
			String token = dto.getHeaders() == null ? "" : dto.getHeaders().get(AUTHORIZATION_KEY);
			
			ResponseSpec responseSpec = 
					webClient.get()
					.uri(url)
					.header(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE)
					.header(AUTHORIZATION_KEY, token)
					.retrieve();
			
			String response = responseSpec.bodyToMono(String.class).block();
			log.trace("End response={}",response);
			return response;
			
		} catch (Exception e) {
			throw processException(e);
		}
	}

	@Override
	public String patch(HttpConnectDto dto) {
		try {
			log.trace("Start dto={}", dto);
			
			String url = getQueryParam(dto); 
			
			final WebClient webClient = WebClient.create();
			
			String response = 
					webClient.patch()
					.uri(url)
					.body(Mono.just(dto.getRequestBody()), dto.getRequestBody().getClass())
					.header(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE)
					.retrieve()
					.bodyToMono(String.class)
					.block();
			
			
			log.trace("End response={}", response);
			
			return response;
		} catch (Exception e) {
			throw processException(e);
		}
	}

	
	private HttpConnectorException processException(Exception e) {
		int statusCode = 500;
		String message = e.getMessage();
		
		if(e instanceof WebClientResponseException ex) {
			statusCode = ex.getStatusCode().value();
			message = ex.getResponseBodyAsString();
			
			log.warn("HTTP Status: {},  Response body: {}", statusCode, message);
		}
		
		log.error(e.getMessage(), e);
		return new HttpConnectorException(statusCode, message);
	}

	private String getQueryParam(HttpConnectDto dto) {
		return dto.getQueryParams() == null ? dto.getUrl() : dto.getUrl().concat(dto.getQueryParamUrl());
	}
	
	private String getToken(HttpConnectDto dto) {
		return dto.getHeaders() == null ? "" : dto.getHeaders().get(AUTHORIZATION_KEY);
	}


}
