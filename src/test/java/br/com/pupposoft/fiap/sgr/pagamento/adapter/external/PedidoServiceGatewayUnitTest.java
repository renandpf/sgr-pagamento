package br.com.pupposoft.fiap.sgr.pagamento.adapter.external;

import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomLong;
import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomString;
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pupposoft.fiap.sgr.pagamento.adapter.external.json.PedidoMessageJson;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PedidoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.ErrorToAccessPedidoServiceException;
import br.com.pupposoft.fiap.starter.http.HttpConnectGateway;

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
