package br.com.pupposoft.fiap.sgr.pagamento.core.usecase;

import java.util.Optional;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.Pedido;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.PlataformaPagamento;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PagamentoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PedidoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.PagamentoNaoEncontradoException;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.PedidoNaoEncontradoException;
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
	
	@Override
    public void atualizar(PlataformaPagamento plataformaPagamento, String identificadorPagamento) {
        PagamentoDto pagamentoDto = obtemPagamentoPorIdentificadorPagamento(identificadorPagamento);
        PedidoDto pedidoDto = getPedidoById(pagamentoDto.getPedido().getId());
        
        StatusPedido newStatus = plataformaPagamentoFactory.obter(plataformaPagamento).obtemStatus(identificadorPagamento);
        
        Pedido pedido = Pedido.builder().id(pedidoDto.getId()).status(pedidoDto.getStatus()).build();
        pedido.setStatus(newStatus);//Deve chamar um endpoint do pedido-service para validar a mudança de status

        PedidoDto pedidoDtoStatusPago = PedidoDto.builder()
        		.id(pedido.getId())
        		.status(newStatus)
        		.build();

        this.pedidoGateway.alterarStatus(pedidoDtoStatusPago);
    }


	private PedidoDto getPedidoById(Long pedidoId) {
        Optional<PedidoDto> pedidoDtoOp = pedidoGateway.obterPorId(pedidoId);
        if(pedidoDtoOp.isEmpty()) {
        	log.warn("Pedido não encontrado. pedidoId={}", pedidoId);
        	throw new PedidoNaoEncontradoException();
        }
        
        return pedidoDtoOp.get();
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
