package br.com.pupposoft.fiap.sgr.pagamento.core.gateway;

import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PedidoDto;

public interface PedidoGateway {
    void alterarStatus(PedidoDto pedido);
}
