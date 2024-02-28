package br.com.pupposoft.fiap.sgr.pagamento.adapter.qeue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pupposoft.fiap.sgr.pagamento.core.dto.NotificarDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.ErrorToNotifiyException;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.NotificarGateway;
import lombok.extern.slf4j.Slf4j;


@Profile({"prd"})
@Component
@Slf4j
public class NotificarAwsSqsGateway implements NotificarGateway{

	@Autowired//NOSONAR
	private JmsTemplate notifyTemplate;
	
	@Autowired//NOSONAR
	private ObjectMapper mapper;
	
	@Async//Para não travar o fluxo de criação de pedido 
	@Override
	public void notificar(NotificarDto dto) {
		
		try {
			
			String dtoJsonStr = mapper.writeValueAsString(dto);
			
			notifyTemplate.convertAndSend("notificar-qeue", dtoJsonStr);
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ErrorToNotifiyException();
		}

		
	}

}
