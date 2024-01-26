package br.com.pupposoft.fiap.sgr.config.database.pagamento.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.PagamentoEntity;
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
    	PagamentoEntity pagamentoEntityA = PagamentoEntity.builder()

    			.identificadorPagamentoExterno(getRandomString())
    			.valor(getRandomDouble())
    			.pedidoId(getRandomLong())
    			.build();
    	PagamentoEntity pagamentoEntityB = PagamentoEntity.builder()

    			.identificadorPagamentoExterno(getRandomString())
    			.valor(getRandomDouble())
    			.pedidoId(getRandomLong())
    			.build();
    	
    	entityManager.persist(pagamentoEntityA);
    	entityManager.persist(pagamentoEntityB);

    	Optional<PagamentoEntity> findByIdentificadorPagamentoExternoOP = pagamentoEntityRepository
    		.findByIdentificadorPagamentoExterno(pagamentoEntityA.getIdentificadorPagamentoExterno());
    	
    	assertTrue(findByIdentificadorPagamentoExternoOP.isPresent());
    	
    	PagamentoEntity pagamentoEntityFound = findByIdentificadorPagamentoExternoOP.get();
    	
    	assertEquals(pagamentoEntityA.getIdentificadorPagamentoExterno(), pagamentoEntityFound.getIdentificadorPagamentoExterno());
    	assertEquals(pagamentoEntityA.getValor(), pagamentoEntityFound.getValor());
    	assertEquals(pagamentoEntityA.getPedidoId(), pagamentoEntityFound.getPedidoId());
    }
	
}
