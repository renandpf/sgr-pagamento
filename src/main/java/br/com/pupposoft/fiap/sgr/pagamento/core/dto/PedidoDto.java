package br.com.pupposoft.fiap.sgr.pagamento.core.dto;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PedidoDto {
    private Long id;
    private ClienteDto cliente;
    
    @Setter
    private StatusPedido status;
    
    @Setter
    private Double valor;
}
