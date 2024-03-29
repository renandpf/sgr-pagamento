package br.com.pupposoft.fiap.sgr.pagamento.core.gateway;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.PlataformaPagamento;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EnviaPagamentoExternoParamDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EnviaPagamentoReturnDto;

public abstract class PlataformaPagamentoGateway {
	protected PlataformaPagamento plataformaPagamentoExterna;
	
	public abstract EnviaPagamentoReturnDto enviarPagamento(EnviaPagamentoExternoParamDto dto);
	public abstract StatusPedido obtemStatus(String identificadorPagamento);

	public boolean isElegivel(PlataformaPagamento plataformaPagamentoExterna) {
		return plataformaPagamentoExterna.equals(this.plataformaPagamentoExterna);
	}
}
