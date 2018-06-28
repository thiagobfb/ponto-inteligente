/**
 * 
 */
package com.thiagobernardo.pontointeligente.api.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.fasterxml.jackson.databind.ObjectMapper;
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
	private static final String CNPJ = "51463645000100";
	private static final String VALOR_HORA = "50";
	private static final String NOME = "Fulano";
	private static final String CPF = "55437611200";
	private static final String EMAIL = "fulano@dedeus.com";

	@Test
	@WithMockUser
	public void testCadastrar_isValido() throws Exception {
		BDDMockito.given(this.funcionarioService.persistir(Mockito.any(Funcionario.class))).willReturn(this.obterDadosFuncionario());
		BDDMockito.given(this.empresaService.buscarPorCnpj(Mockito.anyString())).willReturn(Optional.of(this.obterDadosEmpresa()));
		CadastroPFDTO retorno = this.obterCadastroPFRetorno();
		
		mvc.perform(MockMvcRequestBuilders.post(CADASTRAR_PF_URL)
				.content(asJsonString(retorno))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
//				.andExpect(jsonPath("$.data.id").value(nullValue()))
//				.andExpect(jsonPath("$.data.nome", equalTo(retorno.getNome())))
//				.andExpect(jsonPath("$.data.email", equalTo(retorno.getEmail())))
//				.andExpect(jsonPath("$.data.senha").value(nullValue()))
//				.andExpect(jsonPath("$.data.cpf", equalTo(retorno.getCpf())))
//				.andExpect(jsonPath("$.data.valorHora").value(nullValue()))
//				.andExpect(jsonPath("$.data.qtdHorasTrabalhoDia").value(nullValue()))
//				.andExpect(jsonPath("$.data.qtdHorasAlmoco").value(nullValue()))
//				.andExpect(jsonPath("$.data.cnpj", equalTo(retorno.getCnpj())))
//				.andExpect(jsonPath("$.errors").isEmpty())
//				.andReturn();
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
		funcionario.setNome(NOME);
		funcionario.setEmail(EMAIL);
		funcionario.setCpf(CPF);
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		funcionario.setSenha(PasswordUtils.gerarBCrypt("123456"));
		funcionario.setQtdHorasAlmoco(Float.valueOf(VALOR_HORA));
		
		return funcionario;
	}
	
	private CadastroPFDTO obterCadastroPFRetorno() {
		CadastroPFDTO cadastroPFDTO = new CadastroPFDTO();
//		cadastroPFDTO.setId(ID);
		cadastroPFDTO.setNome(NOME);
		cadastroPFDTO.setEmail(EMAIL);
		cadastroPFDTO.setSenha("123456");
		cadastroPFDTO.setCpf(CPF);
//		cadastroPFDTO.setValorHora(Optional.of(VALOR_HORA));
		cadastroPFDTO.setCnpj(CNPJ);
		
		return cadastroPFDTO;
	}
	
	public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
