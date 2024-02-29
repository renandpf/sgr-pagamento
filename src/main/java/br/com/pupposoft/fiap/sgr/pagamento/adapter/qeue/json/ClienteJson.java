package br.com.pupposoft.fiap.sgr.pagamento.adapter.qeue.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteJson {
	private Long id;
	private String nome;
	private String email;
	private String telefone;
}
