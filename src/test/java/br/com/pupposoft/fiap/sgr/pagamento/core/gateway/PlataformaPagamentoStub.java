package br.com.pupposoft.fiap.sgr.pagamento.core.gateway;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EnviaPagamentoExternoParamDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EnviaPagamentoReturnDto;

public class PlataformaPagamentoStub extends PlataformaPagamentoGateway {

	@Override
	public EnviaPagamentoReturnDto enviarPagamento(EnviaPagamentoExternoParamDto dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatusPedido obtemStatus(String identificadorPagamento) {
		// TODO Auto-generated method stub
		return null;
	}

}
