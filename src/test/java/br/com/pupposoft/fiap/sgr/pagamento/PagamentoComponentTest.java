package br.com.pupposoft.fiap.sgr.pagamento;

import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomDouble;
import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomInteger;
import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomLong;
import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomString;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import br.com.pupposoft.fiap.SgrPagamentoService;
import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.ClienteEntity;
import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.PagamentoEntity;
import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.PedidoEntity;
import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.PlataformaPagamentoEntity;
import br.com.pupposoft.fiap.sgr.config.database.pagamento.repository.PlataformaPagamentoEntityRepository;
import br.com.pupposoft.fiap.sgr.pagamento.adapter.web.PagamentoApiController;
import br.com.pupposoft.fiap.sgr.pagamento.adapter.web.json.PagamentoJson;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PlataformaPagamentoGateway;
import br.com.pupposoft.fiap.starter.http.HttpConnect;
import br.com.pupposoft.fiap.test.databuilder.DataBuilderBase;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SgrPagamentoService.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@WireMockTest
@Disabled
class PagamentoComponentTest extends ComponentTestBase {

	@LocalServerPort
    private int randomServerPort;

	@Autowired
	private PagamentoApiController pagamentoApiController;
	
    @Autowired
    @Qualifier("plataformaPagamentoMercadoPagoGateway")
    PlataformaPagamentoGateway plataformaPagamentoGateway;
    
    @Autowired
    private PlataformaPagamentoEntityRepository plataformaPagamentoEntityRepository;
    
    @BeforeEach
    public void initEach(){
    	cleanAllDatabase();
    }

    @Test
	void efetuarWithMercadoPagoTest(WireMockRuntimeInfo wmRuntimeInfo) {
		
		final Integer idPagamentoExterno = getRandomInteger();
		final String responseBodyStr = "{\"id\": "+ idPagamentoExterno +"  }";
		final String accessToken = getRandomString();
		final String path = "/v1/payments";
		final long pedidoId = DataBuilderBase.getRandomLong();
		
		final String pedidoResponseBodyStr = "{\"id\": "+ pedidoId+" }";
		stubFor(get("/sgr/pedidos/" + pedidoId).willReturn(okJson(pedidoResponseBodyStr)));
		stubFor(post(path).willReturn(okJson(responseBodyStr)));

		setField(plataformaPagamentoGateway, "baseUrl", wmRuntimeInfo.getHttpBaseUrl());
		setField(plataformaPagamentoGateway, "accessToken", accessToken);
		setField(plataformaPagamentoGateway, "httpConnectGateway", new HttpConnect());
		setField(plataformaPagamentoGateway, "objectMapper", new ObjectMapper());
		
		createPlataformPagamentoMP();
		
		PagamentoJson pagamentoJsonRequest = PagamentoJson.builder().pedidoId(pedidoId).forma("PIX").build();
		PagamentoJson pagamentoJsonResponse = pagamentoApiController.efetuar(pagamentoJsonRequest);
		
		assertEquals(idPagamentoExterno+"", pagamentoJsonResponse.getPagamentoExternoId());

		List<PagamentoEntity> allPaymentsfindAll = pagamentoEntityRepository.findAll();
		assertFalse(allPaymentsfindAll.isEmpty());
		assertEquals(idPagamentoExterno+"", allPaymentsfindAll.get(0).getIdentificadorPagamentoExterno());
	}

	private void createPlataformPagamentoMP() {
		PlataformaPagamentoEntity plataformaPagamentoEntity = PlataformaPagamentoEntity.builder()
				.id(getRandomLong())
				.code("MERCADO_PAGO")
				.status(0L)
				.build();
		plataformaPagamentoEntityRepository.save(plataformaPagamentoEntity);
	}
	
	@Test
	void obterByIdentificadorPagamentoTest() {
		
		
		PagamentoEntity pagamentoEntity = createObterByIdentificadorPagamentoData();
		
		PagamentoJson pagamentoJson = pagamentoApiController.obterByIdentificadorPagamento(pagamentoEntity.getIdentificadorPagamentoExterno());
		assertEquals(pagamentoEntity.getId(), pagamentoJson.getId());
	}

	private PagamentoEntity createObterByIdentificadorPagamentoData() {
		
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
		
		PagamentoEntity pagamentoEntity = PagamentoEntity.builder()
				.identificadorPagamentoExterno(getRandomString())
				.valor(getRandomDouble())
				.pedido(pedidoEntity)
				.build();
		
		pagamentoEntityRepository.save(pagamentoEntity);
		return pagamentoEntity;
	}
}
