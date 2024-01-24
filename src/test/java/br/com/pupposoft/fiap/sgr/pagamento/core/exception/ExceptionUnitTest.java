package br.com.pupposoft.fiap.sgr.pagamento.core.exception;

import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import br.com.pupposoft.fiap.starter.exception.SystemBaseException;

class ExceptionUnitTest {

	@Test
	void camposObrigatoriosNaoPreechidoException() {
		final String message = getRandomString();
		final SystemBaseException exception = new CamposObrigatoriosNaoPreechidoException(message);
		
		assertEquals("sgr.camposObrigatoriosNaoPreenchido", exception.getCode());
		assertEquals(message, exception.getMessage());
		assertEquals(400, exception.getHttpStatus());
	}
	
	@Test
	void clienteNaoEncontradoException() {
		final SystemBaseException exception = new ClienteNaoEncontradoException();
		
		assertEquals("sgr.clienteNotFound", exception.getCode());
		assertEquals("Cliente não foi encontrado", exception.getMessage());
		assertEquals(404, exception.getHttpStatus());
	}
	
	@Test
	void errorToAccessPagamentoServicoExternoException() {
		final SystemBaseException exception = new ErrorToAccessPagamentoServicoExternoException();
		
		assertEquals("sgr.erroAoAcessarSistemaPagamentoExterno", exception.getCode());
		assertEquals("Erro ao acessar sistema de pagamento externo", exception.getMessage());
		assertEquals(500, exception.getHttpStatus());
	}
	
	@Test
	void plataformaPagamentoGatewayNotFoundException() {
		final SystemBaseException exception = new PlataformaPagamentoGatewayNotFoundException();
		
		assertEquals("sgr.plataformaPagamentoGatewayNotFound", exception.getCode());
		assertEquals("Plataforma de pagamento não encontrada. Verifique a configuração do sistema", exception.getMessage());
		assertEquals(500, exception.getHttpStatus());
	}
	
	@Test
	void errorToAccessPedidoServiceException() {
		final SystemBaseException exception = new ErrorToAccessPedidoServiceException();
		
		assertEquals("sgr.errorToAccessPedidoService", exception.getCode());
		assertEquals("Erro ao acessar pedido service", exception.getMessage());
		assertEquals(500, exception.getHttpStatus());
	}
	
	@Test
	void pedidoNaoEncontradoException() {
		final SystemBaseException exception = new PedidoNaoEncontradoException();
		
		assertEquals("sgr.pedidoNotFound", exception.getCode());
		assertEquals("Pedido não foi encontrado", exception.getMessage());
		assertEquals(404, exception.getHttpStatus());
	}
	
	@Test
	void errorToAccessRepositoryException() {
		final SystemBaseException exception = new ErrorToAccessRepositoryException();
		
		assertEquals("sgr.errorToAccessRepository", exception.getCode());
		assertEquals("Erro ao acessar repositório de dados", exception.getMessage());
		assertEquals(500, exception.getHttpStatus());
	}
	
	@Test
	void pagamentoNaoEncontradoException() {
		final SystemBaseException exception = new PagamentoNaoEncontradoException();
		
		assertEquals("sgr.pagamentoNaoEncontrado", exception.getCode());
		assertEquals("Pagamento não encontrado", exception.getMessage());
		assertEquals(404, exception.getHttpStatus());
	}
	
}
