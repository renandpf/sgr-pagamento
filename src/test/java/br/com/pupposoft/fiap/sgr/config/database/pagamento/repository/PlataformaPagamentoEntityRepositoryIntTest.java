package br.com.pupposoft.fiap.sgr.config.database.pagamento.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.PagamentoEntity;
import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.PlataformaPagamentoEntity;

import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.*;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class PlataformaPagamentoEntityRepositoryIntTest {

	@Autowired
	private PlataformaPagamentoEntityRepository plataformaPagamentoEntityRepository;
	
    @Autowired
    private TestEntityManager entityManager;
	
    @Test
    void shouldSucessOnFindByIdentificadorPagamentoExterno() {
    	PlataformaPagamentoEntity plataformaPagamentoEntityA = PlataformaPagamentoEntity.builder()
    			.id(getRandomLong())
    			.code(getRandomString())
    			.nome(getRandomString())
    			.status(0L)
    			.build();
    	PlataformaPagamentoEntity plataformaPagamentoEntityB = PlataformaPagamentoEntity.builder()
    			.id(getRandomLong())
    			.code(getRandomString())
    			.nome(getRandomString())
    			.status(0L)
    			.build();
    	PlataformaPagamentoEntity plataformaPagamentoEntityC = PlataformaPagamentoEntity.builder()
    			.id(getRandomLong())
    			.code(getRandomString())
    			.nome(getRandomString())
    			.status(1L)
    			.build();
    	
    	entityManager.persist(plataformaPagamentoEntityA);
    	entityManager.persist(plataformaPagamentoEntityB);
    	entityManager.persist(plataformaPagamentoEntityC);

    	List<PlataformaPagamentoEntity> resultList = plataformaPagamentoEntityRepository.findByStatus(0L);
    	
    	assertEquals(2, resultList.size());
    	
    	PlataformaPagamentoEntity plataformaPagamentoEntityReturnedA = resultList.stream().filter(p -> p.getId().equals(plataformaPagamentoEntityA.getId())).findAny().get();
    	assertEquals(plataformaPagamentoEntityA.getId(), plataformaPagamentoEntityReturnedA.getId());
    	assertEquals(plataformaPagamentoEntityA.getCode(), plataformaPagamentoEntityReturnedA.getCode());
    	assertEquals(plataformaPagamentoEntityA.getNome(), plataformaPagamentoEntityReturnedA.getNome());
    	assertEquals(plataformaPagamentoEntityA.getStatus(), plataformaPagamentoEntityReturnedA.getStatus());
    	
    	PlataformaPagamentoEntity plataformaPagamentoEntityReturnedB = resultList.stream().filter(p -> p.getId().equals(plataformaPagamentoEntityB.getId())).findAny().get();
    	assertEquals(plataformaPagamentoEntityB.getId(), plataformaPagamentoEntityReturnedB.getId());
    	assertEquals(plataformaPagamentoEntityB.getCode(), plataformaPagamentoEntityReturnedB.getCode());
    	assertEquals(plataformaPagamentoEntityB.getNome(), plataformaPagamentoEntityReturnedB.getNome());
    	assertEquals(plataformaPagamentoEntityB.getStatus(), plataformaPagamentoEntityReturnedB.getStatus());
    }
	
}
