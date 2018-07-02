/**
 * 
 */
package com.thiagobernardo.pontointeligente.api.controllers;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;

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
import com.thiagobernardo.pontointeligente.api.dtos.CadastroPFDTO;
import com.thiagobernardo.pontointeligente.api.entities.Empresa;
import com.thiagobernardo.pontointeligente.api.entities.Funcionario;
import com.thiagobernardo.pontointeligente.api.enums.PerfilEnum;
import com.thiagobernardo.pontointeligente.api.services.EmpresaService;
import com.thiagobernardo.pontointeligente.api.services.FuncionarioService;
import com.thiagobernardo.pontointeligente.api.utils.PasswordUtils;

/**
 * @author thiago
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(secure = false)
@ActiveProfiles("test")
public class CadastroPFControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private FuncionarioService funcionarioService;

	@MockBean
	private EmpresaService empresaService;
	
	private static final String CADASTRAR_PF_URL = "/api/cadastrar-pf";
	private static final Long ID = Long.valueOf(1);
	private static final Long ID_FUNCIONARIO = Long.valueOf(1);
	private static final String CNPJ = "51463645000100";
	private static final String VALOR_HORA = "50.0";
	private static final String NOME = "Fulano";
	private static final String CPF = "55437611200";
	private static final String EMAIL = "fulano@dedeus.com";

	@Test
	@WithMockUser
	public void testCadastrar_isValido() throws Exception {
		BDDMockito.given(this.empresaService.buscarPorCnpj(Mockito.anyString())).willReturn(Optional.of(this.obterDadosEmpresa()));
		BDDMockito.given(this.funcionarioService.persistir(Mockito.any(Funcionario.class))).willReturn(this.obterDadosFuncionario());
		String request = this.obterCadastroPFRequest();

		mvc.perform(MockMvcRequestBuilders.post(CADASTRAR_PF_URL)
				.content(request)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(ID_FUNCIONARIO))
				.andExpect(jsonPath("$.data.nome").value(NOME))
				.andExpect(jsonPath("$.data.email").value(EMAIL))
				.andExpect(jsonPath("$.data.senha").value(nullValue()))
				.andExpect(jsonPath("$.data.cpf").value(CPF))
				.andExpect(jsonPath("$.data.valorHora").value(VALOR_HORA))
				.andExpect(jsonPath("$.data.qtdHorasTrabalhoDia").value(nullValue()))
				.andExpect(jsonPath("$.data.qtdHorasAlmoco").value(nullValue()))
				.andExpect(jsonPath("$.data.cnpj").value(CNPJ))
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	private Empresa obterDadosEmpresa() {
		Empresa empresa = new Empresa();
		empresa.setId(ID);
		empresa.setRazaoSocial("De Deus");
		empresa.setCnpj(CNPJ);
		return empresa;
	}
	
	private Funcionario obterDadosFuncionario() {
		Funcionario funcionario = new Funcionario();
		funcionario.setId(ID_FUNCIONARIO);
		funcionario.setNome(NOME);
		funcionario.setEmail(EMAIL);
		funcionario.setCpf(CPF);
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		funcionario.setSenha(PasswordUtils.gerarBCrypt("123456"));
		funcionario.setValorHora(BigDecimal.valueOf(Float.valueOf(VALOR_HORA)));
		funcionario.setEmpresa(obterDadosEmpresa());
		
		return funcionario;
	}
	
	private String obterCadastroPFRequest() throws JsonProcessingException {
		CadastroPFDTO cadastroPFDTO = new CadastroPFDTO();
		cadastroPFDTO.setId(null);
		cadastroPFDTO.setNome(NOME);
		cadastroPFDTO.setEmail(EMAIL);
		cadastroPFDTO.setSenha("123456");
		cadastroPFDTO.setCpf(CPF);
		cadastroPFDTO.setValorHora(Optional.of(VALOR_HORA));
		cadastroPFDTO.setQtdHorasAlmoco(null);
		cadastroPFDTO.setQtdHorasTrabalhoDia(null);
		cadastroPFDTO.setCnpj(CNPJ);
		
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		return mapper.writeValueAsString(cadastroPFDTO);
	}

}
