package br.com.pupposoft.fiap.sgr.pagamento.adapter.external.mercadopago.json;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseBody {
	private Long id;
	private String status;
	
	
	public StatusPedido mapDomainStatus() {
        StatusPedido status = StatusPedido.PAGAMENTO_INVALIDO;
        
		if(this.status.equals("pending") || this.status.equals("in_process") || this.status.equals("authorized")) {
			status = StatusPedido.AGUARDANDO_CONFIRMACAO_PAGAMENTO;
			
		} else if(this.status.equals("approved")) {
			status = StatusPedido.PAGO;
			
		} 
		
		return status;
	}
}
