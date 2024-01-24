package br.com.pupposoft.fiap.sgr.pagamento.adapter.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pupposoft.fiap.sgr.pagamento.adapter.web.json.NotificacaoPagamentoJson;
import br.com.pupposoft.fiap.sgr.pagamento.adapter.web.json.PagamentoJson;
import br.com.pupposoft.fiap.sgr.pagamento.core.controller.PagamentoController;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.PlataformaPagamento;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PagamentoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PedidoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EfetuarPagamentoParamDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EfetuarPagamentoReturnDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.PagamentoNaoEncontradoException;

import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.*;

@WebMvcTest(PagamentoApiController.class)
class PagamentoApiControllerIntTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PagamentoController pagamentoController;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Test
	void shouldSucessOnEfetuar() throws Exception {
		
		final PagamentoJson requestBody = PagamentoJson.builder()
				.id(getRandomLong())
				.pedidoId(getRandomLong())
				.pagamentoExternoId(getRandomString())
				.forma(getRandomString())
				.build();
		
		EfetuarPagamentoReturnDto returnDto = EfetuarPagamentoReturnDto.builder()
				.pagamentoId(10L)
				.pagamentoExternoId("111222")
				.build();
		doReturn(returnDto).when(pagamentoController).efetuar(any(EfetuarPagamentoParamDto.class));
		
		this.mockMvc.perform(
				post("/sgr/pagamentos/efetuar")
				.content(objectMapper.writeValueAsString(requestBody))
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isCreated())
		.andExpect(content().json("{\"id\":10,\"pagamentoExternoId\":\"111222\",\"pedidoId\":null,\"forma\":null}"));

		ArgumentCaptor<EfetuarPagamentoParamDto> efetuarPagamentoParamDtoAC = ArgumentCaptor.forClass(EfetuarPagamentoParamDto.class);
		
		verify(pagamentoController).efetuar(efetuarPagamentoParamDtoAC.capture());
		
		EfetuarPagamentoParamDto efetuarPagamentoParamDto = efetuarPagamentoParamDtoAC.getValue();
		
		assertEquals(requestBody.getId(), efetuarPagamentoParamDto.getPagamento().getId());
		assertEquals(requestBody.getPedidoId(), efetuarPagamentoParamDto.getPagamento().getPedido().getId());
		assertEquals(requestBody.getPagamentoExternoId(), efetuarPagamentoParamDto.getPagamento().getPagamentoExternoId());
		assertEquals(requestBody.getForma(), efetuarPagamentoParamDto.getPagamento().getFormaPagamento());
	}
	
	@Test
	void shouldSucessMocknotificacoes() throws Exception {
		final String plataformaPagamento = "ANY";//Qualquer um diferente de MERCADO_PAGO ou PAG_SEGURO
		final NotificacaoPagamentoJson requestBody = new NotificacaoPagamentoJson();
		setField(requestBody, "identificador", getRandomString());
		
		this.mockMvc.perform(
				post("/sgr/pagamentos/notificacoes/" + plataformaPagamento)
				.content(objectMapper.writeValueAsString(requestBody))
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().string(""));
		
		verify(pagamentoController).notificacoes(PlataformaPagamento.MOCK, requestBody.getIdentificador());
	}
	
	@Test
	void shouldSucessMercadoPagoNotificacoes() throws Exception {
		final String plataformaPagamento = "mercado-pago";
		final NotificacaoPagamentoJson requestBody = new NotificacaoPagamentoJson();
		setField(requestBody, "identificador", getRandomString());
		
		this.mockMvc.perform(
				post("/sgr/pagamentos/notificacoes/" + plataformaPagamento)
				.content(objectMapper.writeValueAsString(requestBody))
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().string(""));
		
		verify(pagamentoController).notificacoes(PlataformaPagamento.MERCADO_PAGO, requestBody.getIdentificador());
	}
	
	@Test
	void shouldSucessPagSeguroNotificacoes() throws Exception {
		final String plataformaPagamento = "pag-seguro";
		final NotificacaoPagamentoJson requestBody = new NotificacaoPagamentoJson();
		setField(requestBody, "identificador", getRandomString());
		
		this.mockMvc.perform(
				post("/sgr/pagamentos/notificacoes/" + plataformaPagamento)
				.content(objectMapper.writeValueAsString(requestBody))
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().string(""));
		
		verify(pagamentoController).notificacoes(PlataformaPagamento.PAG_SEGURO, requestBody.getIdentificador());
	}
	
	@Test
	void shouldSucessObterByIdentificadorPagamento() throws Exception {
		final String identificadorPagamento = getRandomString();
		
		PagamentoDto pagamentoDto = PagamentoDto.builder()
				.id(1L)
				.pagamentoExternoId("aaa")
				.pedido(PedidoDto.builder().id(2L).build())
				.build();
		doReturn(pagamentoDto).when(pagamentoController).obterByIdentificadorPagamento(identificadorPagamento);
		
		this.mockMvc.perform(
				get("/sgr/pagamentos/identificador-pagamento-externo/" + identificadorPagamento))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().json("{\"id\":1,\"pagamentoExternoId\":\"aaa\",\"pedidoId\":2,\"forma\":null}"));
		
		verify(pagamentoController).obterByIdentificadorPagamento(identificadorPagamento);
	}
	
	@Test
	void shouldBussinesError() throws Exception {
		final String identificadorPagamento = getRandomString();
		

		doThrow(new PagamentoNaoEncontradoException()) .when(pagamentoController).obterByIdentificadorPagamento(identificadorPagamento);
		
		this.mockMvc.perform(
				get("/sgr/pagamentos/identificador-pagamento-externo/" + identificadorPagamento))
		.andDo(print())
		.andExpect(status().isNotFound())
		.andExpect(content().json("{\"code\":\"sgr.pagamentoNaoEncontrado\",\"message\":\"Pagamento não encontrado\"}"));
		
		verify(pagamentoController).obterByIdentificadorPagamento(identificadorPagamento);
	}

	@Test
	void shouldUnexpectedError() throws Exception {
		final String identificadorPagamento = getRandomString();
		
		doThrow(new RuntimeException()).when(pagamentoController).obterByIdentificadorPagamento(identificadorPagamento);
		
		this.mockMvc.perform(
				get("/sgr/pagamentos/identificador-pagamento-externo/" + identificadorPagamento))
		.andDo(print())
		.andExpect(status().isInternalServerError())
		.andExpect(content().json("{\"code\":\"INTERNAL_SERVER_ERROR\",\"message\":\"Internal Server Error\"}"));
		
		verify(pagamentoController).obterByIdentificadorPagamento(identificadorPagamento);
	}
	
}
