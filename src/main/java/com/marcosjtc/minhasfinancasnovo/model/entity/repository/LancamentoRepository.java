package com.marcosjtc.minhasfinancasnovo.model.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marcosjtc.minhasfinancasnovo.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento,Long> {
	
	

}
