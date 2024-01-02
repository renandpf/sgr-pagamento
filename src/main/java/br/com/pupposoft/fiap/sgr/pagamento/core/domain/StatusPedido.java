package br.com.pupposoft.fiap.sgr.pagamento.core.domain;

public enum StatusPedido {
    RECEBIDO,
    AGUARDANDO_CONFIRMACAO_PAGAMENTO,
    PAGO,
    EM_PREPARACAO,
    PRONTO,
    FINALIZADO,
    PAGAMENTO_INVALIDO;
    
    public static StatusPedido get(Long id) {
    	StatusPedido[] values = StatusPedido.values();
    	return values[id.intValue()]; 
    }
    
    public static Long get(StatusPedido status) {
    	return (long) status.ordinal();
    }
}
