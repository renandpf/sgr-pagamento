package br.com.pupposoft.fiap.sgr.pagamento.adapter.external;

import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomLong;
import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomString;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pupposoft.fiap.sgr.pagamento.adapter.external.json.ClienteJson;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.ClienteDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.ErrorToAccessPedidoServiceException;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.ClienteGateway;
import br.com.pupposoft.fiap.starter.http.HttpConnectGateway;
import br.com.pupposoft.fiap.starter.http.dto.HttpConnectDto;
import br.com.pupposoft.fiap.starter.http.exception.HttpConnectorException;

@ExtendWith(MockitoExtension.class)
class ClienteServiceGatewayUnitTest {
	
	@InjectMocks
	private ClienteGateway clienteGateway = new ClienteServiceGateway();
	
	@Mock
	private HttpConnectGateway httpConnectGateway;

    @Mock
    private ObjectMapper mapper;

	
	@Test
	void shouldSucessFoundOnObterPorId() throws Exception {
		final Long clientIdParam = getRandomLong();
		final String response = getRandomString();
		final String baseUrl = getRandomString();
		
		setField(clienteGateway, "baseUrl", baseUrl);
		
		doReturn(response).when(httpConnectGateway).get(any(HttpConnectDto.class));

		ClienteJson clienteJson = new ClienteJson();
		setField(clienteJson, "id", clientIdParam);
		setField(clienteJson, "nome", getRandomString());
		setField(clienteJson, "cpf", getRandomString());
		setField(clienteJson, "email", getRandomString());
		
		doReturn(clienteJson).when(mapper).readValue(response, ClienteJson.class);
		
		Optional<ClienteDto> clienteDto = clienteGateway.obterPorId(clientIdParam);
		assertTrue(clienteDto.isPresent());

		ClienteDto clienteDtoReturned = clienteDto.get();
		
		assertEquals(clienteJson.getId(), clienteDtoReturned.getId());
		assertEquals(clienteJson.getNome(), clienteDtoReturned.getNome());
		assertEquals(clienteJson.getCpf(), clienteDtoReturned.getCpf());
		assertEquals(clienteJson.getEmail(), clienteDtoReturned.getEmail());
		
		ArgumentCaptor<HttpConnectDto> httpConnectDtoAC = ArgumentCaptor.forClass(HttpConnectDto.class);
		verify(httpConnectGateway).get(httpConnectDtoAC.capture());
		HttpConnectDto httpCpnnectDto = httpConnectDtoAC.getValue();
		
		assertEquals(baseUrl + "/sgr/gerencial/clientes/" + clientIdParam, httpCpnnectDto.getUrl());
	}

	@Test
	void shouldSucessNotFoundOnObterPorId() throws Exception {
		final Long clientIdParam = getRandomLong();
		
		HttpConnectorException notFoundException = Mockito.mock(HttpConnectorException.class);
		doReturn(404).when(notFoundException).getHttpStatus();
		doThrow(notFoundException).when(httpConnectGateway).get(any(HttpConnectDto.class));

		Optional<ClienteDto> clienteDto = clienteGateway.obterPorId(clientIdParam);
		assertTrue(clienteDto.isEmpty());
		
		verify(httpConnectGateway).get(any(HttpConnectDto.class));
	}
	
	@Test
	void shouldErrorToAccessPedidoServiceExceptionNotFoundOnObterPorId() throws Exception {
		final Long clientIdParam = getRandomLong();
		
		HttpConnectorException notFoundException = Mockito.mock(HttpConnectorException.class);
		doReturn(404).when(notFoundException).getHttpStatus();
		doThrow(notFoundException).when(httpConnectGateway).get(any(HttpConnectDto.class));
		
		Optional<ClienteDto> clienteDto = clienteGateway.obterPorId(clientIdParam);
		assertTrue(clienteDto.isEmpty());
		
		verify(httpConnectGateway).get(any(HttpConnectDto.class));
	}
	
	@Test
	void shouldErrorToAccessPedidoServiceExceptionOnObterPorId() throws Exception {
		final Long clientIdParam = getRandomLong();
		
		HttpConnectorException exception = Mockito.mock(HttpConnectorException.class);
		doReturn(500).when(exception).getHttpStatus();
		doThrow(exception).when(httpConnectGateway).get(any(HttpConnectDto.class));
		
		assertThrows(ErrorToAccessPedidoServiceException.class, () -> clienteGateway.obterPorId(clientIdParam));
		verify(httpConnectGateway).get(any(HttpConnectDto.class));
	}
}
