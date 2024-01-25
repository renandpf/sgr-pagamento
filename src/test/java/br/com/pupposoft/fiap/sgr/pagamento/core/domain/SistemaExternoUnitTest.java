package br.com.pupposoft.fiap.sgr.pagamento.core.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SistemaExternoUnitTest {

	
	@Test
	void sistemaExterno() {
		SistemaExterno domain = SistemaExterno.builder()
				.identificadorPagamento("any")
				.build();
		
		assertEquals("any", domain.getIdentificadorPagamento());
		
		domain.toString();
	}
	
}
