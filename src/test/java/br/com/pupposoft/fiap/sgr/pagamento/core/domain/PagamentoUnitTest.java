package br.com.pupposoft.fiap.sgr.pagamento.core.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PagamentoUnitTest {

	
	@Test
	void pagamento() {
		Pagamento pagamento = Pagamento.builder()
				.id(1L)
				.pedido(Pedido.builder().id(2L).build())
				.build();
		
		assertEquals(1L, pagamento.getId());
		assertEquals(2L, pagamento.getPedido().getId());
		
		pagamento.toString();
	}
	
}
