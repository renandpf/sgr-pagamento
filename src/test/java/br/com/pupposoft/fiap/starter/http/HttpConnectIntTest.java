package br.com.pupposoft.fiap.starter.http;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import br.com.pupposoft.fiap.starter.http.dto.HttpConnectDto;
import br.com.pupposoft.fiap.starter.http.exception.HttpConnectorException;

@WireMockTest
class HttpConnectIntTest {

	@Test
	void shouldSucessOnPostWhithRequestBody(WireMockRuntimeInfo wmRuntimeInfo) {
		
		final String path = "/test";
		final String responseBodyToBeReturned = "{\"id\": \"TESTE\"  }";
		stubFor(post(path).willReturn(okJson(responseBodyToBeReturned)));
		
		final String url = wmRuntimeInfo.getHttpBaseUrl() + "/test";
		HttpConnectGateway httpConnect = new HttpConnect();
		final HttpConnectDto dto = HttpConnectDto.builder()
				.url(url)
				.requestBody("TESTE")
				.build();
		
		final String responseBody = httpConnect.postWhithRequestBody(dto);
		
		assertEquals(responseBodyToBeReturned, responseBody);
	}
	
	@Test
	void shouldErrorOnPostWhithRequestBody(WireMockRuntimeInfo wmRuntimeInfo) {
		
		final String path = "/test";
		stubFor(post(path).willReturn(serverError()));
		
		final String url = wmRuntimeInfo.getHttpBaseUrl() + "/test";
		HttpConnectGateway httpConnect = new HttpConnect();
		final HttpConnectDto dto = HttpConnectDto.builder()
				.url(url)
				.requestBody("TESTE")
				.build();
		
		assertThrows(HttpConnectorException.class, () -> httpConnect.postWhithRequestBody(dto));
	}
	
	@Test
	void shouldSucessOnGet(WireMockRuntimeInfo wmRuntimeInfo) {
		
		final String path = "/test?foo=bar";
		final String responseBodyToBeReturned = "{\"id\": \"TESTE\"  }";
		stubFor(get(path).willReturn(okJson(responseBodyToBeReturned)));
		
		final Map<String, String> params = new HashMap<>();
		params.put("foo", "bar");
		
		final Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", "xyz");
		
		final String url = wmRuntimeInfo.getHttpBaseUrl() + "/test";
		HttpConnectGateway httpConnect = new HttpConnect();
		final HttpConnectDto dto = HttpConnectDto.builder()
				.url(url)
				.queryParams(params)
				.headers(headers)
				.build();
		
		final String responseBody = httpConnect.get(dto);
		
		assertEquals(responseBodyToBeReturned, responseBody);
	}
	
	@Test
	void shouldSucessOnGetNoParamAndNoHeader(WireMockRuntimeInfo wmRuntimeInfo) {
		
		final String path = "/test";
		final String responseBodyToBeReturned = "{\"id\": \"TESTE\"  }";
		stubFor(get(path).willReturn(okJson(responseBodyToBeReturned)));
		
		final String url = wmRuntimeInfo.getHttpBaseUrl() + "/test";
		HttpConnectGateway httpConnect = new HttpConnect();
		final HttpConnectDto dto = HttpConnectDto.builder()
				.url(url)
				.queryParams(null)
				.headers(null)
				.build();
		
		final String responseBody = httpConnect.get(dto);
		
		assertEquals(responseBodyToBeReturned, responseBody);
	}
	
	@Test
	void shouldErrorOnGet(WireMockRuntimeInfo wmRuntimeInfo) {
		
		final String path = "/test";
		stubFor(get(path).willReturn(serverError()));
		
		final String url = wmRuntimeInfo.getHttpBaseUrl() + "/test";
		HttpConnectGateway httpConnect = new HttpConnect();
		final HttpConnectDto dto = HttpConnectDto.builder()
				.url(url)
				.queryParams(null)
				.headers(null)
				.build();
		
		assertThrows(HttpConnectorException.class, () -> httpConnect.get(dto));
	}
	
	@Test
	void shouldError2OnGet(WireMockRuntimeInfo wmRuntimeInfo) {
		
		HttpConnectGateway httpConnect = new HttpConnect();
		final HttpConnectDto dto = null;
		
		assertThrows(HttpConnectorException.class, () -> httpConnect.get(dto));
	}
	
	@Test
	void shouldSucesspatch(WireMockRuntimeInfo wmRuntimeInfo) {
		
		final String path = "/test";
		final String responseBodyToBeReturned = "{\"id\": \"TESTE\"  }";
		stubFor(patch(path).willReturn(okJson(responseBodyToBeReturned)));
		
		final String url = wmRuntimeInfo.getHttpBaseUrl() + "/test";
		HttpConnectGateway httpConnect = new HttpConnect();
		final HttpConnectDto dto = HttpConnectDto.builder()
				.url(url)
				.requestBody("TESTE")
				.build();
		
		final String responseBody = httpConnect.patch(dto);
		
		assertEquals(responseBodyToBeReturned, responseBody);
	}
	
	@Test
	void shouldErrorPatch(WireMockRuntimeInfo wmRuntimeInfo) {
		
		final String path = "/test";
		stubFor(patch(path).willReturn(serverError()));
		
		final String url = wmRuntimeInfo.getHttpBaseUrl() + "/test";
		HttpConnectGateway httpConnect = new HttpConnect();
		final HttpConnectDto dto = HttpConnectDto.builder()
				.url(url)
				.requestBody("TESTE")
				.build();
		
		assertThrows(HttpConnectorException.class, () -> httpConnect.patch(dto));
	}
	
}
