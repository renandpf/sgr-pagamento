package br.com.pupposoft.fiap.sgr.pagamento.core.controller;

import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomLong;
import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.PlataformaPagamento;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PagamentoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PedidoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EfetuarPagamentoParamDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EfetuarPagamentoReturnDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.AtualizarStatusPagamentoUseCase;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.EfetuarPagamentoUseCase;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.ObterPagamentoUsecase;

@ExtendWith(MockitoExtension.class)
class PagamentoControllerUnitTest {
	
	@Mock
    private EfetuarPagamentoUseCase efetuarPagamentoUseCase;
	
	@Mock
    private AtualizarStatusPagamentoUseCase atualizarStatusPagamentoUseCase;
	
	@Mock
    private ObterPagamentoUsecase obterPagamentoUseCase;
	
	@InjectMocks
	private PagamentoController pagamentoController;
	
	@Test
	void shouldSuccessOnEfetuar() {
		
		final Long pedidoId = getRandomLong();
		EfetuarPagamentoParamDto paramsDto = createParams(pedidoId);
		
		EfetuarPagamentoReturnDto returnDto = EfetuarPagamentoReturnDto.builder().build();
		doReturn(returnDto).when(efetuarPagamentoUseCase).efetuar(paramsDto);

		EfetuarPagamentoReturnDto returnDtoReturned = pagamentoController.efetuar(paramsDto);
		
		assertEquals(returnDto, returnDtoReturned);
		verify(efetuarPagamentoUseCase).efetuar(paramsDto);
	}
	
	@Test
	void shouldSuccessOnNotificacoes() {
		
		final PlataformaPagamento plataformaPagamento = PlataformaPagamento.MERCADO_PAGO; 
		final String identificadorPagamento = getRandomString();
		
		pagamentoController.notificacoes(plataformaPagamento, identificadorPagamento);
		verify(atualizarStatusPagamentoUseCase).atualizar(plataformaPagamento, identificadorPagamento);
	}
	
	@Test
	void shouldSuccessOnObterByIdentificadorPagamento() {
		final String identificadorPagamento = getRandomString();
		
		PagamentoDto dto = PagamentoDto.builder().build();
		doReturn(dto).when(obterPagamentoUseCase).obterPorIdentificadorPagamento(identificadorPagamento);
		
		PagamentoDto returnDto = pagamentoController.obterByIdentificadorPagamento(identificadorPagamento);
		
		assertEquals(dto, returnDto);
		
		verify(obterPagamentoUseCase).obterPorIdentificadorPagamento(identificadorPagamento);
	}
	
	private EfetuarPagamentoParamDto createParams(Long pedidoId) {

		PagamentoDto pagamentoDto = PagamentoDto.builder()
				.pedido(PedidoDto.builder().id(pedidoId).build())
				.formaPagamento("PIX")
				.build();
		EfetuarPagamentoParamDto paramsDto = EfetuarPagamentoParamDto.builder()
				.pagamento(pagamentoDto)
				.build();
		return paramsDto;
	}	
	
}
