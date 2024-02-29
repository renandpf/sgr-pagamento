package br.com.pupposoft.fiap.sgr.pagamento.adapter.qeue;

import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pupposoft.fiap.sgr.pagamento.adapter.qeue.json.PedidoMessageJson;
import br.com.pupposoft.fiap.sgr.pagamento.core.controller.PagamentoController;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.ClienteDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PagamentoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PedidoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EfetuarPagamentoParamDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Profile("prd")
@AllArgsConstructor
@Slf4j
public class EfetuarPagamentoQeueGateway {

	private PagamentoController pagamentoController;
	
	private ObjectMapper mapper;

	@JmsListener(destination = "${sgr.queue.efetuar-pagamento}", containerFactory = "efetuarPagamentoSqsFactory")
	public void efetuar(String message) {
		try {
			log.info("Received message: {}", message);
			
			PedidoMessageJson pedidoMessageJson = mapper.readValue(message, PedidoMessageJson.class);
			
			ClienteDto clienteDto = ClienteDto.builder()
				.id(pedidoMessageJson.getCliente().getId())
				.nome(pedidoMessageJson.getCliente().getNome())
				.telefone(pedidoMessageJson.getCliente().getTelefone())
				.email(pedidoMessageJson.getCliente().getEmail())
				.build();
			
			PedidoDto pedidoDto = PedidoDto.builder()
			.id(pedidoMessageJson.getId())
			.cliente(clienteDto)
			.valor(pedidoMessageJson.getValor())
			.status(StatusPedido.valueOf(pedidoMessageJson.getStatus()))
			.build();
			
			PagamentoDto pagamentoDto = PagamentoDto.builder()
			.id(null)
			.formaPagamento("PIX")
			.valor(pedidoMessageJson.getValor())
			.pedido(pedidoDto)
			.pagamentoExternoId(null)
			.build();
			
			EfetuarPagamentoParamDto paramDto = EfetuarPagamentoParamDto.builder()
					.pagamento(pagamentoDto)
					.build();
			
			pagamentoController.efetuar(paramDto);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);//NOSONAR
		}
	}
	
}
