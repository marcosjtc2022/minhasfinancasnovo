package com.marcosjtc.minhasfinancasnovo.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.marcosjtc.minhasfinancasnovo.exception.RegraNegocioException;
import com.marcosjtc.minhasfinancasnovo.model.entity.Lancamento;
import com.marcosjtc.minhasfinancasnovo.model.entity.Usuario;
import com.marcosjtc.minhasfinancasnovo.model.entity.repository.LancamentoRepository;
import com.marcosjtc.minhasfinancasnovo.model.enums.StatusLancamento;
import com.marcosjtc.minhasfinancasnovo.model.enums.TipoLancamento;
import com.marcosjtc.minhasfinancasnovo.service.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService {

	private LancamentoRepository repository;
	
	public LancamentoServiceImpl(LancamentoRepository repository) {
	       this.repository = repository;	
	}
	
	@Override
	@Transactional //Abre uma transação. Ao final se ocorrer tudo bem faz commit.
	//No caso de erro faz rollback.
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		//Garante que passa um lançamento com id. Caso contrário lança null pointer exception
		Objects.requireNonNull(lancamento.getId());
		validar(lancamento);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		repository.delete(lancamento);
		
	}

	@Override
	@Transactional(readOnly = true) //Spring faz otimizações da consulta.
	public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
		// TODO Auto-generated method stub
		Example example = Example.of(lancamentoFiltro, ExampleMatcher.matching()
				.withIgnoreCase() //Não leva em conta caixa alta ou baixa
				.withStringMatcher(StringMatcher.STARTING)); //Igual ao like sql
		return repository.findAll(example);
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		lancamento.setStatus(status);
		atualizar(lancamento);
		
	}

	@Override
	public void validar(Lancamento lancamento) {
		
		if (lancamento.getDescricao() == null || lancamento.getDescricao() == "" ) {
			throw new RegraNegocioException("Informe uma Descrição válida. ");
		}
		
		if (lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12 ) {
			throw new RegraNegocioException("Informe um Mês válido. ");
		}
		
		if (lancamento.getAno() == null || lancamento.getAno().toString().length() != 4 ) {
			throw new RegraNegocioException("Informe um Ano válido. ");
		}
		
		if (lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null ) {
			throw new RegraNegocioException("Informe um Usuário válido. ");
		}
		
		//Classe bigdecimal tem a função compareto que retorna 1 caso o valor seja maior
		//Retorna zero se o valor for igual, e retorna -1 se o valor for menor que o passado.
		//Neste caso o valor é zero e será lançada a mensagem.
		if (lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1 ) {
			throw new RegraNegocioException("Informe um Valor válido. ");
		}
		

		if (lancamento.getTipo() == null ) {
			throw new RegraNegocioException("Informe um Tipo de Lançamento. ");
		}
		
	}

	@Override
	public Optional<Lancamento> obterPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal obterSaldoPorUsuario(Long id) {
		
		Usuario usuario = new Usuario();
		usuario.setId(id);
		
		BigDecimal receitas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(usuario, TipoLancamento.RECEITA, StatusLancamento.EFETIVADO);
		BigDecimal despesas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(usuario, TipoLancamento.DESPESA, StatusLancamento.EFETIVADO);
		
		if(receitas == null) {
			receitas = BigDecimal.ZERO;
		}
		
		if(despesas == null) {
			despesas = BigDecimal.ZERO;
		}
		
		return receitas.subtract(despesas);
	}

}
