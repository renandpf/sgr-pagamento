package br.com.pupposoft.fiap.sgr.pagamento.core.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class PedidoUnitTest {

	
	@Test
	void shouldSucessOnGetValorTotal() {
		
		
		Item itemA = Item.builder()
				.quantidade(3L)
				.valorUnitario(new BigDecimal("5"))
				.build();
		Item itemB = Item.builder()
				.quantidade(4L)
				.valorUnitario(new BigDecimal("3.5"))
				.build();
		
		Pedido pedido = Pedido.builder()
				.itens(Arrays.asList(itemA, itemB))
				
				.build();
		
		assertEquals(new BigDecimal("29.0"), pedido.getValorTotal());
	}
	
}
