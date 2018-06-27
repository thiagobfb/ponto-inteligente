package com.thiagobernardo.pontointeligente.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thiagobernardo.pontointeligente.api.entities.Empresa;
import com.thiagobernardo.pontointeligente.api.repositories.EmpresaRepository;
import com.thiagobernardo.pontointeligente.api.services.EmpresaService;

@Service
public class EmpresaServiceImpl implements EmpresaService {
	
	private static final Logger log = LoggerFactory.getLogger(EmpresaServiceImpl.class);
	
	@Autowired
	private EmpresaRepository empresaRepository;

	/* (non-Javadoc)
	 * @see com.thiagobernardo.pontointeligente.api.services.EmpresaService#buscarPorCnpj(java.lang.String)
	 */
	@Override
	public Optional<Empresa> buscarPorCnpj(String cnpj) {
		log.info("Buscando uma empresa por CNPJ {}", cnpj);
		return Optional.ofNullable(empresaRepository.findByCnpj(cnpj));
	}

	/* (non-Javadoc)
	 * @see com.thiagobernardo.pontointeligente.api.services.EmpresaService#persistir(com.thiagobernardo.pontointeligente.api.entities.Empresa)
	 */
	@Override
	public Empresa persistir(Empresa empresa) {
		log.info("Persistindo empresa {}", empresa);
		return this.empresaRepository.save(empresa);
	}

}
