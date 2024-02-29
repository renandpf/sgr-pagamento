package br.com.pupposoft.fiap.sgr.pagamento.adapter.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pupposoft.fiap.sgr.pagamento.adapter.external.json.PedidoMessageJson;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PedidoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.ErrorToAccessPedidoServiceException;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PedidoGateway;
import br.com.pupposoft.fiap.starter.http.HttpConnectGateway;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoServiceGateway implements PedidoGateway {

	@Value("${sgr.pedido-service.url}")
	private String baseUrl;
	
	@NonNull
	private HttpConnectGateway httpConnectGateway;

	@NonNull
    private ObjectMapper mapper;
	
	@NonNull
	private Environment environment;
	
	@Autowired//NOSONAR
	@Qualifier("statusPedidoTemplate")
	private JmsTemplate statusPedidoTemplate;
	
	@Async // Para não travar o fluxo de pagamento
	@Override
	public void alterarStatus(PedidoDto pedido) {
		try {
			
			if(isProdActiveProfile()) {
				String dtoJsonStr = mapper.writeValueAsString(new PedidoMessageJson(pedido.getId(), pedido.getStatus().name()));
				statusPedidoTemplate.convertAndSend("atualiza-status-pedido-qeue", dtoJsonStr);
				
			} else {
				log.warn("## MOCK ##");
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ErrorToAccessPedidoServiceException();
		}
	}
	
	private boolean isProdActiveProfile() {
		String[] activeProfiles = environment.getActiveProfiles();
		String activeProfile = activeProfiles[0];
		return "prd".equals(activeProfile);
	}
}
