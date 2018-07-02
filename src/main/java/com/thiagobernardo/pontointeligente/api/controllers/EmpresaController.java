/**
 * 
 */
package com.thiagobernardo.pontointeligente.api.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thiagobernardo.pontointeligente.api.dtos.EmpresaDTO;
import com.thiagobernardo.pontointeligente.api.entities.Empresa;
import com.thiagobernardo.pontointeligente.api.response.Response;
import com.thiagobernardo.pontointeligente.api.services.EmpresaService;

/**
 * @author thiago
 *
 */
@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {
	
	private static final Logger log = LoggerFactory.getLogger(EmpresaController.class);
		
	@Autowired
	private EmpresaService empresaService;
	
	/**
	 * 
	 */
	public EmpresaController() {
	}

	/**
	 * Retorna uma emprea dado um CNPJ.
	 * 
	 * @param cnpj
	 * @return ResponseEntity<Response<EmpresaDTO>>
	 */
	@GetMapping(value = "/cnpj/{cnpj}")
	public ResponseEntity<Response<EmpresaDTO>> buscarPorCnpj(@PathVariable("cnpj") String cnpj) {
		log.info("Buscando empresa por CNPJ: {}", cnpj);
		Response<EmpresaDTO> response = new Response<>();
		Optional<Empresa> empresa = this.empresaService.buscarPorCnpj(cnpj);
		
		if (!empresa.isPresent()) {
			log.info("Empresa não encontrada para o CNPJ: {}", cnpj);
			response.getErrors().add("Empresa não encontrada para o CNPJ " + cnpj);
			return ResponseEntity.badRequest().body(response);
		}
		
		response.setData(this.converterEmpresaDTO(empresa.get()));
		return ResponseEntity.ok(response);
	}

	/**
	 * Popula um DTO com os dados de uma empresa
	 * 
	 * @param empresa
	 * @return {@link EmpresaDTO}
	 */
	private EmpresaDTO converterEmpresaDTO(Empresa empresa) {
		EmpresaDTO empresaDTO = new EmpresaDTO();
		empresaDTO.setId(empresa.getId());
		empresaDTO.setCnpj(empresa.getCnpj());
		empresaDTO.setRazaoSocial(empresa.getRazaoSocial());
		
		return empresaDTO;
	}
	
}
