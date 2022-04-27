package com.marcosjtc.minhasfinancasnovo.service;

import java.util.Optional;

import com.marcosjtc.minhasfinancasnovo.model.entity.Usuario;

public interface UsuarioService {
	
	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);
	
	//Retorna um optiona vazio caso não exista.
	Optional<Usuario> obterPorId(Long id);

}
