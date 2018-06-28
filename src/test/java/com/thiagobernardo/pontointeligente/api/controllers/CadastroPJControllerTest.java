/**
 * 
 */
package com.thiagobernardo.pontointeligente.api.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thiagobernardo.pontointeligente.api.dtos.CadastroPJDTO;
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
public class CadastroPJControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private FuncionarioService funcionarioService;

	@MockBean
	private EmpresaService empresaService;
	
	private static final String CADASTRAR_PJ_URL = "/api/cadastrar-pj";
	private static final Long ID = Long.valueOf(1);
	private static final String CNPJ = "51463645000100";
	private static final String RAZAO_SOCIAL = "Empresa XYZ";
	private static final String NOME = "Thiago Bernardo";
	private static final String CPF = "58656733869";
	private static final String EMAIL = "mail@mail.com";

	@Test
	@WithMockUser
	public void testCadastrar_isValido() throws Exception {
		BDDMockito.given(this.funcionarioService.persistir(Mockito.any(Funcionario.class))).willReturn(this.obterDadosFuncionario());
		BDDMockito.given(this.empresaService.persistir(Mockito.any(Empresa.class))).willReturn(this.obterDadosEmpresa());
		
		CadastroPJDTO retorno = this.obterCadastroPJRetorno();
		retorno.setSenha(PasswordUtils.gerarBCrypt("123456"));
		
		mvc.perform(MockMvcRequestBuilders.post(CADASTRAR_PJ_URL)
				.content(asJsonString(retorno))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").value(nullValue()))
				.andExpect(jsonPath("$.data.nome", equalTo(retorno.getNome())))
				.andExpect(jsonPath("$.data.email", equalTo(retorno.getEmail())))
				.andExpect(jsonPath("$.data.senha").value(nullValue()))
				.andExpect(jsonPath("$.data.cpf", equalTo(retorno.getCpf())))
				.andExpect(jsonPath("$.data.razaoSocial", equalTo(retorno.getRazaoSocial())))
				.andExpect(jsonPath("$.data.cnpj", equalTo(retorno.getCnpj())))
				.andExpect(jsonPath("$.errors").isEmpty())
				.andReturn();
	}
	
	private Funcionario obterDadosFuncionario() {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(NOME);
		funcionario.setEmail(EMAIL);
		funcionario.setCpf(CPF);
		funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
		funcionario.setSenha(PasswordUtils.gerarBCrypt("123456"));
		
		return funcionario;
	}
	
	private Empresa obterDadosEmpresa() {
		Empresa empresa = new Empresa();
		empresa.setId(ID);
		empresa.setRazaoSocial(RAZAO_SOCIAL);
		empresa.setCnpj(CNPJ);
		return empresa;
	}
	
	private CadastroPJDTO obterCadastroPJRetorno() {
		CadastroPJDTO cadastroPJDTO = new CadastroPJDTO();
		cadastroPJDTO.setId(ID);
		cadastroPJDTO.setNome(NOME);
		cadastroPJDTO.setEmail(EMAIL);
		cadastroPJDTO.setCpf(CPF);
		cadastroPJDTO.setRazaoSocial(RAZAO_SOCIAL);
		cadastroPJDTO.setCnpj(CNPJ);
		
		return cadastroPJDTO;
	}
	
	public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
