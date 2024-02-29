package br.com.pupposoft.fiap.sgr.pagamento.adapter.external;

import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.PlataformaPagamento;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EnviaPagamentoExternoParamDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EnviaPagamentoReturnDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.ErrorToAccessPagamentoServicoExternoException;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PlataformaPagamentoGateway;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PlataformaPagamentoMockGateway extends PlataformaPagamentoGateway {

	public PlataformaPagamentoMockGateway() {
		plataformaPagamentoExterna = PlataformaPagamento.MOCK;
	}
	
	@Override
	public EnviaPagamentoReturnDto enviarPagamento(EnviaPagamentoExternoParamDto dto) {
        try {
            log.warn("### MOCK ###");

            return EnviaPagamentoReturnDto.builder()
            		.pagamentoExternoId(UUID.randomUUID().toString())
            		.build();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ErrorToAccessPagamentoServicoExternoException();
        }
	}

	@Override
	public StatusPedido obtemStatus(String identificadorPagamento) {
		log.warn("### MOCK ###");
		return StatusPedido.PAGO;
    }
}
