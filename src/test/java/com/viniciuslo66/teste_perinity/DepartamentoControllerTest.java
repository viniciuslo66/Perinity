package com.viniciuslo66.teste_perinity;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.viniciuslo66.teste_perinity.controller.DepartamentoController;
import com.viniciuslo66.teste_perinity.model.Departamento.DepartamentoRelatorioDTO;
import com.viniciuslo66.teste_perinity.service.DepartamentoService;

@WebMvcTest(DepartamentoController.class)
public class DepartamentoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private DepartamentoService departamentoService;

  @Test
  public void shouldReturnDepartamentosWithQuantities() throws Exception {
    DepartamentoRelatorioDTO departamento1 = new DepartamentoRelatorioDTO("Departamento 1", 10L, 20L);
    DepartamentoRelatorioDTO departamento2 = new DepartamentoRelatorioDTO("Departamento 2", 5L, 15L);

    List<DepartamentoRelatorioDTO> departamentos = Arrays.asList(departamento1, departamento2);

    when(departamentoService.listarDepartamentosComResumo()).thenReturn(departamentos);

    mockMvc.perform(get("/get/departamentos")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].nome").value("Departamento 1"))
        .andExpect(jsonPath("$[0].quantPessoas").value(10))
        .andExpect(jsonPath("$[0].quantTarefas").value(20))
        .andExpect(jsonPath("$[1].nome").value("Departamento 2"))
        .andExpect(jsonPath("$[1].quantPessoas").value(5))
        .andExpect(jsonPath("$[1].quantTarefas").value(15));

    verify(departamentoService).listarDepartamentosComResumo();
  }
}
