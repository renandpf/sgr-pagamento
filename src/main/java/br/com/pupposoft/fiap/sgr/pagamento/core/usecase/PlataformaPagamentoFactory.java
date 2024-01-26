package br.com.pupposoft.fiap.sgr.pagamento.core.usecase;

import java.util.List;
import java.util.Optional;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.PlataformaPagamento;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.PlataformaPagamentoConfigParamsDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.PlataformaPagamentoConfigReturnDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.PlataformaPagamentoGatewayNotFoundException;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PlataformaPagamentoConfigGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PlataformaPagamentoGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class PlataformaPagamentoFactory {

	private PlataformaPagamentoConfigGateway plataformaPagamentoConfigGateway;
	
	private List<PlataformaPagamentoGateway> plataformaPagamentoGatewayList;
	
	public PlataformaPagamentoGateway obter() {
		PlataformaPagamentoConfigReturnDto returnDto = plataformaPagamentoConfigGateway.obter(PlataformaPagamentoConfigParamsDto.builder().build());
		
		return obter(returnDto.getPlataformaPagamento());
	}

	public PlataformaPagamentoGateway obter(PlataformaPagamento plataformaPagamento) {
		
		Optional<PlataformaPagamentoGateway> plataformaPagamentoGatewayOp = plataformaPagamentoGatewayList.stream().filter(pp -> pp.isElegivel(plataformaPagamento)).findAny();
		
		if(plataformaPagamentoGatewayOp.isEmpty()) {
			log.warn("Plataforma de pagamento não encontrada. Verifique a configuração do sistema");
			throw new PlataformaPagamentoGatewayNotFoundException();
		}
		
		return plataformaPagamentoGatewayOp.get();
	}
}
