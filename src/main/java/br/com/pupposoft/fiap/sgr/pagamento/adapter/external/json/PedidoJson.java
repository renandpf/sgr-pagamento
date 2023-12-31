package br.com.pupposoft.fiap.sgr.pagamento.adapter.external.json;

import java.time.LocalDate;
import java.util.List;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Builder
public class PedidoJson {
	private Long id;
    private String observacao;
    private StatusPedido status;
    private LocalDate dataCadastro;
    private LocalDate dataConclusao;
    private Long clienteId;
    private List<ItemJson> itens;
}
