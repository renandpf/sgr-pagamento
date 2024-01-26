package br.com.pupposoft.fiap.sgr.pagamento.core.exception;

import br.com.pupposoft.fiap.starter.exception.SystemBaseException;
import lombok.Getter;

@Getter
public class ErrorToAccessPagamentoServicoExternoException extends SystemBaseException {
    private static final long serialVersionUID = -5602569615211742683L;
    
	private String code = "sgr.erroAoAcessarSistemaPagamentoExterno";//NOSONAR
    private String message = "Erro ao acessar sistema de pagamento externo";//NOSONAR
    private Integer httpStatus = 500;//NOSONAR
}
