package br.com.pupposoft.fiap.sgr.pagamento.core.exception;

import br.com.pupposoft.fiap.starter.exception.SystemBaseException;
import lombok.Getter;

@Getter
public class ErrorToNotifiyException extends SystemBaseException {
	private static final long serialVersionUID = -2207026607433523704L;
	
	private String code = "sgr.errorToNotify";//NOSONAR
    private String message = "Erro ao enviar notificação";//NOSONAR
    private Integer httpStatus = 500;//NOSONAR
}
