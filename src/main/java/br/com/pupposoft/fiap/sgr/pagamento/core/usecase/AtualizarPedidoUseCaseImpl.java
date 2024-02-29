package br.com.pupposoft.fiap.sgr.pagamento.core.usecase;

import java.util.Arrays;
import java.util.Optional;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.Pedido;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.PlataformaPagamento;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.NotificarDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PagamentoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PedidoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.PagamentoNaoEncontradoException;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.NotificarGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PagamentoGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PedidoGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class AtualizarPedidoUseCaseImpl implements AtualizarStatusPagamentoUseCase {
	
	private PedidoGateway pedidoGateway;
	
	private PlataformaPagamentoFactory plataformaPagamentoFactory;
	
	private PagamentoGateway pagamentoGateway;
	
	private NotificarGateway notificarGateway;
	
	@Override
    public void atualizar(PlataformaPagamento plataformaPagamento, String identificadorPagamento) {
		StatusPedido newStatusPedido = plataformaPagamentoFactory.obter(plataformaPagamento).obtemStatus(identificadorPagamento);
		
        PagamentoDto pagamentoDto = obtemPagamentoPorIdentificadorPagamento(identificadorPagamento);
        PedidoDto pedidoDto = pagamentoDto.getPedido();
        
        Pedido pedido = Pedido.builder().id(pedidoDto.getId()).status(pedidoDto.getStatus()).build();
        pedido.setStatus(newStatusPedido);

        PedidoDto pedidoDtoStatusPago = PedidoDto.builder()
        		.id(pedido.getId())
        		.status(newStatusPedido)
        		.build();

        pedidoGateway.alterarStatus(pedidoDtoStatusPago);
        
        notificaCliente(newStatusPedido, pedidoDto);
        
        notificaCozinha(pedidoDto);
    }


	private void notificaCozinha(PedidoDto pedidoDto) {
		NotificarDto notificarCozinhaDto = NotificarDto.builder()
        		.assunto("Novo pedido: " + pedidoDto.getId())
        		.conteudo("Verifique detalhes do pedido pela plataforma")
        		.destinatarios(Arrays.asList("mock telefone cozinha"))
        		.build();
        
        notificarGateway.notificar(notificarCozinhaDto);
	}


	private void notificaCliente(StatusPedido newStatusPedido, PedidoDto pedidoDto) {
		NotificarDto notificarClienteDto = NotificarDto.builder()
        		.assunto("Status pedido: " + pedidoDto.getId())
        		.conteudo("O status do seu pedido é " + newStatusPedido.name())
        		.destinatarios(Arrays.asList(pedidoDto.getCliente().getEmail(), pedidoDto.getCliente().getTelefone()))
        		.build();
        
        notificarGateway.notificar(notificarClienteDto);
	}


    private PagamentoDto obtemPagamentoPorIdentificadorPagamento(String identificadorPagamento) {
        Optional<PagamentoDto> pagamentoDtoOp = pagamentoGateway.obterPorIdentificadorPagamento(identificadorPagamento);
        if (pagamentoDtoOp.isEmpty()) {
            log.warn("Pagamento não encontrado. identificadorPagamento={}", identificadorPagamento);
            throw new PagamentoNaoEncontradoException();
        }

        return pagamentoDtoOp.get();
    }
	
}
