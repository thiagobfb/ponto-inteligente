/**
 * 
 */
package com.thiagobernardo.pontointeligente.api.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.thiagobernardo.pontointeligente.api.dtos.FuncionarioDTO;
import com.thiagobernardo.pontointeligente.api.entities.Empresa;
import com.thiagobernardo.pontointeligente.api.entities.Funcionario;
import com.thiagobernardo.pontointeligente.api.enums.PerfilEnum;
import com.thiagobernardo.pontointeligente.api.services.FuncionarioService;
import com.thiagobernardo.pontointeligente.api.utils.PasswordUtils;

/**
 * @author thiago
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FuncionarioControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private FuncionarioService funcionarioService;
	
	private static final String URL_BASE = "/api/funcionarios/";
	private static final Long ID_FUNCIONARIO = 1L;
	private static final Long ID_EMPRESA = 1L;
	private static final String CNPJ = "51463645000100";
	private static final String RAZAO_SOCIAL = "Empresa XYZ";
	private static final String VALOR_HORA = "50.0";
	private static final String QTD_HORAS_ALMOCO = "1.0";
	private static final String QTD_HORAS_TRABALHO_DIA = "8.0";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Funcionario funcionario = this.obterDadosFuncionario();
		BDDMockito.given(this.funcionarioService.buscarPorId(Mockito.anyLong())).willReturn(Optional.of(funcionario));
		BDDMockito.given(this.funcionarioService.buscarPorEmail(Mockito.anyString())).willReturn(Optional.of(funcionario));
		BDDMockito.given(this.funcionarioService.persistir(Mockito.any(Funcionario.class))).willReturn(funcionario);
	}

	@Test
	@WithMockUser
	public void testAtualizar() throws JsonProcessingException, Exception {
		mvc.perform(MockMvcRequestBuilders.put(URL_BASE + ID_FUNCIONARIO)
				.content(this.obterJsonRequisicaoPut())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(ID_FUNCIONARIO))
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	private String obterJsonRequisicaoPut() throws JsonProcessingException {
		FuncionarioDTO funcionarioDTO = new FuncionarioDTO();
		funcionarioDTO.setId(ID_FUNCIONARIO);
		funcionarioDTO.setEmail("email@email.com");
		funcionarioDTO.setNome("Fulano de Tal");
		funcionarioDTO.setQtdHorasAlmoco(Optional.of(QTD_HORAS_ALMOCO));
		funcionarioDTO.setQtdHorasTrabalhoDia(Optional.of(QTD_HORAS_TRABALHO_DIA));
		funcionarioDTO.setValorHora(Optional.of(VALOR_HORA));
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		return mapper.writeValueAsString(funcionarioDTO);
	}
	
	private Empresa obterDadosEmpresa() {
		Empresa empresa = new Empresa();
		empresa.setId(ID_EMPRESA);
		empresa.setRazaoSocial(RAZAO_SOCIAL);
		empresa.setCnpj(CNPJ);
		return empresa;
	}
	
	private Funcionario obterDadosFuncionario() {
		Funcionario funcionario = new Funcionario();
		funcionario.setId(ID_FUNCIONARIO);
		funcionario.setNome("Fulano de Tal");
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		funcionario.setSenha(PasswordUtils.gerarBCrypt("123456"));
		funcionario.setCpf("24291173474");
		funcionario.setEmail("email@email.com");
		funcionario.setEmpresa(obterDadosEmpresa());
		return funcionario;
	}

}
