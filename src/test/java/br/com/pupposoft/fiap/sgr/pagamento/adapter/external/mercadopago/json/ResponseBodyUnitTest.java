package br.com.pupposoft.fiap.sgr.pagamento.adapter.external.mercadopago.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;

class ResponseBodyUnitTest {
	
	
	@Test
	void mapDomainStatusShouldAguardandoConfirmacaoPagamentoWhithPending() {
		ResponseBody responseBody = ResponseBody.builder()
				.status("pending")
				.build(); 
		
		assertEquals(StatusPedido.AGUARDANDO_CONFIRMACAO_PAGAMENTO, responseBody.mapDomainStatus());
	}
	
	@Test
	void mapDomainStatusShouldAguardandoConfirmacaoPagamentoWithInProcess() {
		ResponseBody responseBody = ResponseBody.builder()
				.status("in_process")
				.build(); 
		
		assertEquals(StatusPedido.AGUARDANDO_CONFIRMACAO_PAGAMENTO, responseBody.mapDomainStatus());
	}
	
	@Test
	void mapDomainStatusShouldAguardandoConfirmacaoPagamentoWithAuthorized() {
		ResponseBody responseBody = ResponseBody.builder()
				.status("authorized")
				.build(); 
		
		assertEquals(StatusPedido.AGUARDANDO_CONFIRMACAO_PAGAMENTO, responseBody.mapDomainStatus());
	}
	
	@Test
	void mapDomainStatusShouldPagoWithApproved() {
		ResponseBody responseBody = ResponseBody.builder()
				.status("approved")
				.build(); 
		
		assertEquals(StatusPedido.PAGO, responseBody.mapDomainStatus());
	}

	@Test
	void mapDomainStatusShouldPagoWithAnyOthers() {
		ResponseBody responseBody = ResponseBody.builder()
				.status("cancelled")
				.build(); 
		
		assertEquals(StatusPedido.PAGAMENTO_INVALIDO, responseBody.mapDomainStatus());
	}
	
}
