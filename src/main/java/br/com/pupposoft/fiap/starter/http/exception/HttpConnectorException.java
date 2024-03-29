package br.com.pupposoft.fiap.starter.http.exception;

import br.com.pupposoft.fiap.starter.exception.SystemBaseException;
import lombok.Getter;

@Getter
public class HttpConnectorException extends SystemBaseException {
	private static final long serialVersionUID = -7731178336165557046L;
	
	private final String code = "sgr.httpConnectorError";//NOSONAR
	private String message;//NOSONAR
	private Integer httpStatus;//NOSONAR
	
	public HttpConnectorException(Integer httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
