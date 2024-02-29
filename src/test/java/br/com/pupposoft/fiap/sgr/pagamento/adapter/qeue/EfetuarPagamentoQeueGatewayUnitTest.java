package br.com.pupposoft.fiap.sgr.pagamento.adapter.qeue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pupposoft.fiap.sgr.pagamento.adapter.qeue.json.ClienteJson;
import br.com.pupposoft.fiap.sgr.pagamento.adapter.qeue.json.PedidoMessageJson;
import br.com.pupposoft.fiap.sgr.pagamento.core.controller.PagamentoController;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PagamentoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EfetuarPagamentoParamDto;

import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.*;

@ExtendWith(MockitoExtension.class)
class EfetuarPagamentoQeueGatewayUnitTest {

	@InjectMocks
	private EfetuarPagamentoQeueGateway efetuarPagamentoQeueGateway;
	
	@Mock
	private PagamentoController pagamentoController;
	
	@Mock
	private ObjectMapper mapper;
	
	@Test
	void shouldSucessOnNotificar() throws Exception {
		String receivedMessage = getRandomString(); 
		
		
		PedidoMessageJson pedidoMessageJson = PedidoMessageJson.builder()
				.id(null)
				.cliente(ClienteJson.builder().id(getRandomLong()).email(getRandomString()).telefone(getRandomString()).build())
				.valor(getRandomDouble())
				.status("RECEBIDO")
				.build();
		doReturn(pedidoMessageJson).when(mapper).readValue(receivedMessage, PedidoMessageJson.class);
		
		efetuarPagamentoQeueGateway.efetuar(receivedMessage);
		
		ArgumentCaptor<EfetuarPagamentoParamDto> efetuarPagamentoParamDtoAC = ArgumentCaptor.forClass(EfetuarPagamentoParamDto.class);	
		
		verify(pagamentoController).efetuar(efetuarPagamentoParamDtoAC.capture());
		
		EfetuarPagamentoParamDto paramDtoSent = efetuarPagamentoParamDtoAC.getValue();
		
		PagamentoDto pagamentoDtoSent = paramDtoSent.getPagamento();
		
		assertNull(pagamentoDtoSent.getId());
		assertNull(pagamentoDtoSent.getPagamentoExternoId());
		assertEquals("PIX", pagamentoDtoSent.getFormaPagamento());
		assertEquals(pedidoMessageJson.getValor(), pagamentoDtoSent.getValor());
		assertEquals(pedidoMessageJson.getId(), pagamentoDtoSent.getPedido().getId());
		assertEquals(pedidoMessageJson.getStatus(), pagamentoDtoSent.getPedido().getStatus().name());
		assertEquals(pedidoMessageJson.getValor(), pagamentoDtoSent.getPedido().getValor());
		assertEquals(pedidoMessageJson.getCliente().getId(), pagamentoDtoSent.getPedido().getClienteId());
		assertEquals(pedidoMessageJson.getCliente().getEmail(), pagamentoDtoSent.getPedido().getClienteEmail());
		assertEquals(pedidoMessageJson.getCliente().getTelefone(), pagamentoDtoSent.getPedido().getClienteTelefone());
		assertEquals(pedidoMessageJson.getCliente().getNome(), pagamentoDtoSent.getPedido().getClienteNome());
	}
	
	@Test
	void shouldErrorOnNotificar() throws Exception {
		String receivedMessage = getRandomString(); 
		
		doThrow(new RuntimeException()).when(mapper).readValue(receivedMessage, PedidoMessageJson.class);
		
		assertThrows(RuntimeException.class, () -> efetuarPagamentoQeueGateway.efetuar(receivedMessage));
		
	}
	
}
