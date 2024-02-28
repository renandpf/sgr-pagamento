package br.com.pupposoft.fiap.sgr.pagamento.core.usecase;

import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomLong;
import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomString;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.PlataformaPagamento;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.ClienteDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.NotificarDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PagamentoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PedidoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.PagamentoNaoEncontradoException;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.PedidoNaoEncontradoException;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.ClienteGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.NotificarGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PagamentoGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PedidoGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PlataformaPagamentoGateway;

@ExtendWith(MockitoExtension.class)
class AtualizarPedidoUseCaseImplUnitTest {
	
	@InjectMocks
	private AtualizarStatusPagamentoUseCase atualizarStatusPagamentoUseCase = new AtualizarPedidoUseCaseImpl(null, null, null, null, null);
	
	@Mock
	private PedidoGateway pedidoGateway;
	
	@Mock
	private ClienteGateway clienteGateway;
	
	@Mock
	private PlataformaPagamentoFactory plataformaPagamentoFactory;
	
	@Mock
	private PagamentoGateway pagamentoGateway;
	
	@Mock
	private NotificarGateway notificarGateway;
	
	@Test
	void shouldSuccessOnAtualizar() {
		final PlataformaPagamento plataformaPagamento = PlataformaPagamento.MERCADO_PAGO; 
		final String identificadorPagamento = getRandomString();
		
		PagamentoDto pagamentoDto = PagamentoDto.builder().pedido(PedidoDto.builder().id(getRandomLong()).build()).build();
		doReturn(Optional.of(pagamentoDto)).when(pagamentoGateway).obterPorIdentificadorPagamento(identificadorPagamento);
		
		PedidoDto pedidoDto = PedidoDto.builder().id(getRandomLong()).clienteId(getRandomLong()).build();
		doReturn(Optional.of(pedidoDto)).when(pedidoGateway).obterPorId(pagamentoDto.getPedido().getId());
		
		ClienteDto clienteDto = ClienteDto.builder().build();
		doReturn(Optional.of(clienteDto)).when(clienteGateway).obterPorId(pedidoDto.getClienteId());
		
		StatusPedido newStatus = StatusPedido.AGUARDANDO_CONFIRMACAO_PAGAMENTO;
		PlataformaPagamentoGateway plataformaPagamentoGatewayMock = Mockito.mock(PlataformaPagamentoGateway.class);
		doReturn(newStatus).when(plataformaPagamentoGatewayMock).obtemStatus(identificadorPagamento);
		doReturn(plataformaPagamentoGatewayMock).when(plataformaPagamentoFactory).obter(plataformaPagamento);
		
		atualizarStatusPagamentoUseCase.atualizar(plataformaPagamento, identificadorPagamento);
		
		verify(pagamentoGateway).obterPorIdentificadorPagamento(identificadorPagamento);
		verify(pedidoGateway).obterPorId(pagamentoDto.getPedido().getId());
		verify(clienteGateway).obterPorId(pedidoDto.getClienteId());
		
		ArgumentCaptor<PedidoDto> pedidoDtoAC = ArgumentCaptor.forClass(PedidoDto.class);
		verify(pedidoGateway).alterarStatus(pedidoDtoAC.capture());
		
		PedidoDto pedidoDtoCaptured = pedidoDtoAC.getValue();
		assertEquals(pedidoDto.getId(), pedidoDtoCaptured.getId());
		assertEquals(newStatus, pedidoDtoCaptured.getStatus());
		
		ArgumentCaptor<NotificarDto> notificarDtoAC = ArgumentCaptor.forClass(NotificarDto.class);
		verify(notificarGateway, times(2)).notificar(notificarDtoAC.capture());
		
		NotificarDto notificarClienteDto = notificarDtoAC.getAllValues().get(0);
		assertEquals("Status pedido: " + pedidoDto.getId(), notificarClienteDto.getAssunto());
		assertEquals("O status do seu pedido é " + newStatus.name(), notificarClienteDto.getConteudo());
		assertTrue(notificarClienteDto.getDestinatarios().contains(clienteDto.getEmail()));
		assertTrue(notificarClienteDto.getDestinatarios().contains(clienteDto.getTelefone()));
		
		NotificarDto notificarCozinhaDto = notificarDtoAC.getAllValues().get(1);
		assertEquals("Novo pedido: " + pedidoDto.getId(), notificarCozinhaDto.getAssunto());
		assertEquals("Verifique detalhes do pedido pela plataforma", notificarCozinhaDto.getConteudo());
		assertTrue(notificarCozinhaDto.getDestinatarios().contains("mock telefone cozinha"));
	}
	
	@Test
	void shouldPagamentoNaoEncontradoExceptionOnAtualizar() {
		final PlataformaPagamento plataformaPagamento = PlataformaPagamento.MERCADO_PAGO; 
		final String identificadorPagamento = getRandomString();
		
		doReturn(Optional.empty()).when(pagamentoGateway).obterPorIdentificadorPagamento(identificadorPagamento);
		
		assertThrows(PagamentoNaoEncontradoException.class, () -> atualizarStatusPagamentoUseCase.atualizar(plataformaPagamento, identificadorPagamento));
		
		verify(pagamentoGateway).obterPorIdentificadorPagamento(identificadorPagamento);
		verify(pedidoGateway, never()).obterPorId(any(Long.class));
		verify(plataformaPagamentoFactory, never()).obter(any(PlataformaPagamento.class));
		verify(pedidoGateway, never()).alterarStatus(any(PedidoDto.class));
	}
	
	@Test
	void shouldPedidoNaoEncontradoExceptionOnAtualizar() {
		final PlataformaPagamento plataformaPagamento = PlataformaPagamento.MERCADO_PAGO; 
		final String identificadorPagamento = getRandomString();
		
		PagamentoDto pagamentoDto = PagamentoDto.builder().pedido(PedidoDto.builder().id(getRandomLong()).build()).build();
		doReturn(Optional.of(pagamentoDto)).when(pagamentoGateway).obterPorIdentificadorPagamento(identificadorPagamento);
		
		doReturn(Optional.empty()).when(pedidoGateway).obterPorId(pagamentoDto.getPedido().getId());
		
		assertThrows(PedidoNaoEncontradoException.class, () -> atualizarStatusPagamentoUseCase.atualizar(plataformaPagamento, identificadorPagamento));
		
		verify(pagamentoGateway).obterPorIdentificadorPagamento(identificadorPagamento);
		verify(pedidoGateway).obterPorId(pagamentoDto.getPedido().getId());
		verify(plataformaPagamentoFactory, never()).obter(any(PlataformaPagamento.class));
		verify(pedidoGateway, never()).alterarStatus(any(PedidoDto.class));
	}
	
}
