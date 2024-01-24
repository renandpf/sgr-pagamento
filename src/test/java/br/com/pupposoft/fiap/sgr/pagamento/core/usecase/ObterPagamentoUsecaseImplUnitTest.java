package br.com.pupposoft.fiap.sgr.pagamento.core.usecase;

import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PagamentoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.PagamentoNaoEncontradoException;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PagamentoGateway;

@ExtendWith(MockitoExtension.class)
class ObterPagamentoUsecaseImplUnitTest {
	
	@Mock
	private PagamentoGateway pagamentoGateway;
	
	@InjectMocks
	private ObterPagamentoUsecase obterPagamentoUsecase = new ObterPagamentoUsecaseImpl(null);
	
	@Test
	void shouldSuccessOnObterPorIdentificadorPagamento() {
		
		final String identificadorPagamento = getRandomString();
		
		PagamentoDto pagamentoDto = PagamentoDto.builder().build();
		doReturn(Optional.of(pagamentoDto)).when(pagamentoGateway).obterPorIdentificadorPagamento(identificadorPagamento);
		
		PagamentoDto pagamentoDtoReturned = obterPagamentoUsecase.obterPorIdentificadorPagamento(identificadorPagamento);
		
		assertEquals(pagamentoDto, pagamentoDtoReturned);
		
		verify(pagamentoGateway).obterPorIdentificadorPagamento(identificadorPagamento);
	}
	
	@Test
	void shouldPagamentoNaoEncontradoExceptionOnObterPorIdentificadorPagamento() {
		final String identificadorPagamento = getRandomString();
		
		doReturn(Optional.empty()).when(pagamentoGateway).obterPorIdentificadorPagamento(identificadorPagamento);
		
		assertThrows(PagamentoNaoEncontradoException.class, () -> obterPagamentoUsecase.obterPorIdentificadorPagamento(identificadorPagamento));
		
		verify(pagamentoGateway).obterPorIdentificadorPagamento(identificadorPagamento);
	}
	
}
