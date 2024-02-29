package br.com.pupposoft.fiap.sgr.pagamento.adapter.repository;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.ClienteEntity;
import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.PagamentoEntity;
import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.PedidoEntity;
import br.com.pupposoft.fiap.sgr.config.database.pagamento.repository.PagamentoEntityRepository;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.ClienteDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PagamentoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PedidoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.ErrorToAccessRepositoryException;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PagamentoGateway;
import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.*;

@ExtendWith(MockitoExtension.class)
class PagamentoGatewayMySqlRepositoryUnitTest {

	@InjectMocks
	private PagamentoGateway pagamentoGateway = new PagamentoGatewayMySqlRepository(null);
	
	@Mock
	private PagamentoEntityRepository pagamentoRepository;
	
	@Test
	void shouldSucessOncriar() {
		ClienteDto clienteDto = ClienteDto.builder()
				.id(getRandomLong())
				.nome(getRandomString())
				.email(getRandomString())
				.telefone(getRandomString())
				.build();
		
		PedidoDto pedidoDto = PedidoDto.builder().
				id(getRandomLong())
				.cliente(clienteDto)
				.valor(getRandomDouble())
				.build();
		
		PagamentoDto pagamentoDtoParam = PagamentoDto.builder()
				.pagamentoExternoId(getRandomString())
				.valor(getRandomDouble())
				.pedido(pedidoDto)
				.build();
		
		PagamentoEntity pagamentoEntitySaved = PagamentoEntity.builder()
				.id(getRandomLong())
				.build();
		doReturn(pagamentoEntitySaved).when(pagamentoRepository).save(any(PagamentoEntity.class));
		
		Long pagamentoId = pagamentoGateway.criar(pagamentoDtoParam);
		assertEquals(pagamentoEntitySaved.getId(), pagamentoId);
		
		ArgumentCaptor<PagamentoEntity> pagamentoEntityAC = ArgumentCaptor.forClass(PagamentoEntity.class);
		
		verify(pagamentoRepository).save(pagamentoEntityAC.capture());
		
		PagamentoEntity pagamentoEntityCaptured = pagamentoEntityAC.getValue();
		
		assertEquals(pagamentoDtoParam.getPagamentoExternoId() , pagamentoEntityCaptured.getIdentificadorPagamentoExterno());
		assertEquals(pagamentoDtoParam.getValor(), pagamentoEntityCaptured.getValor());
		assertEquals(pagamentoDtoParam.getPedido().getId(), pagamentoEntityCaptured.getPedido().getId());
		assertEquals(pagamentoDtoParam.getPedido().getValor(), pagamentoEntityCaptured.getPedido().getValor());
		assertEquals(pagamentoDtoParam.getPedido().getCliente().getId(), pagamentoEntityCaptured.getPedido().getCliente().getId());
		assertEquals(pagamentoDtoParam.getPedido().getCliente().getNome(), pagamentoEntityCaptured.getPedido().getCliente().getNome());
		assertEquals(pagamentoDtoParam.getPedido().getCliente().getTelefone(), pagamentoEntityCaptured.getPedido().getCliente().getTelefone());
		assertEquals(pagamentoDtoParam.getPedido().getCliente().getEmail(), pagamentoEntityCaptured.getPedido().getCliente().getEmail());
	}
	
	@Test
	void shouldErrorToAccessRepositoryExceptionOncriar() {
		
		ClienteDto clienteDto = ClienteDto.builder()
				.id(getRandomLong())
				.nome(getRandomString())
				.email(getRandomString())
				.telefone(getRandomString())
				.build();
		
		PedidoDto pedidoDto = PedidoDto.builder().
				id(getRandomLong())
				.cliente(clienteDto)
				.valor(getRandomDouble())
				.build();
		
		PagamentoDto pagamentoDtoParam = PagamentoDto.builder()
				.pagamentoExternoId(getRandomString())
				.valor(getRandomDouble())
				.pedido(pedidoDto)
				.build();
		
		doThrow(new RuntimeException()).when(pagamentoRepository).save(any(PagamentoEntity.class));
		
		assertThrows(ErrorToAccessRepositoryException.class, () -> pagamentoGateway.criar(pagamentoDtoParam)) ;
		verify(pagamentoRepository).save(any(PagamentoEntity.class));
	}

