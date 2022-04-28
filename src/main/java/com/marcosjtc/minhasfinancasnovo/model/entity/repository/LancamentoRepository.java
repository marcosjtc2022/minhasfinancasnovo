package com.marcosjtc.minhasfinancasnovo.model.entity.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.marcosjtc.minhasfinancasnovo.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento,Long> {
	
	//Método mais complexo não é interessante fazer por query method.
	//Neste caso é feito através de Jpql
	//No HQL não se coloca o nome da tabela, mas, o nome da entidade.
	//O join tem que ser referenciado através da propriedade.
	//O join do Jpql não precisa colocar o "On".
	@Query(value = " select sum(l.valor) from Lancamento l join l.usuario u"
			+ "where u.id = :idUsuario and l.tipo =:tipo group by u")
	BigDecimal obterSaldoPorTipoLancamentoEUsuario(@Param("idUsuario") Long idUsuario, @Param("tipo") String tipo);
	
	

}
