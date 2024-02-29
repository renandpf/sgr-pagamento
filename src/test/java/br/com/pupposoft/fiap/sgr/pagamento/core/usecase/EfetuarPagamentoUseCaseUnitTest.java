package br.com.pupposoft.fiap.sgr.pagamento.core.usecase;

import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomDouble;
import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomLong;
import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomString;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.ModoPagamento;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.ClienteDto;
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
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PlataformaPagamentoGateway;

@ExtendWith(MockitoExtension.class)
class EfetuarPagamentoUseCaseUnitTest {
	
	@Mock
	private PedidoGateway pedidoGateway;
	
	@Mock
	private PlataformaPagamentoFactory plataformaPagamentoFactory;
	
	@Mock
	private PagamentoGateway pagamentoGateway;
	
	@Mock
	private NotificarGateway notificarGateway;
	
	@InjectMocks
	private EfetuarPagamentoUseCase efetuarPagamentoUseCase = 
		new EfetuarPagamentoUseCaseImpl(
				pedidoGateway, 
				plataformaPagamentoFactory, 
				pagamentoGateway, 
				notificarGateway);
	
	@Test
	void shouldSuccess() {
		
		final String pagamentoExternoId = getRandomString();
		
		ClienteDto clienteDto = ClienteDto.builder()
				.id(getRandomLong())
				.nome(getRandomString())
				.email(getRandomString())
				.telefone(getRandomString())
				.build();
		
		PedidoDto pedidoDto = PedidoDto.builder()
				.id(getRandomLong())
				.cliente(clienteDto)
				.status(StatusPedido.RECEBIDO)
				.valor(getRandomDouble())
				.build();
		
		EfetuarPagamentoParamDto paramsDto = EfetuarPagamentoParamDto.builder()
				.pagamento(PagamentoDto.builder()
						.pedido(pedidoDto)
						.valor(getRandomDouble())
						.formaPagamento("PIX")
						.build())
				.build();
		
		PlataformaPagamentoGateway plataformaPagamentoGatewayMock = Mockito.mock(PlataformaPagamentoGateway.class);
		doReturn(plataformaPagamentoGatewayMock).when(plataformaPagamentoFactory).obter();
		
		EnviaPagamentoReturnDto enviaPagamentoReturnDto = EnviaPagamentoReturnDto.builder().pagamentoExternoId(pagamentoExternoId).build();
		doReturn(enviaPagamentoReturnDto).when(plataformaPagamentoGatewayMock).enviarPagamento(any(EnviaPagamentoExternoParamDto.class));
		
		final Long pagamentoId = getRandomLong();
		doReturn(pagamentoId).when(pagamentoGateway).criar(paramsDto.getPagamento());
		
		EfetuarPagamentoReturnDto returnDto = efetuarPagamentoUseCase.efetuar(paramsDto);
		
		assertEquals(pagamentoId, returnDto.getPagamentoId());
		assertEquals(pagamentoExternoId, returnDto.getPagamentoExternoId());
		
		ArgumentCaptor<EnviaPagamentoExternoParamDto> enviaPagamentoExternoParamDtoAC = ArgumentCaptor.forClass(EnviaPagamentoExternoParamDto.class); 
		verify(plataformaPagamentoGatewayMock).enviarPagamento(enviaPagamentoExternoParamDtoAC.capture());
		
		EnviaPagamentoExternoParamDto enviaPagamentoExternoParamDtoSent = enviaPagamentoExternoParamDtoAC.getValue();

		assertEquals(ModoPagamento.PIX, enviaPagamentoExternoParamDtoSent.getModoPagamento());
		assertEquals(clienteDto.getEmail(), enviaPagamentoExternoParamDtoSent.getEmailCliente());
		assertEquals(clienteDto.getNome(), enviaPagamentoExternoParamDtoSent.getNomeCliente());
		assertEquals("", enviaPagamentoExternoParamDtoSent.getSobrenomeCliente());
		assertEquals("Combo de lanches", enviaPagamentoExternoParamDtoSent.getNomeProduto());
		assertEquals(1, enviaPagamentoExternoParamDtoSent.getParcelas());
		assertEquals(paramsDto.getPagamento().getValor(), enviaPagamentoExternoParamDtoSent.getValor());

		verify(pagamentoGateway).criar(paramsDto.getPagamento());
		
		ArgumentCaptor<NotificarDto> notificarDtoAC = ArgumentCaptor.forClass(NotificarDto.class);
		verify(notificarGateway).notificar(notificarDtoAC.capture());
		
		NotificarDto notificarDto = notificarDtoAC.getValue();
		
		assertEquals("Status pedido: " + pedidoDto.getId(), notificarDto.getAssunto());
		assertEquals("O status do seu pedido é " + StatusPedido.AGUARDANDO_CONFIRMACAO_PAGAMENTO.name(), notificarDto.getConteudo());
		assertTrue(notificarDto.getDestinatarios().contains(clienteDto.getEmail()));
		assertTrue(notificarDto.getDestinatarios().contains(clienteDto.getTelefone()));
	}

	@Test
	void shouldCamposObrigatoriosNaoPreechidoException() {
		EfetuarPagamentoParamDto paramsDto = EfetuarPagamentoParamDto.builder().pagamento(PagamentoDto.builder().build()).build();
		assertThrows(CamposObrigatoriosNaoPreechidoException.class, () -> efetuarPagamentoUseCase.efetuar(paramsDto));
	}
}
