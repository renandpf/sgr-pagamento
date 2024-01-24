package br.com.pupposoft.fiap.sgr.pagamento.core.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StatusPedidoUnitTest {

	
	@Test
	void shouldSucessOnGetById() {
		StatusPedido statusPedido = StatusPedido.get(0L);
		assertEquals(StatusPedido.RECEBIDO, statusPedido);
		
		statusPedido = StatusPedido.get(1L);
		assertEquals(StatusPedido.AGUARDANDO_CONFIRMACAO_PAGAMENTO, statusPedido);

		statusPedido = StatusPedido.get(2L);
		assertEquals(StatusPedido.PAGO, statusPedido);
		
		statusPedido = StatusPedido.get(3L);
		assertEquals(StatusPedido.EM_PREPARACAO, statusPedido);
		
		statusPedido = StatusPedido.get(4L);
		assertEquals(StatusPedido.PRONTO, statusPedido);
		
		statusPedido = StatusPedido.get(5L);
		assertEquals(StatusPedido.FINALIZADO, statusPedido);
		
		statusPedido = StatusPedido.get(6L);
		assertEquals(StatusPedido.PAGAMENTO_INVALIDO, statusPedido);
	}
	
	@Test
	void shouldSucessOnGetByEnum() {
		Long id = StatusPedido.get(StatusPedido.RECEBIDO);
		assertEquals(0, id);
		
		id = StatusPedido.get(StatusPedido.AGUARDANDO_CONFIRMACAO_PAGAMENTO);
		assertEquals(1, id);
		
		id = StatusPedido.get(StatusPedido.PAGO);
		assertEquals(2, id);
		
		id = StatusPedido.get(StatusPedido.EM_PREPARACAO);
		assertEquals(3, id);
		
		id = StatusPedido.get(StatusPedido.PRONTO);
		assertEquals(4, id);
		
		id = StatusPedido.get(StatusPedido.FINALIZADO);
		assertEquals(5, id);
		
		id = StatusPedido.get(StatusPedido.PAGAMENTO_INVALIDO);
		assertEquals(6, id);
		
	}
	
}
