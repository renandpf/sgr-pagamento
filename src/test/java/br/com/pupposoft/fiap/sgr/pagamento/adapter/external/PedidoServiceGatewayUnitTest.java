package br.com.pupposoft.fiap.sgr.pagamento.adapter.external;

import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomDouble;
import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomLong;
import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomString;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pupposoft.fiap.sgr.pagamento.adapter.external.json.ItemJson;
import br.com.pupposoft.fiap.sgr.pagamento.adapter.external.json.PedidoJson;
import br.com.pupposoft.fiap.sgr.pagamento.adapter.external.json.PedidoMessageJson;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PedidoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.ErrorToAccessPedidoServiceException;
import br.com.pupposoft.fiap.starter.http.HttpConnectGateway;
import br.com.pupposoft.fiap.starter.http.dto.HttpConnectDto;
import br.com.pupposoft.fiap.starter.http.exception.HttpConnectorException;
import br.com.pupposoft.fiap.test.databuilder.DataBuilderBase;

@ExtendWith(MockitoExtension.class)
class PedidoServiceGatewayUnitTest {
	
	@InjectMocks
	private PedidoServiceGateway pedidoGateway;
	
	@Mock
	private HttpConnectGateway httpConnectGateway;

    @Mock
    private ObjectMapper mapper;

	@Mock
	private Environment environment;
	
	@Mock
	private JmsTemplate statusPedidoTemplate;

    
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
	void shouldSucessAlterarStatusInNoPrdProfile() throws Exception {
		final PedidoDto pedidoDto = PedidoDto.builder()
				.id(getRandomLong())
				.status(StatusPedido.AGUARDANDO_CONFIRMACAO_PAGAMENTO)
				.build();
		
		String profiles[] = {"anyNoPrd"};
		doReturn(profiles).when(environment).getActiveProfiles();
		
		pedidoGateway.alterarStatus(pedidoDto);
		
		verify(statusPedidoTemplate, never()).convertAndSend(anyString(), anyString());
	}
	
	@Test
	void shouldSucessAlterarStatusInPrdProfile() throws Exception {
		setField(pedidoGateway, "statusPedidoTemplate", statusPedidoTemplate);
		
		final PedidoDto pedidoDto = PedidoDto.builder()
				.id(getRandomLong())
				.status(StatusPedido.AGUARDANDO_CONFIRMACAO_PAGAMENTO)
				.build();
		
		
		String profiles[] = {"prd"};
		doReturn(profiles).when(environment).getActiveProfiles();
		
		String pedidoMessageJsonStr = getRandomString();
		doReturn(pedidoMessageJsonStr).when(mapper).writeValueAsString(any(PedidoMessageJson.class));
		
		pedidoGateway.alterarStatus(pedidoDto);

		ArgumentCaptor<PedidoMessageJson> pedidoMessageJsonAC = ArgumentCaptor.forClass(PedidoMessageJson.class);
		verify(mapper).writeValueAsString(pedidoMessageJsonAC.capture());
		PedidoMessageJson pedidoMessageJsonDto = pedidoMessageJsonAC.getValue();
		assertEquals(pedidoDto.getId(), pedidoMessageJsonDto.getId());
		assertEquals(pedidoDto.getStatus().name(), pedidoMessageJsonDto.getStatus());
		
		
		verify(statusPedidoTemplate).convertAndSend("atualiza-status-pedido-qeue", pedidoMessageJsonStr);
	}
	
	@Test
	void shouldErrorAlterarStatusInPrdProfile() throws Exception {
		setField(pedidoGateway, "statusPedidoTemplate", statusPedidoTemplate);
		
		final PedidoDto pedidoDto = PedidoDto.builder()
				.id(getRandomLong())
				.status(StatusPedido.AGUARDANDO_CONFIRMACAO_PAGAMENTO)
				.build();

		
		String profiles[] = {"prd"};
		doReturn(profiles).when(environment).getActiveProfiles();
		
		String pedidoMessageJsonStr = getRandomString();
		doReturn(pedidoMessageJsonStr).when(mapper).writeValueAsString(any(PedidoMessageJson.class));
		
		doThrow(new RuntimeException()).when(statusPedidoTemplate).convertAndSend(anyString(), anyString());
		
		assertThrows(ErrorToAccessPedidoServiceException.class, () -> pedidoGateway.alterarStatus(pedidoDto));
		
		verify(statusPedidoTemplate).convertAndSend(eq("atualiza-status-pedido-qeue"), anyString());
	}

}
