package br.com.pupposoft.fiap.sgr.pagamento.core.usecase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.ModoPagamento;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.NotificarDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PagamentoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PedidoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EfetuarPagamentoParamDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EfetuarPagamentoReturnDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EnviaPagamentoExternoParamDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EnviaPagamentoReturnDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.CamposObrigatoriosNaoPreechidoException;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.NotificarGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PagamentoGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PedidoGateway;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EfetuarPagamentoUseCaseImpl implements EfetuarPagamentoUseCase {

	private PedidoGateway pedidoGateway;
	
	private PlataformaPagamentoFactory plataformaPagamentoFactory;
	
	private PagamentoGateway pagamentoGateway;
	
	private NotificarGateway notificarGateway;
	
	@Override
	public EfetuarPagamentoReturnDto efetuar(EfetuarPagamentoParamDto paramsDto) {
        validaCamposObrigatorios(paramsDto.getPagamento());

        String pagamentoExternoId = enviaPagamentoSistemaExterno(paramsDto);
        paramsDto.getPagamento().setPagamentoExternoId(pagamentoExternoId);
        
        Long idPagamento = this.pagamentoGateway.criar(paramsDto.getPagamento());

        PedidoDto pedidoDto = paramsDto.getPagamento().getPedido();
        pedidoDto.setStatus(StatusPedido.AGUARDANDO_CONFIRMACAO_PAGAMENTO);
        
        pedidoGateway.alterarStatus(pedidoDto);

        notificaCliente(paramsDto.getPagamento().getPedido());
        
        return EfetuarPagamentoReturnDto.builder().pagamentoId(idPagamento).pagamentoExternoId(pagamentoExternoId).build();
	}

	private void notificaCliente(PedidoDto pedido) {
		List<String> destinarios = Arrays.asList(pedido.getCliente().getEmail(), pedido.getCliente().getTelefone());
		notificarGateway.notificar(NotificarDto.builder()
				.assunto("Status pedido: " + pedido.getId())
				.conteudo("O status do seu pedido é " + pedido.getStatus().name())
				.destinatarios(destinarios)
				.build());
	}
	
	//Método candidato a ser usecase 
	private String enviaPagamentoSistemaExterno(EfetuarPagamentoParamDto dto) {
		
		final String clienteNome = dto.getPagamento().getPedido().getCliente().getNome();
		final String clienteEmail = dto.getPagamento().getPedido().getCliente().getEmail();
		
		EnviaPagamentoExternoParamDto enviaPagamentoExternoParamDto = 
				EnviaPagamentoExternoParamDto.builder()
				.nomeProduto("Combo de lanches")
				.nomeCliente(clienteNome)
				.sobrenomeCliente("")
				.emailCliente(clienteEmail)
				.parcelas(1)
				.valor(dto.getPagamento().getValor())
				.modoPagamento(ModoPagamento.valueOf(dto.getPagamento().getFormaPagamento()))
				.build();
		
		EnviaPagamentoReturnDto responsePagamentoDto = plataformaPagamentoFactory.obter().enviarPagamento(enviaPagamentoExternoParamDto);
		
        dto.getPagamento().setPagamentoExternoId(responsePagamentoDto.getPagamentoExternoId());
        
        return responsePagamentoDto.getPagamentoExternoId();
	}

	
	private void validaCamposObrigatorios(PagamentoDto pagamentoDto) {
		final List<String>  mensagens = new ArrayList<>();
		if (pagamentoDto.getPedido() == null) {
			mensagens.add("Identificador do pedido (pedido id)");
		}

		if (pagamentoDto.getFormaPagamento() == null) {
			mensagens.add("Meio de pagamento não informado");
		}
		
		if (!mensagens.isEmpty()) {
			throw new CamposObrigatoriosNaoPreechidoException(mensagens.stream().reduce("", (a,b) -> a + ", " + b ));
		}
	}
}
