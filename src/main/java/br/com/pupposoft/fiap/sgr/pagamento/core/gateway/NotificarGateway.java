package br.com.pupposoft.fiap.sgr.pagamento.core.gateway;

import br.com.pupposoft.fiap.sgr.pagamento.core.dto.NotificarDto;

public interface NotificarGateway {

	void notificar(NotificarDto dto);
	
}
