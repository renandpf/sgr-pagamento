package br.com.pupposoft.fiap.sgr.pagamento.adapter.qeue;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import br.com.pupposoft.fiap.sgr.pagamento.core.dto.NotificarDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.exception.ErrorToNotifiyException;

@ExtendWith(MockitoExtension.class)
class NotificarAwsSqsGatewayUnitTest {

	@InjectMocks
	private NotificarAwsSqsGateway notificarAwsSqsGateway;
	
	@Mock
	private JmsTemplate notifyClienteTemplate;
	
	@Test
	void ShouldSucessOnNotificar() {
		
		NotificarDto notificarDto = NotificarDto.builder().build();
		
		notificarAwsSqsGateway.notificar(notificarDto);
		
		verify(notifyClienteTemplate).convertAndSend("notificar-qeue", notificarDto);
		
	}
	
	@Test
	void ShouldErrorOnNotificar() {
		
		NotificarDto notificarDto = NotificarDto.builder().build();
		
		doThrow(new RuntimeException()).when(notifyClienteTemplate).convertAndSend(anyString(), eq(notificarDto));
		
		assertThrows(ErrorToNotifiyException.class, () -> notificarAwsSqsGateway.notificar(notificarDto));
		
		verify(notifyClienteTemplate).convertAndSend("notificar-qeue", notificarDto);
		
	}
	
}
