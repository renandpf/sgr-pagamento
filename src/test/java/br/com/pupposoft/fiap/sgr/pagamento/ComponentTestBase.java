package br.com.pupposoft.fiap.sgr.pagamento;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.pupposoft.fiap.sgr.config.database.pagamento.repository.PagamentoEntityRepository;

public abstract class ComponentTestBase {

	@Autowired
	protected PagamentoEntityRepository pagamentoEntityRepository;
	
	protected void cleanAllDatabase() {
		pagamentoEntityRepository.deleteAll();
	}
	
}
