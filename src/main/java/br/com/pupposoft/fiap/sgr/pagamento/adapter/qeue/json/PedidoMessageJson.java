package br.com.pupposoft.fiap.sgr.pagamento.adapter.qeue.json;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PedidoMessageJson {
	private Long id;
	private ClienteJson cliente;
    private String status;
    private Double valor;
}
