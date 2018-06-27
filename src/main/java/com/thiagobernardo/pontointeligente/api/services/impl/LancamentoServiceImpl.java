/**
 * 
 */
package com.thiagobernardo.pontointeligente.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.thiagobernardo.pontointeligente.api.entities.Lancamento;
import com.thiagobernardo.pontointeligente.api.repositories.LancamentoRepository;
import com.thiagobernardo.pontointeligente.api.services.LancamentoService;

/**
 * @author thiago
 *
 */
@Service
public class LancamentoServiceImpl implements LancamentoService {

	private static final Logger log = LoggerFactory.getLogger(LancamentoServiceImpl.class);
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	/* (non-Javadoc)
	 * @see com.thiagobernardo.pontointeligente.api.services.LancamentoService#buscarPorFuncionarioId(java.lang.Long, org.springframework.data.domain.PageRequest)
	 */
	@Override
	public Page<Lancamento> buscarPorFuncionarioId(Long funcionarioId, PageRequest pageRequest) {
		log.info("Buscando lançamento pelo funcionário ID {}", funcionarioId);
		return this.lancamentoRepository.findByFuncionarioId(funcionarioId, pageRequest);
	}

	/* (non-Javadoc)
	 * @see com.thiagobernardo.pontointeligente.api.services.LancamentoService#buscarPorId(java.lang.Long)
	 */
	@Override
	public Optional<Lancamento> buscarPorId(Long id) {
		log.info("Buscando lançamento pelo ID {}", id);
		return this.lancamentoRepository.findById(id);
	}

	/* (non-Javadoc)
	 * @see com.thiagobernardo.pontointeligente.api.services.LancamentoService#persistir(com.thiagobernardo.pontointeligente.api.entities.Lancamento)
	 */
	@Override
	public Lancamento persistir(Lancamento lancamento) {
		log.info("Persistindo lançamento {}", lancamento);
		return this.lancamentoRepository.save(lancamento);
	}

	/* (non-Javadoc)
	 * @see com.thiagobernardo.pontointeligente.api.services.LancamentoService#remover(java.lang.Long)
	 */
	@Override
	public void remover(Long id) {
		log.info("Removendo o lançamento ID {}", id);
		this.lancamentoRepository.deleteById(id);
	}

}