	@Test
	void shouldSucessOnObterPorIdentificadorPagamento() {
		
		final String pagamentoExternoIdParam = getRandomString();
		
		ClienteEntity clienteEntity = ClienteEntity.builder()
				.id(getRandomLong())
				.nome(getRandomString())
				.email(getRandomString())
				.telefone(getRandomString())
				.build();
		
		PedidoEntity pedidoEntity = PedidoEntity.builder()
				.id(getRandomLong())
				.valor(getRandomDouble())
				.cliente(clienteEntity)
				.build();
		
		PagamentoEntity pagamentoEntityExistent = PagamentoEntity.builder()
				.id(getRandomLong())
				.identificadorPagamentoExterno(pagamentoExternoIdParam)
				.valor(getRandomDouble())
				.pedido(pedidoEntity)
				.build();
		doReturn(Optional.of(pagamentoEntityExistent)).when(pagamentoRepository).findByIdentificadorPagamentoExterno(pagamentoExternoIdParam);
		
		Optional<PagamentoDto> pagamentoDtoOP = pagamentoGateway.obterPorIdentificadorPagamento(pagamentoExternoIdParam);
		assertTrue(pagamentoDtoOP.isPresent());
		
		PagamentoDto pagamentoDto = pagamentoDtoOP.get();
		
		assertEquals(pagamentoEntityExistent.getId() , pagamentoDto.getId());
		assertEquals(pagamentoExternoIdParam, pagamentoDto.getPagamentoExternoId());
		assertEquals(pagamentoEntityExistent.getPedido().getId() , pagamentoDto.getPedido().getId());
		assertEquals(pagamentoEntityExistent.getPedido().getValor() , pagamentoDto.getPedido().getValor());
		assertEquals(pagamentoEntityExistent.getPedido().getCliente().getId() , pagamentoDto.getPedido().getCliente().getId());
		assertEquals(pagamentoEntityExistent.getPedido().getCliente().getNome() , pagamentoDto.getPedido().getCliente().getNome());
		assertEquals(pagamentoEntityExistent.getPedido().getCliente().getEmail() , pagamentoDto.getPedido().getCliente().getEmail());
		assertEquals(pagamentoEntityExistent.getPedido().getCliente().getTelefone() , pagamentoDto.getPedido().getCliente().getTelefone());
		
		verify(pagamentoRepository).findByIdentificadorPagamentoExterno(pagamentoExternoIdParam);
	}
	
	@Test
	void shouldSucessPagamentoNotFoundOnObterPorIdentificadorPagamento() {
		
		final String pagamentoExternoIdParam = getRandomString();
		
		doReturn(Optional.empty()).when(pagamentoRepository).findByIdentificadorPagamentoExterno(pagamentoExternoIdParam);
		
		Optional<PagamentoDto> pagamentoDtoOP = pagamentoGateway.obterPorIdentificadorPagamento(pagamentoExternoIdParam);
		assertTrue(pagamentoDtoOP.isEmpty());
		
		verify(pagamentoRepository).findByIdentificadorPagamentoExterno(pagamentoExternoIdParam);
	}
	
	@Test
	void shouldErrorToAccessRepositoryExceptionOnObterPorIdentificadorPagamento() {
		final String pagamentoExternoIdParam = getRandomString();
		
		doThrow(new RuntimeException()).when(pagamentoRepository).findByIdentificadorPagamentoExterno(pagamentoExternoIdParam);
		
		assertThrows(ErrorToAccessRepositoryException.class, () -> pagamentoGateway.obterPorIdentificadorPagamento(pagamentoExternoIdParam));
		
		verify(pagamentoRepository).findByIdentificadorPagamentoExterno(pagamentoExternoIdParam);
	}
	
}
