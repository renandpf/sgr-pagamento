package br.com.pupposoft.fiap.sgr.config.di;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import br.com.pupposoft.fiap.sgr.pagamento.core.controller.PagamentoController;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.ClienteGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PagamentoGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PedidoGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PlataformaPagamentoConfigGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PlataformaPagamentoGateway;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.AtualizarPedidoUseCaseImpl;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.AtualizarStatusPagamentoUseCase;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.EfetuarPagamentoUseCase;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.EfetuarPagamentoUseCaseImpl;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.ObterPagamentoUsecase;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.ObterPagamentoUsecaseImpl;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.PlataformaPagamentoFactory;
import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class PagamentoDIConfiguration {

	private PedidoGateway pedidoGateway;
	
	private ClienteGateway clienteGateway;
	
	private PagamentoGateway pagamentoGateway;
	
	private PlataformaPagamentoConfigGateway plataformaPagamentoConfigGateway;
	
	private List<PlataformaPagamentoGateway> plataformaPagamentoGatewayList;

	@Bean
	public PlataformaPagamentoFactory plataformaPagamentoFactory() {
		return new PlataformaPagamentoFactory(plataformaPagamentoConfigGateway, plataformaPagamentoGatewayList);
	}
	
	@Bean
	@Autowired
	@DependsOn("plataformaPagamentoFactory")
	public EfetuarPagamentoUseCase efetuarPagamentoUseCase(PlataformaPagamentoFactory plataformaPagamentoFactory) {
		return new EfetuarPagamentoUseCaseImpl(pedidoGateway, plataformaPagamentoFactory, pagamentoGateway, clienteGateway);
	}

	@Bean
	@Autowired
	@DependsOn("plataformaPagamentoFactory")
	public AtualizarStatusPagamentoUseCase confirmarPagamentoUseCase(PlataformaPagamentoFactory plataformaPagamentoFactory) {
		return new AtualizarPedidoUseCaseImpl(pedidoGateway, plataformaPagamentoFactory, pagamentoGateway);
	}
	
	@Bean
	public ObterPagamentoUsecase obterPagamentoUsecase() {
		return new ObterPagamentoUsecaseImpl(pagamentoGateway);
	}
	
	@Bean
	@Autowired
	@DependsOn({"efetuarPagamentoUseCase", "confirmarPagamentoUseCase", "obterPagamentoUsecase"})
	public PagamentoController pagamentoController(
			EfetuarPagamentoUseCase efetuarPagamentoUseCase, 
			AtualizarStatusPagamentoUseCase confirmarPagamentoUseCase, 
			ObterPagamentoUsecase obterPagamentoUsecase) {
		
		return new PagamentoController(efetuarPagamentoUseCase, confirmarPagamentoUseCase, obterPagamentoUsecase);
	}
}
