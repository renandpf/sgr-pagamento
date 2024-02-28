package br.com.pupposoft.fiap.sgr.pagamento.core.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificarDto implements Serializable {
	private static final long serialVersionUID = 4549831220800579527L;
	private List<String> destinatarios;
	private String assunto;
	private String conteudo;
}
