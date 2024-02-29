package br.com.pupposoft.fiap.sgr.config.database.pagamento.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Pedido")
public class PedidoEntity {
	@Id
	private Long id;
	private Double valor;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "clienteId")
	private ClienteEntity cliente;

}
