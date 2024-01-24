package br.com.pupposoft.fiap.sgr.pagamento.core.gateway;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.junit.jupiter.api.Test;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.PlataformaPagamento;

class PlataformaPagamentoGatewayUnitTest {

	@Test
	void shouldSucessOnIsElegivel() {
		PlataformaPagamentoStub plataformaPagamentoStub = new PlataformaPagamentoStub();
		setField(plataformaPagamentoStub, "plataformaPagamentoExterna", PlataformaPagamento.MERCADO_PAGO);
		
		assertTrue(plataformaPagamentoStub.isElegivel(PlataformaPagamento.MERCADO_PAGO));
		assertFalse(plataformaPagamentoStub.isElegivel(PlataformaPagamento.PAG_SEGURO));
	}
	
}
