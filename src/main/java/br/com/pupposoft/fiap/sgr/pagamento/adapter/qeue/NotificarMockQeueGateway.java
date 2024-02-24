package br.com.pupposoft.fiap.sgr.pagamento.adapter.qeue;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import br.com.pupposoft.fiap.sgr.pagamento.core.dto.NotificarDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.NotificarGateway;
import lombok.extern.slf4j.Slf4j;

@Profile({"!prd"})
@Slf4j
@Component
public class NotificarMockQeueGateway implements NotificarGateway {

	@Override
	public void notificar(NotificarDto dto) {
		log.warn("### MOCK ###");
	}

}
