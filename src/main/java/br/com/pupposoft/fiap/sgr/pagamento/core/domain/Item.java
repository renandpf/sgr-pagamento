package br.com.pupposoft.fiap.sgr.pagamento.core.domain;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class Item {
	private Long id;
	
	private Long quantidade;
	private BigDecimal valorUnitario;
	
	public BigDecimal getValorTotal() {
		return new BigDecimal(quantidade).multiply(valorUnitario);
	}
}
