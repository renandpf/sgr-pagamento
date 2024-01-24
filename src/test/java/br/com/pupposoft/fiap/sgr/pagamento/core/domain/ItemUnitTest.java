package br.com.pupposoft.fiap.sgr.pagamento.core.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ItemUnitTest {

	
	@Test
	void shouldSucessOnGetValorTotal() {
		
		Item item = Item.builder()
				.quantidade(3L)
				.valorUnitario(new BigDecimal("5"))
				.build();
		
		assertEquals(new BigDecimal("15"), item.getValorTotal());
	}
	
}
