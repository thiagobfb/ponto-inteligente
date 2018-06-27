/**
 * 
 */
package com.thiagobernardo.pontointeligente.api.services;

import java.util.Optional;

import com.thiagobernardo.pontointeligente.api.entities.Funcionario;

/**
 * @author thiago
 *
 */
public interface FuncionarioService {
	
	/**
	 * Cadastra um novo funcionário na base de dados
	 * 
	 * @param funcionario
	 * @return Funcionario
	 */
	Funcionario persistir(Funcionario funcionario);
	
	/**
	 * Busca e retorna um funcionário dado um CPF
	 * 
	 * @param cpf
	 * @return Optional<Funcionario>
	 */
	Optional<Funcionario> buscarPorCpf(String cpf);
	
	/**
	 * Busca e retorna um funcionário dado um email
	 * 
	 * @param email
	 * @return Optional<Funcionario>
	 */
	Optional<Funcionario> buscarPorEmail(String email);
	
	/**
	 * Busca e retorna um funcionário por ID
	 * 
	 * @param id
	 * @return Optional<Funcionario>
	 */
	Optional<Funcionario> buscarPorId(Long id);

}
