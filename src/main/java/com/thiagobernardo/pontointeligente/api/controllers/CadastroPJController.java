/**
 * 
 */
package com.thiagobernardo.pontointeligente.api.controllers;

import java.security.NoSuchAlgorithmException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thiagobernardo.pontointeligente.api.dtos.CadastroPJDTO;
import com.thiagobernardo.pontointeligente.api.entities.Empresa;
import com.thiagobernardo.pontointeligente.api.entities.Funcionario;
import com.thiagobernardo.pontointeligente.api.enums.PerfilEnum;
import com.thiagobernardo.pontointeligente.api.response.Response;
import com.thiagobernardo.pontointeligente.api.services.EmpresaService;
import com.thiagobernardo.pontointeligente.api.services.FuncionarioService;
import com.thiagobernardo.pontointeligente.api.utils.PasswordUtils;

/**
 * @author thiago
 *
 */
@RestController
@RequestMapping("/api/cadastrar-pj")
@CrossOrigin(origins = "*")
public class CadastroPJController {
	
	private static final Logger log = LoggerFactory.getLogger(CadastroPJController.class);
	
	@Autowired
	private FuncionarioService funcionarioService;
	
	@Autowired
	private EmpresaService empresaService;
	
	/**
	 * 
	 */
	public CadastroPJController() {
	}

	/**
	 * Cadastra uma pessoa jurídica no sistema
	 * 
	 * @param cadastroPJDTO
	 * @param result
	 * @return ResponseEntity<Response<CadastroPJDTO>>
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping
	public ResponseEntity<Response<CadastroPJDTO>> cadastrar(@Valid @RequestBody CadastroPJDTO cadastroPJDTO,
			BindingResult result) throws NoSuchAlgorithmException {
		log.info("Cadastrando PJ: {}", cadastroPJDTO.toString());
		Response<CadastroPJDTO> response = new Response<>();
		
		validarDadosExistentes(cadastroPJDTO, result);
		Empresa empresa = this.converterDtoParaEmpresa(cadastroPJDTO);
		Funcionario funcionario = this.converterDtoParaFuncionario(cadastroPJDTO, result);
		
		if (result.hasErrors()) {
			log.error("Erro validando dados de cadastro PJ: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		this.empresaService.persistir(empresa);
		funcionario.setEmpresa(empresa);
		this.funcionarioService.persistir(funcionario);
		
		response.setData(this.converterCadastroPJDTO(funcionario));
		return ResponseEntity.ok(response);
	}

	/**
	 * Popula o DTO de cadastro com os dados do funcionário e empresa.
	 * 
	 * @param funcionario
	 * @return {@link CadastroPJDTO}
	 */
	private CadastroPJDTO converterCadastroPJDTO(Funcionario funcionario) {
		CadastroPJDTO cadastroPJDTO = new CadastroPJDTO();
		cadastroPJDTO.setId(funcionario.getId());
		cadastroPJDTO.setNome(funcionario.getNome());
		cadastroPJDTO.setEmail(funcionario.getEmail());
		cadastroPJDTO.setCpf(funcionario.getCpf());
		cadastroPJDTO.setRazaoSocial(funcionario.getEmpresa().getRazaoSocial());
		cadastroPJDTO.setCnpj(funcionario.getEmpresa().getCnpj());
		
		return cadastroPJDTO;
	}

	/**
	 * Converte os dados do DTO para funcionário.
	 * 
	 * @param cadastroPJDTO
	 * @param result
	 * @return {@link Funcionario}
	 */
	private Funcionario converterDtoParaFuncionario(CadastroPJDTO cadastroPJDTO, BindingResult result) {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(cadastroPJDTO.getNome());
		funcionario.setEmail(cadastroPJDTO.getEmail());
		funcionario.setCpf(cadastroPJDTO.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
		funcionario.setSenha(PasswordUtils.gerarBCrypt(cadastroPJDTO.getSenha()));
		
		return funcionario;
	}

	/**
	 * Converte os dados do DTO para empresa.
	 * 
	 * @param cadastroPJDTO
	 * @return {@link Empresa}
	 */
	private Empresa converterDtoParaEmpresa(CadastroPJDTO cadastroPJDTO) {
		Empresa empresa = new Empresa();
		empresa.setCnpj(cadastroPJDTO.getCnpj());
		empresa.setRazaoSocial(cadastroPJDTO.getRazaoSocial());
		return empresa;
	}

	/**
	 * Verifica se a empresa ou funcionário já existem na base de dados.
	 * 
	 * @param cadastroPJDTO
	 * @param result
	 */
	private void validarDadosExistentes(CadastroPJDTO cadastroPJDTO, BindingResult result) {
		this.empresaService.buscarPorCnpj(cadastroPJDTO.getCnpj())
				.ifPresent(emp -> result.addError(new ObjectError("empresa", "Empresa já existente.")));
		
		this.funcionarioService.buscarPorCpf(cadastroPJDTO.getCpf())
				.ifPresent(emp -> result.addError(new ObjectError("funcionario", "CPF já existente.")));
		
		this.funcionarioService.buscarPorEmail(cadastroPJDTO.getEmail())
				.ifPresent(emp -> result.addError(new ObjectError("funcionario", "E-Mail já existente.")));
	}
	
}
