package br.com.pupposoft.fiap.sgr.pagamento.adapter.external.json;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class PedidoMessageJson implements Serializable {
	private static final long serialVersionUID = 6699953226673060815L;
	
	private Long id;
    private String status;
}
