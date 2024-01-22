package br.com.pupposoft.fiap.sgr.config.di;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.ReflectionTestUtils.getField;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.pupposoft.fiap.sgr.pagamento.core.controller.PagamentoController;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.ClienteGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PagamentoGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PedidoGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PlataformaPagamentoConfigGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PlataformaPagamentoGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.AtualizarStatusPagamentoUseCase;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.EfetuarPagamentoUseCase;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.ObterPagamentoUsecase;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.PlataformaPagamentoFactory;

@ExtendWith(MockitoExtension.class)
class PagamentoDIConfigurationUnitTest {

	@InjectMocks
	private PagamentoDIConfiguration pagamentoDIConfiguration;
	
	@Mock
	private PedidoGateway pedidoGateway;
	
	@Mock
	private ClienteGateway clienteGateway;
	
	@Mock
	private PagamentoGateway pagamentoGateway;
	
	@Mock
	private PlataformaPagamentoConfigGateway plataformaPagamentoConfigGateway;
	
	@Mock
	private List<PlataformaPagamentoGateway> plataformaPagamentoGatewayList;
	
	@Test
	void plataformaPagamentoFactory() {
		PlataformaPagamentoFactory plataformaPagamentoFactory = pagamentoDIConfiguration.plataformaPagamentoFactory();
		assertEquals(plataformaPagamentoConfigGateway, getField(plataformaPagamentoFactory, "plataformaPagamentoConfigGateway"));
		assertEquals(plataformaPagamentoGatewayList, getField(plataformaPagamentoFactory, "plataformaPagamentoGatewayList"));
	}
	
	@Test
	void efetuarPagamentoUseCase() {
		PlataformaPagamentoFactory plataformaPagamentoFactory = Mockito.mock(PlataformaPagamentoFactory.class);
		
		EfetuarPagamentoUseCase efetuarPagamentoUseCase = pagamentoDIConfiguration.efetuarPagamentoUseCase(plataformaPagamentoFactory);
		assertEquals(pedidoGateway, getField(efetuarPagamentoUseCase, "pedidoGateway"));
		assertEquals(plataformaPagamentoFactory, getField(efetuarPagamentoUseCase, "plataformaPagamentoFactory"));
		assertEquals(clienteGateway, getField(efetuarPagamentoUseCase, "clienteGateway"));
	}
	
	@Test
	void confirmarPagamentoUseCase() {
		PlataformaPagamentoFactory plataformaPagamentoFactory = Mockito.mock(PlataformaPagamentoFactory.class);
		
		AtualizarStatusPagamentoUseCase confirmarPagamentoUseCase = pagamentoDIConfiguration.confirmarPagamentoUseCase(plataformaPagamentoFactory);
		assertEquals(pedidoGateway, getField(confirmarPagamentoUseCase, "pedidoGateway"));
		assertEquals(plataformaPagamentoFactory, getField(confirmarPagamentoUseCase, "plataformaPagamentoFactory"));
		assertEquals(pagamentoGateway, getField(confirmarPagamentoUseCase, "pagamentoGateway"));
	}
	
	@Test
	void obterPagamentoUsecase() {
		ObterPagamentoUsecase obterPagamentoUsecase = pagamentoDIConfiguration.obterPagamentoUsecase();
		assertEquals(pagamentoGateway, getField(obterPagamentoUsecase, "pagamentoGateway"));
	}
	
	@Test
	void pagamentoController() {
		EfetuarPagamentoUseCase efetuarPagamentoUseCase = Mockito.mock(EfetuarPagamentoUseCase.class); 
		AtualizarStatusPagamentoUseCase confirmarPagamentoUseCase = Mockito.mock(AtualizarStatusPagamentoUseCase.class);
		ObterPagamentoUsecase obterPagamentoUsecase = Mockito.mock(ObterPagamentoUsecase.class);
		
		PagamentoController pagamentoController = pagamentoDIConfiguration.pagamentoController(efetuarPagamentoUseCase, confirmarPagamentoUseCase, obterPagamentoUsecase);
		assertEquals(efetuarPagamentoUseCase, getField(pagamentoController, "efetuarPagamentoUseCase"));
		assertEquals(confirmarPagamentoUseCase, getField(pagamentoController, "atualizarStatusPagamentoUseCase"));
		assertEquals(obterPagamentoUsecase, getField(pagamentoController, "obterPagamentoUseCase"));
	}
}
