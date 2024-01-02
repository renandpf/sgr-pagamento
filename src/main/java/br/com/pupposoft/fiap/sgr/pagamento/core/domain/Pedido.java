package br.com.pupposoft.fiap.sgr.pagamento.core.domain;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class Pedido {
	private Long id;
	private List<Item> itens;
	
	@Setter
	private StatusPedido status;
	
    public BigDecimal getValorTotal() {
    	return itens.stream().map(Item::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
