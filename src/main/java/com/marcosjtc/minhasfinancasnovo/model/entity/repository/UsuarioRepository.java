package com.marcosjtc.minhasfinancasnovo.model.entity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marcosjtc.minhasfinancasnovo.model.entity.Usuario;

//Injeta automaticamente a implementação da interface.
//Já faz parte do contêiner springframework.
//findBy(nome do atributo procurado da entidade buscada (Usuario) dentro de UsuarioRepository)
//procura o parâmetro passado e busca na base de dados.	
//Possível fazer concatenação ex: findByEmailAndNome(String email, String nome)
//Na assinatura do método o nome do atributo tem que ser igual ao que está na entidade.
//Parâmetros têm que ser na ordem em que se está passando.
//boolean existsByEmail(String email) = select * from usuario where exists
public interface UsuarioRepository extends JpaRepository<Usuario,Long>  {
	
	//findByEmail é um querymethod - não precisa informar que vai fazer um select * from usuario where email = email
	//
	//Optional<Usuario> findByEmail(String email);
	
	boolean existsByEmail(String email);
	
	//Optional evita tratamento de null pointer exception
	Optional<Usuario> findByEmail(String email);
	
	
	
	

}
