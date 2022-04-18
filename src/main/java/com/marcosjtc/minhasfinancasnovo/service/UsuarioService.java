package com.marcosjtc.minhasfinancasnovo.service;

import com.marcosjtc.minhasfinancasnovo.model.entity.Usuario;

public interface UsuarioService {
	
	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);

}
