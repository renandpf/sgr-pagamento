package br.com.pupposoft.fiap.sgr.pagamento.adapter.external;

import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomDouble;
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

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pupposoft.fiap.sgr.pagamento.adapter.external.json.ItemJson;
import br.com.pupposoft.fiap.sgr.pagamento.adapter.external.json.PedidoJson;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PedidoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.ErrorToAccessPedidoServiceException;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PedidoGateway;
import br.com.pupposoft.fiap.starter.http.HttpConnectGateway;
import br.com.pupposoft.fiap.starter.http.dto.HttpConnectDto;
import br.com.pupposoft.fiap.starter.http.exception.HttpConnectorException;
import br.com.pupposoft.fiap.test.databuilder.DataBuilderBase;

@ExtendWith(MockitoExtension.class)
class PedidoServiceGatewayIntTest {
	
	@InjectMocks
	private PedidoGateway pedidoGateway = new PedidoServiceGateway();
	
	@Mock
	private HttpConnectGateway httpConnectGateway;

    @Mock
    private ObjectMapper mapper;

	@Test
	void shouldSucessFoundOnObterPorId() throws Exception {
		final Long pedidoIdParam = getRandomLong();
		final String response = getRandomString();
		final String baseUrl = getRandomString();
		
		setField(pedidoGateway, "baseUrl", baseUrl);
		
		doReturn(response).when(httpConnectGateway).get(any(HttpConnectDto.class));

		PedidoJson pedidoJson = new PedidoJson(
				pedidoIdParam,
				getRandomString(),
				StatusPedido.AGUARDANDO_CONFIRMACAO_PAGAMENTO,
				DataBuilderBase.getRandomLocalDate(),
				DataBuilderBase.getRandomLocalDate(),
				getRandomLong(),
				Arrays.asList(new ItemJson(
						getRandomLong(),
						getRandomLong(),
						getRandomLong(),
						getRandomString(),
						getRandomDouble())));
		
		doReturn(pedidoJson).when(mapper).readValue(response, PedidoJson.class);
		
		Optional<PedidoDto> pedidoDto = pedidoGateway.obterPorId(pedidoIdParam);
		assertTrue(pedidoDto.isPresent());

		PedidoDto pedidoDtoReturned = pedidoDto.get();
		
		assertEquals(pedidoJson.getId(), pedidoDtoReturned.getId());
		assertEquals(pedidoJson.getClienteId(), pedidoDtoReturned.getClienteId());
		assertEquals(pedidoJson.getStatus(), pedidoDtoReturned.getStatus());
		
		ArgumentCaptor<HttpConnectDto> httpConnectDtoAC = ArgumentCaptor.forClass(HttpConnectDto.class);
		verify(httpConnectGateway).get(httpConnectDtoAC.capture());
		HttpConnectDto httpCpnnectDto = httpConnectDtoAC.getValue();
		
		assertEquals(baseUrl + "/sgr/pedidos/" + pedidoIdParam, httpCpnnectDto.getUrl());
	}

	@Test
	void shouldSucessNotFoundOnObterPorId() throws Exception {
		final Long pedidoIdParam = getRandomLong();
		
		HttpConnectorException notFoundException = Mockito.mock(HttpConnectorException.class);
		doReturn(404).when(notFoundException).getHttpStatus();
		doThrow(notFoundException).when(httpConnectGateway).get(any(HttpConnectDto.class));

		Optional<PedidoDto> pedidoDto = pedidoGateway.obterPorId(pedidoIdParam);
		assertTrue(pedidoDto.isEmpty());
		
		verify(httpConnectGateway).get(any(HttpConnectDto.class));
	}
	
	@Test
	void shouldErrorToAccessPedidoServiceExceptionNotFoundOnObterPorId() throws Exception {
		final Long pedidoIdParam = getRandomLong();
		
		HttpConnectorException notFoundException = Mockito.mock(HttpConnectorException.class);
		doReturn(404).when(notFoundException).getHttpStatus();
		doThrow(notFoundException).when(httpConnectGateway).get(any(HttpConnectDto.class));
		
		Optional<PedidoDto> pedidoDto = pedidoGateway.obterPorId(pedidoIdParam);
		assertTrue(pedidoDto.isEmpty());
		
		verify(httpConnectGateway).get(any(HttpConnectDto.class));
	}
	
	@Test
	void shouldErrorToAccessPedidoServiceExceptionOnObterPorId() throws Exception {
		final Long pedidoIdParam = getRandomLong();
		
		HttpConnectorException exception = Mockito.mock(HttpConnectorException.class);
		doReturn(500).when(exception).getHttpStatus();
		doThrow(exception).when(httpConnectGateway).get(any(HttpConnectDto.class));
		
		assertThrows(ErrorToAccessPedidoServiceException.class, () -> pedidoGateway.obterPorId(pedidoIdParam));
		verify(httpConnectGateway).get(any(HttpConnectDto.class));
	}
	
	@Test
	void shouldSucessAlterarStatus() throws Exception {
		final PedidoDto pedidoDto = PedidoDto.builder().build();
		final String response = getRandomString();
		final String baseUrl = getRandomString();
		
		setField(pedidoGateway, "baseUrl", baseUrl);
		
		doReturn(response).when(httpConnectGateway).patch(any(HttpConnectDto.class));
		
		pedidoGateway.alterarStatus(pedidoDto);
		
		ArgumentCaptor<HttpConnectDto> httpConnectDtoAC = ArgumentCaptor.forClass(HttpConnectDto.class);
		verify(httpConnectGateway).patch(httpConnectDtoAC.capture());
		HttpConnectDto httpCpnnectDto = httpConnectDtoAC.getValue();
		
		assertEquals(baseUrl + "/sgr/pedidos/" + pedidoDto.getId() + "/status", httpCpnnectDto.getUrl());
		assertEquals(pedidoDto.getStatus(), ((PedidoJson) httpCpnnectDto.getRequestBody()).getStatus());
	}
	
	@Test
	void shouldErrorToAccessPedidoServiceExceptionOnAlterarStatus() throws Exception {
		final PedidoDto pedidoDto = PedidoDto.builder().build();
		
		doThrow(new RuntimeException()).when(httpConnectGateway).patch(any(HttpConnectDto.class));
		
		assertThrows(ErrorToAccessPedidoServiceException.class, () -> pedidoGateway.alterarStatus(pedidoDto));
	}
}
