package br.com.pupposoft.fiap.sgr.pagamento.core.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificarDto {
	private List<String> destinatarios;
	private String assunto;
	private String conteudo;
}
