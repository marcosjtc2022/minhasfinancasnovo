package com.marcosjtc.minhasfinancasnovo.api.resource;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marcosjtc.minhasfinancasnovo.api.dto.UsuarioDTO;
import com.marcosjtc.minhasfinancasnovo.exception.RegraNegocioException;
import com.marcosjtc.minhasfinancasnovo.model.entity.Usuario;
import com.marcosjtc.minhasfinancasnovo.service.LancamentoService;
import com.marcosjtc.minhasfinancasnovo.service.UsuarioService;


//Esta annotation + o método construtor faz o spring injetar a dependência
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {
	
	private UsuarioService service;
	private LancamentoService lancamentoService;
	
	public UsuarioResource(UsuarioService service ) {
		this.service = service;
	}
	
	@PostMapping("/autenticar")
	public ResponseEntity autenticar(@RequestBody UsuarioDTO dto ) {
		
		try {
			Usuario usuarioAutenticado = service.autenticar(dto.getEmail(),dto.getSenha());
			return ResponseEntity.ok(usuarioAutenticado);
		} catch(RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	//@RequestBody - Inofmra ao objeto Json que os dados que vêm da requisição 
	// referentes ao usuário, sejam transformados no objeto UsuarioDTO
	//Quando usa o @PostMapping pode retornar o responseentity "ok",
	//como o created.
	@PostMapping
	public ResponseEntity salvar(@RequestBody UsuarioDTO dto ) {
		
		Usuario usuario = new Usuario();
		usuario.setEmail(dto.getEmail());
		usuario.setNome(dto.getNome());
		usuario.setSenha(dto.getSenha());
		
		
		try {
			Usuario usuarioSalvo = service.salvarUsuario(usuario);
			return new ResponseEntity(usuarioSalvo,HttpStatus.CREATED);
		} catch(RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity obterSaldo(@PathVariable("id") Long id) {
		
		Optional<Usuario> usuario = service.obterPorId(id);
		
		if(!usuario.isPresent()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		
		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
		return ResponseEntity.ok(saldo);
		
	}
}
