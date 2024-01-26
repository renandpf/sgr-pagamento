package br.com.pupposoft.fiap.sgr.pagamento.core.exception;

import br.com.pupposoft.fiap.starter.exception.SystemBaseException;
import lombok.Getter;

@Getter
public class CamposObrigatoriosNaoPreechidoException extends SystemBaseException {
	private static final long serialVersionUID = -4204425173101901157L;
    
	private String code = "sgr.camposObrigatoriosNaoPreenchido";//NOSONAR
    private String message;//NOSONAR
    private Integer httpStatus = 400;//NOSONAR
    
	public CamposObrigatoriosNaoPreechidoException(String message) {
		this.message = message;
	}
}
