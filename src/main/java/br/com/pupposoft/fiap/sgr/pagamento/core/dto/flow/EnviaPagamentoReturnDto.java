package br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class EnviaPagamentoReturnDto {
	private String pagamentoExternoId;

}
