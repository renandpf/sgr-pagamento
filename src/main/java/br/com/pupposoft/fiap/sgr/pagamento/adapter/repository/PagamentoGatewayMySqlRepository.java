package br.com.pupposoft.fiap.sgr.pagamento.adapter.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.ClienteEntity;
import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.PagamentoEntity;
import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.PedidoEntity;
import br.com.pupposoft.fiap.sgr.config.database.pagamento.repository.PagamentoEntityRepository;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.ClienteDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PagamentoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PedidoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.ErrorToAccessRepositoryException;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PagamentoGateway;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@AllArgsConstructor
public class PagamentoGatewayMySqlRepository implements PagamentoGateway {

	private PagamentoEntityRepository pagamentoRepository;

	@Override
	public Long criar(PagamentoDto pagamentoDto) {
		try {
			
			ClienteEntity clienteEntity = ClienteEntity.builder()
					.clienteId(pagamentoDto.getPedido().getCliente().getId())
					.nome(pagamentoDto.getPedido().getCliente().getNome())
					.email(pagamentoDto.getPedido().getCliente().getEmail())
					.telefone(pagamentoDto.getPedido().getCliente().getTelefone())
					.build();
			
			PedidoEntity pedidoEntity = PedidoEntity.builder()
					.id(pagamentoDto.getPedido().getId())
					.valor(pagamentoDto.getPedido().getValor())
					.cliente(clienteEntity)
					.build();
			
			PagamentoEntity pagamentoEntity = PagamentoEntity.builder()
					.identificadorPagamentoExterno(pagamentoDto.getPagamentoExternoId())
					.valor(pagamentoDto.getValor())
					.pedido(pedidoEntity)
					.build();

			PagamentoEntity pagamentoEntityCreated = pagamentoRepository.save(pagamentoEntity);
			return pagamentoEntityCreated.getId();
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ErrorToAccessRepositoryException();
		}
	}

	@Override
	public Optional<PagamentoDto> obterPorIdentificadorPagamento(String pagamentoExternoId) {
		try {

			Optional<PagamentoEntity> pagamentoEntityOp = pagamentoRepository.findByIdentificadorPagamentoExterno(pagamentoExternoId);

			Optional<PagamentoDto> pagamentoDtoOp = Optional.empty();
			if(pagamentoEntityOp.isPresent()) {
				PagamentoEntity pagamentoEntity = pagamentoEntityOp.get();
				PagamentoDto pagamentoDto = mapEntityToDto(pagamentoExternoId, pagamentoEntity);
				pagamentoDtoOp = Optional.of(pagamentoDto);
			}

			return pagamentoDtoOp;
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ErrorToAccessRepositoryException();
		}	
	}

	private PagamentoDto mapEntityToDto(String pagamentoExternoId, PagamentoEntity pagamentoEntity) {
		
		PedidoEntity pedidoEntity = pagamentoEntity.getPedido();
		ClienteEntity clienteEntity = pedidoEntity.getCliente();
		
		ClienteDto clienteDto = ClienteDto.builder()
				.id(clienteEntity.getId())
				.nome(clienteEntity.getNome())
				.email(clienteEntity.getEmail())
				.telefone(clienteEntity.getTelefone())
				.build();
		
		PedidoDto pedidoDto = PedidoDto.builder()
			.id(pedidoEntity.getId())
			.valor(pedidoEntity.getValor())
			.cliente(clienteDto)
		.build();
		
		return PagamentoDto.builder()
				.id(pagamentoEntity.getId())
				.pagamentoExternoId(pagamentoExternoId)
				.pedido(pedidoDto)
				.build();
	}

}
