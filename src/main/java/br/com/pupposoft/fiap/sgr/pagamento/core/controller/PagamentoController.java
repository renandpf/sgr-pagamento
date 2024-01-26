package br.com.pupposoft.fiap.sgr.pagamento.core.controller;

import br.com.pupposoft.fiap.sgr.pagamento.core.domain.PlataformaPagamento;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.PagamentoDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EfetuarPagamentoParamDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.dto.flow.EfetuarPagamentoReturnDto;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.AtualizarStatusPagamentoUseCase;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.EfetuarPagamentoUseCase;
import br.com.pupposoft.fiap.sgr.pagamento.core.usecase.ObterPagamentoUsecase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class PagamentoController {
	
    private EfetuarPagamentoUseCase efetuarPagamentoUseCase;
	
    private AtualizarStatusPagamentoUseCase atualizarStatusPagamentoUseCase;
	
    private ObterPagamentoUsecase obterPagamentoUseCase;

    public EfetuarPagamentoReturnDto efetuar(EfetuarPagamentoParamDto paramsDto) {
        return this.efetuarPagamentoUseCase.efetuar(paramsDto);
    }

    public void notificacoes(PlataformaPagamento plataformaPagamento, String identificadorPagamento) {
        atualizarStatusPagamentoUseCase.atualizar(plataformaPagamento, identificadorPagamento);
    }

	public PagamentoDto obterByIdentificadorPagamento(String identificadorPagamentoExterno) {
		return obterPagamentoUseCase.obterPorIdentificadorPagamento(identificadorPagamentoExterno);
	}
}
