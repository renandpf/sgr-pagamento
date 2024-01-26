package br.com.pupposoft.fiap.sgr.pagamento.core.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ItemUnitTest {

	
	@Test
	void shouldSucessOnGetValorTotal() {
		
		Item item = Item.builder()
				.id(1L)
				.quantidade(3L)
				.valorUnitario(new BigDecimal("5"))
				.build();
		
		assertEquals(new BigDecimal("15"), item.getValorTotal());
		
		assertEquals(1L, item.getId());
		assertEquals(3L, item.getQuantidade());
		assertEquals(new BigDecimal("5"), item.getValorUnitario());
		
		item.toString();
	}
	
}
