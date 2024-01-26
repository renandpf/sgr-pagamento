package br.com.pupposoft.fiap.sgr.pagamento.adapter.repository;

import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomLong;
import static br.com.pupposoft.fiap.test.databuilder.DataBuilderBase.getRandomString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.PlataformaPagamentoEntity;
import br.com.pupposoft.fiap.sgr.config.database.pagamento.entity.StatusPlataformaPagamento;
import br.com.pupposoft.fiap.sgr.config.database.pagamento.repository.PlataformaPagamentoEntityRepository;
import br.com.pupposoft.fiap.sgr.pagamento.core.domain.PlataformaPagamento;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.PlataformaPagamentoConfigParamsDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.PlataformaPagamentoConfigReturnDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.ErrorToAccessRepositoryException;
import br.com.pupposoft.fiap.sgr.pagamento.core.gateway.PlataformaPagamentoConfigGateway;

@ExtendWith(MockitoExtension.class)
class PlataformaPagamentoConfigMySqlRepositoryUnitTest {

	@InjectMocks
	private PlataformaPagamentoConfigGateway pagamentoGateway = new PlataformaPagamentoConfigMySqlRepository(null);
	
	@Mock
	private PlataformaPagamentoEntityRepository plataformaPagamentoEntityRepository;
	
	@Test
	void shouldSucessOnObter() {
		
		PlataformaPagamentoConfigParamsDto paramDto = PlataformaPagamentoConfigParamsDto.builder().build();
		
		PlataformaPagamentoEntity plataformaPagamentoEntity = PlataformaPagamentoEntity.builder()
				.id(getRandomLong())
				.code("MERCADO_PAGO")
				.nome(getRandomString())
				.status((long) StatusPlataformaPagamento.ATIVO.ordinal())
				.build();
		doReturn(Arrays.asList(plataformaPagamentoEntity))
			.when(plataformaPagamentoEntityRepository)
			.findByStatus((long) StatusPlataformaPagamento.ATIVO.ordinal());
		
		PlataformaPagamentoConfigReturnDto plataformaPagamentoConfigReturnDto = pagamentoGateway.obter(paramDto);
		
		verify(plataformaPagamentoEntityRepository).findByStatus(0L);
		
		assertEquals(PlataformaPagamento.MERCADO_PAGO , plataformaPagamentoConfigReturnDto.getPlataformaPagamento());
	}
	
	@Test
	void shouldSucessReturnMockOnObter() {
		
		PlataformaPagamentoConfigParamsDto paramDto = PlataformaPagamentoConfigParamsDto.builder().build();
		
		doReturn(Arrays.asList())
			.when(plataformaPagamentoEntityRepository)
			.findByStatus((long) StatusPlataformaPagamento.ATIVO.ordinal());
		
		PlataformaPagamentoConfigReturnDto plataformaPagamentoConfigReturnDto = pagamentoGateway.obter(paramDto);
		
		verify(plataformaPagamentoEntityRepository).findByStatus(0L);
		
		assertEquals(PlataformaPagamento.MOCK , plataformaPagamentoConfigReturnDto.getPlataformaPagamento());
	}
	
	@Test
	void shouldErrorToAccessRepositoryExceptionOnObter() {
		
		PlataformaPagamentoConfigParamsDto paramDto = PlataformaPagamentoConfigParamsDto.builder().build();
		
		doThrow(new RuntimeException())
			.when(plataformaPagamentoEntityRepository)
			.findByStatus((long) StatusPlataformaPagamento.ATIVO.ordinal());
		
		assertThrows(ErrorToAccessRepositoryException.class, () -> pagamentoGateway.obter(paramDto));
		
		verify(plataformaPagamentoEntityRepository).findByStatus(0L);
	}
}
