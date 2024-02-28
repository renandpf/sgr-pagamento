package br.com.pupposoft.fiap.sgr.pagamento.core.usecase;

import java.util.Arrays;
import java.util.Optional;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.Pedido;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.PlataformaPagamento;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.ClienteDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.NotificarDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PagamentoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PedidoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.ClienteNaoEncontradoException;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.PagamentoNaoEncontradoException;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.PedidoNaoEncontradoException;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.ClienteGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.NotificarGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PagamentoGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PedidoGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class AtualizarPedidoUseCaseImpl implements AtualizarStatusPagamentoUseCase {
	
	private PedidoGateway pedidoGateway;
	
	private ClienteGateway clienteGateway;
	
	private PlataformaPagamentoFactory plataformaPagamentoFactory;
	
	private PagamentoGateway pagamentoGateway;
	
	private NotificarGateway notificarGateway;
	
	@Override
    public void atualizar(PlataformaPagamento plataformaPagamento, String identificadorPagamento) {
        PagamentoDto pagamentoDto = obtemPagamentoPorIdentificadorPagamento(identificadorPagamento);
        PedidoDto pedidoDto = getPedidoById(pagamentoDto.getPedido().getId());
        ClienteDto clienteDto = getClienteById(pedidoDto.getClienteId());
        
        
        StatusPedido newStatus = plataformaPagamentoFactory.obter(plataformaPagamento).obtemStatus(identificadorPagamento);
        
        Pedido pedido = Pedido.builder().id(pedidoDto.getId()).status(pedidoDto.getStatus()).build();
        pedido.setStatus(newStatus);//Deve chamar um endpoint do pedido-service para validar a mudança de status

        PedidoDto pedidoDtoStatusPago = PedidoDto.builder()
        		.id(pedido.getId())
        		.status(newStatus)
        		.build();

        pedidoGateway.alterarStatus(pedidoDtoStatusPago);
        
        NotificarDto notificarClienteDto = NotificarDto.builder()
        		.assunto("Status pedido: " + pedidoDto.getId())
        		.conteudo("O status do seu pedido é " + newStatus.name())
        		.destinatarios(Arrays.asList(clienteDto.getEmail(), clienteDto.getTelefone()))
        		.build();
        
        notificarGateway.notificar(notificarClienteDto);
        
        NotificarDto notificarCozinhaDto = NotificarDto.builder()
        		.assunto("Novo pedido: " + pedidoDto.getId())
        		.conteudo("Verifique detalhes do pedido pela plataforma")
        		.destinatarios(Arrays.asList("mock telefone cozinha"))
        		.build();
        
        notificarGateway.notificar(notificarCozinhaDto);
    }


	private PedidoDto getPedidoById(Long pedidoId) {
        Optional<PedidoDto> pedidoDtoOp = pedidoGateway.obterPorId(pedidoId);
        if(pedidoDtoOp.isEmpty()) {
        	log.warn("Pedido não encontrado. pedidoId={}", pedidoId);
        	throw new PedidoNaoEncontradoException();
        }
        
        return pedidoDtoOp.get();
	}
	
	private ClienteDto getClienteById(Long clienteId) {
		Optional<ClienteDto> clienteDtoOp = clienteGateway.obterPorId(clienteId);
		if(clienteDtoOp.isEmpty()) {
			log.warn("Cliente não encontrado. pedidoId={}", clienteId);
			throw new ClienteNaoEncontradoException();
		}
		
		return clienteDtoOp.get();
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
