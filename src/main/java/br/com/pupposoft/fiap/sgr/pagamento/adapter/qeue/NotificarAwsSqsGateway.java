package br.com.pupposoft.fiap.sgr.pagamento.adapter.qeue;

import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import br.com.pupposoft.fiap.sgr.pagamento.core.dto.NotificarDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.ErrorToNotifiyException;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.NotificarGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Profile({"prd"})
@Component
@AllArgsConstructor
@Slf4j
public class NotificarAwsSqsGateway implements NotificarGateway{

	private JmsTemplate notifyTemplate;
	
	@Async//Para não travar o fluxo de criação de pedido 
	@Override
	public void notificar(NotificarDto dto) {
		
		try {
			
			notifyTemplate.convertAndSend("notificar-qeue", dto);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ErrorToNotifiyException();
		}

		
	}

}
