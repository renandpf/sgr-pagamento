package br.com.pupposoft.fiap.sgr.pagamento.adapter.external.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class PedidoMessageJson {
	private Long id;
    private String status;
}
