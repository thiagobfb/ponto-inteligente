/**
 * 
 */
package com.thiagobernardo.pontointeligente.api.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thiagobernardo.pontointeligente.api.dtos.LancamentoDTO;
import com.thiagobernardo.pontointeligente.api.entities.Funcionario;
import com.thiagobernardo.pontointeligente.api.entities.Lancamento;
import com.thiagobernardo.pontointeligente.api.enums.TipoEnum;
import com.thiagobernardo.pontointeligente.api.response.Response;
import com.thiagobernardo.pontointeligente.api.services.FuncionarioService;
import com.thiagobernardo.pontointeligente.api.services.LancamentoService;

/**
 * @author thiago
 *
 */
@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoController {

	private static final Logger log = LoggerFactory.getLogger(LancamentoController.class);
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private LancamentoService lancamentoService;

	@Autowired
	private FuncionarioService funcionarioService;
	
	@Value("${paginacao.qtd_por_pagina}")
	private int qtdPorPagina;

	public LancamentoController() {
	}

	/**
	 * Retorna a listagem de lançamentos de um funcionário.
	 * 
	 * @param funcionarioId
	 * @return ResponseEntity<Response<LancamentoDTO>>
	 */
	@GetMapping(value = "/funcionario/{funcionarioId}")
	public ResponseEntity<Response<Page<LancamentoDTO>>> listarPorFuncionarioId(
			@PathVariable("funcionarioId") Long funcionarioId,
			@RequestParam(value = "pag", defaultValue = "0") int pag,
			@RequestParam(value = "ord", defaultValue = "id") String ord,
			@RequestParam(value = "dir", defaultValue = "DESC") String dir) {
		log.info("Buscando lançamentos por ID do funcionário: {}, página: {}", funcionarioId, pag);
		Response<Page<LancamentoDTO>> response = new Response<Page<LancamentoDTO>>();

		PageRequest pageRequest = PageRequest.of(pag, this.qtdPorPagina, Direction.valueOf(dir), ord);
		Page<Lancamento> lancamentos = this.lancamentoService.buscarPorFuncionarioId(funcionarioId, pageRequest);
		Page<LancamentoDTO> lancamentosDto = lancamentos.map(lancamento -> this.converterLancamentoDTO(lancamento));

		response.setData(lancamentosDto);
		return ResponseEntity.ok(response);
	}

	/**
	 * Retorna um lançamento por ID.
	 * 
	 * @param id
	 * @return ResponseEntity<Response<LancamentoDTO>>
	 */
	@GetMapping(value = "/{id}")
	public ResponseEntity<Response<LancamentoDTO>> listarPorId(@PathVariable("id") Long id) {
		log.info("Buscando lançamento por ID: {}", id);
		Response<LancamentoDTO> response = new Response<LancamentoDTO>();
		Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);

		if (!lancamento.isPresent()) {
			log.info("Lançamento não encontrado para o ID: {}", id);
			response.getErrors().add("Lançamento não encontrado para o id " + id);
			return ResponseEntity.badRequest().body(response);
		}

		response.setData(this.converterLancamentoDTO(lancamento.get()));
		return ResponseEntity.ok(response);
	}

	/**
	 * Adiciona um novo lançamento.
	 * 
	 * @param lancamento
	 * @param result
	 * @return ResponseEntity<Response<LancamentoDTO>>
	 * @throws ParseException 
	 */
	@PostMapping
	public ResponseEntity<Response<LancamentoDTO>> adicionar(@Valid @RequestBody LancamentoDTO lancamentoDTO,
			BindingResult result) throws ParseException {
		log.info("Adicionando lançamento: {}", lancamentoDTO.toString());
		Response<LancamentoDTO> response = new Response<LancamentoDTO>();
		validarFuncionario(lancamentoDTO, result);
		Lancamento lancamento = this.converterDtoParaLancamento(lancamentoDTO, result);

		if (result.hasErrors()) {
			log.error("Erro validando lançamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		lancamento = this.lancamentoService.persistir(lancamento);
		response.setData(this.converterLancamentoDTO(lancamento));
		return ResponseEntity.ok(response);
	}

	/**
	 * Atualiza os dados de um lançamento.
	 * 
	 * @param id
	 * @param lancamentoDTO
	 * @return ResponseEntity<Response<Lancamento>>
	 * @throws ParseException 
	 */
	@PutMapping(value = "/{id}")
	public ResponseEntity<Response<LancamentoDTO>> atualizar(@PathVariable("id") Long id,
			@Valid @RequestBody LancamentoDTO lancamentoDTO, BindingResult result) throws ParseException {
		log.info("Atualizando lançamento: {}", lancamentoDTO.toString());
		Response<LancamentoDTO> response = new Response<LancamentoDTO>();
		validarFuncionario(lancamentoDTO, result);
		lancamentoDTO.setId(Optional.of(id));
		Lancamento lancamento = this.converterDtoParaLancamento(lancamentoDTO, result);

		if (result.hasErrors()) {
			log.error("Erro validando lançamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		lancamento = this.lancamentoService.persistir(lancamento);
		response.setData(this.converterLancamentoDTO(lancamento));
		return ResponseEntity.ok(response);
	}

	/**
	 * Remove um lançamento por ID.
	 * 
	 * @param id
	 * @return ResponseEntity<Response<Lancamento>>
	 */
	@DeleteMapping(value = "/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<String>> remover(@PathVariable("id") Long id) {
		log.info("Removendo lançamento: {}", id);
		Response<String> response = new Response<String>();
		Optional<Lancamento> lancamento = this.lancamentoService.buscarPorId(id);

		if (!lancamento.isPresent()) {
			log.info("Erro ao remover devido ao lançamento ID: {} ser inválido.", id);
			response.getErrors().add("Erro ao remover lançamento. Registro não encontrado para o id " + id);
			return ResponseEntity.badRequest().body(response);
		}

		this.lancamentoService.remover(id);
		return ResponseEntity.ok(new Response<String>());
	}

	/**
	 * Valida um funcionário, verificando se ele é existente e válido no
	 * sistema.
	 * 
	 * @param lancamentoDTO
	 * @param result
	 */
	private void validarFuncionario(LancamentoDTO lancamentoDTO, BindingResult result) {
		if (lancamentoDTO.getFuncionarioId() == null) {
			result.addError(new ObjectError("funcionario", "Funcionário não informado."));
			return;
		}

		log.info("Validando funcionário id {}: ", lancamentoDTO.getFuncionarioId());
		Optional<Funcionario> funcionario = this.funcionarioService.buscarPorId(lancamentoDTO.getFuncionarioId());
		if (!funcionario.isPresent()) {
			result.addError(new ObjectError("funcionario", "Funcionário não encontrado. ID inexistente."));
		}
	}

	/**
	 * Converte uma entidade lançamento para seu respectivo DTO.
	 * 
	 * @param lancamento
	 * @return LancamentoDTO
	 */
	private LancamentoDTO converterLancamentoDTO(Lancamento lancamento) {
		LancamentoDTO lancamentoDTO = new LancamentoDTO();
		lancamentoDTO.setId(Optional.of(lancamento.getId()));
		lancamentoDTO.setData(this.dateFormat.format(lancamento.getData()));
		lancamentoDTO.setTipo(lancamento.getTipo().toString());
		lancamentoDTO.setDescricao(lancamento.getDescricao());
		lancamentoDTO.setLocalizacao(lancamento.getLocalizacao());
		lancamentoDTO.setFuncionarioId(lancamento.getFuncionario().getId());

		return lancamentoDTO;
	}

	/**
	 * Converte um LancamentoDTO para uma entidade Lancamento.
	 * 
	 * @param lancamentoDTO
	 * @param result
	 * @return Lancamento
	 * @throws ParseException 
	 */
	private Lancamento converterDtoParaLancamento(LancamentoDTO lancamentoDTO, BindingResult result) throws ParseException {
		Lancamento lancamento = new Lancamento();

		if (lancamentoDTO.getId().isPresent()) {
			Optional<Lancamento> lanc = this.lancamentoService.buscarPorId(lancamentoDTO.getId().get());
			if (lanc.isPresent()) {
				lancamento = lanc.get();
			} else {
				result.addError(new ObjectError("lancamento", "Lançamento não encontrado."));
			}
		} else {
			lancamento.setFuncionario(new Funcionario());
			lancamento.getFuncionario().setId(lancamentoDTO.getFuncionarioId());
		}

		lancamento.setDescricao(lancamentoDTO.getDescricao());
		lancamento.setLocalizacao(lancamentoDTO.getLocalizacao());
		lancamento.setData(this.dateFormat.parse(lancamentoDTO.getData()));

		if (EnumUtils.isValidEnum(TipoEnum.class, lancamentoDTO.getTipo())) {
			lancamento.setTipo(TipoEnum.valueOf(lancamentoDTO.getTipo()));
		} else {
			result.addError(new ObjectError("tipo", "Tipo inválido."));
		}

		return lancamento;
	}
}
