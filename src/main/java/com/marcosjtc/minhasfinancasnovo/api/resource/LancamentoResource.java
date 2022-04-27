package com.marcosjtc.minhasfinancasnovo.api.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marcosjtc.minhasfinancasnovo.api.dto.AtualizaStatusDTO;
import com.marcosjtc.minhasfinancasnovo.api.dto.LancamentoDTO;
import com.marcosjtc.minhasfinancasnovo.exception.RegraNegocioException;
import com.marcosjtc.minhasfinancasnovo.model.entity.Lancamento;
import com.marcosjtc.minhasfinancasnovo.model.entity.Usuario;
import com.marcosjtc.minhasfinancasnovo.model.enums.StatusLancamento;
import com.marcosjtc.minhasfinancasnovo.model.enums.TipoLancamento;
import com.marcosjtc.minhasfinancasnovo.service.LancamentoService;
import com.marcosjtc.minhasfinancasnovo.service.UsuarioService;

@RestController
@RequestMapping("api/lancamentos")
//@RequiredArgsConstructor (Lombok e retira o construtor e coloca final nos atributos. Aula 73).
//Final obriga os parâmetros no construtor.
public class LancamentoResource {
	
	private LancamentoService service;
	private UsuarioService usuarioService;

	public LancamentoResource(LancamentoService service, UsuarioService usuarioService) {
		this.service = service;
		this.usuarioService = usuarioService;
	}
	
	//Para passar parâmetros via url usa-se @RequestParam.
	//Required determina se será obrigatório ou opcional. (Default = obrigatório).
	@GetMapping
	public ResponseEntity buscar(
			//Pode ser assim também @RequestParam java.util.Map<String,String> params. Passa a chavee e o valor.
			@RequestParam(value="descricao", required = false) String descricao,
			@RequestParam(value="mes", required = false) Integer mes,
			@RequestParam(value="ano", required = false) Integer ano,
			@RequestParam("usuario") Long idUsuario			
			) {
		
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);
		
		Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
		if (!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuario não encontrado para o Id informado");
		}else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
		
		//Método ValueOf busca no enum a string passada como parâmetro.
		
		return service.obterPorId(id).map(entity -> {
			StatusLancamento  statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
			if (statusSelecionado == null) {
				return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie um status válido!");
			}
			try {
				entity.setStatus(statusSelecionado);
				service.atualizar(entity);
				return ResponseEntity.ok(entity);
			} catch (RegraNegocioException e) {
				  return ResponseEntity.badRequest().body(e.getMessage());
			  }
			
		}).orElseGet(() -> 
	    new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
	//Json será convertido em DTO
	//@PostMapping é a raiz. Usado para salvar recurso no servidor.
	//Quando usa o @PostMapping pode retornar o responseentity "ok",
	//como o created.
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
	  try {
		Lancamento entidade = converter(dto);
		entidade = service.salvar(entidade);
		return new ResponseEntity(entidade, HttpStatus.CREATED);
	  } catch (RegraNegocioException e) {
		  return ResponseEntity.badRequest().body(e.getMessage());
	  }
		
	}
	
	//Usado para recuperar recurso no servidor.
	//Quando o "id" é passado na url o valor é colocado na variável "id".
	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		
		//entity é o que retorna de ObterPorId
		return service.obterPorId(id).map(entity -> {
			try {
			Lancamento lancamento = converter(dto);
			lancamento.setId(entity.getId());
			service.atualizar(lancamento);
			return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				  return ResponseEntity.badRequest().body(e.getMessage());
			}
			
		}).orElseGet(() -> 
		    new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
		
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		
		//entity é o que retorna de ObterPorId
				return service.obterPorId(id).map(entity -> {					
					service.deletar(entity);
					return new ResponseEntity(HttpStatus.NO_CONTENT);
				}).orElseGet(() -> 
				    new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
		
		
	}
	
	//Converte DTO em uma entidade JPA.
	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId()); //Se precisar atualizar tem que vir preenchido com o id.
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		
		Usuario usuario = usuarioService
		.obterPorId(dto.getUsuario())
		.orElseThrow(() -> new RegraNegocioException("Usuario não encontrado para o Id informado"));
		
		lancamento.setUsuario(usuario);
		if (dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));	
		}
		if (dto.getStatus()!= null) {
		    lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}    
		
		return lancamento;
	}

}
