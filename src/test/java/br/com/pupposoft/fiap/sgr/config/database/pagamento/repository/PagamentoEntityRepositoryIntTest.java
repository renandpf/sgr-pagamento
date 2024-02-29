package br.com.pupposoft.fiap.sgr.config.database.pagamento.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.ClienteEntity;
import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.PagamentoEntity;
import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.PedidoEntity;

import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.*;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class PagamentoEntityRepositoryIntTest {

	@Autowired
	private PagamentoEntityRepository pagamentoEntityRepository;
	
    @Autowired
    private TestEntityManager entityManager;
	
    @Test
    void shouldSucessOnFindByIdentificadorPagamentoExterno() {

    	ClienteEntity clienteEntityA = ClienteEntity.builder()
				.clienteId(getRandomLong())
				.nome(getRandomString())
				.email(getRandomString())
				.telefone(getRandomString())
				.build();
		
		PedidoEntity pedidoEntityA = PedidoEntity.builder()
				.id(getRandomLong())
				.valor(getRandomDouble())
				.cliente(clienteEntityA)
				.build();
		
		PagamentoEntity pagamentoEntityA = PagamentoEntity.builder()
				.identificadorPagamentoExterno(getRandomString())
				.valor(getRandomDouble())
				.pedido(pedidoEntityA)
				.build();
		
		ClienteEntity clienteEntityB = ClienteEntity.builder()
				.clienteId(getRandomLong())
				.nome(getRandomString())
				.email(getRandomString())
				.telefone(getRandomString())
				.build();
		
		PedidoEntity pedidoEntityB = PedidoEntity.builder()
				.id(getRandomLong())
				.valor(getRandomDouble())
				.cliente(clienteEntityB)
				.build();
		
		PagamentoEntity pagamentoEntityB = PagamentoEntity.builder()
				.identificadorPagamentoExterno(getRandomString())
				.valor(getRandomDouble())
				.pedido(pedidoEntityB)
				.build();
    	
    	entityManager.persist(pagamentoEntityA);
    	entityManager.persist(pagamentoEntityB);

    	Optional<PagamentoEntity> findByIdentificadorPagamentoExternoOP = pagamentoEntityRepository
    		.findByIdentificadorPagamentoExterno(pagamentoEntityA.getIdentificadorPagamentoExterno());
    	
    	assertTrue(findByIdentificadorPagamentoExternoOP.isPresent());
    	
    	PagamentoEntity pagamentoEntityFound = findByIdentificadorPagamentoExternoOP.get();
    	
    	assertEquals(pagamentoEntityA.getIdentificadorPagamentoExterno(), pagamentoEntityFound.getIdentificadorPagamentoExterno());
    	assertEquals(pagamentoEntityA.getValor(), pagamentoEntityFound.getValor());
    	assertEquals(pagamentoEntityA.getPedido().getId(), pagamentoEntityFound.getPedido().getId());
    	assertEquals(pagamentoEntityA.getPedido().getValor(), pagamentoEntityFound.getPedido().getValor());
    	assertEquals(pagamentoEntityA.getPedido().getCliente().getId(), pagamentoEntityFound.getPedido().getCliente().getId());
    	assertEquals(pagamentoEntityA.getPedido().getCliente().getNome(), pagamentoEntityFound.getPedido().getCliente().getNome());
    	assertEquals(pagamentoEntityA.getPedido().getCliente().getTelefone(), pagamentoEntityFound.getPedido().getCliente().getTelefone());
    	assertEquals(pagamentoEntityA.getPedido().getCliente().getEmail(), pagamentoEntityFound.getPedido().getCliente().getEmail());
    }
	
}
