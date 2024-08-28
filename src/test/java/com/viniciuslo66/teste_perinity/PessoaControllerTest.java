package com.viniciuslo66.teste_perinity;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viniciuslo66.teste_perinity.controller.PessoaController;
import com.viniciuslo66.teste_perinity.model.Pessoa.Pessoa;
import com.viniciuslo66.teste_perinity.model.Pessoa.PessoaDTO;
import com.viniciuslo66.teste_perinity.model.Pessoa.PessoaHorasDTO;
import com.viniciuslo66.teste_perinity.model.Pessoa.PessoaMediaHorasDTO;
import com.viniciuslo66.teste_perinity.service.PessoaService;

@WebMvcTest(PessoaController.class)
public class PessoaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PessoaService pessoaService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void shouldCreatePessoa() throws Exception {
    PessoaDTO pessoaDTO = new PessoaDTO();
    pessoaDTO.setNome("Vinicius");
    pessoaDTO.setDepartamento(1L);
    pessoaDTO.setTarefaDTOs(null);

    Pessoa pessoa = new Pessoa();
    pessoa.setId(1L);
    pessoa.setNome("Vinicius");

    when(pessoaService.savePessoa(any(PessoaDTO.class))).thenReturn(pessoa);

    mockMvc.perform(post("/post/pessoas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(pessoaDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.nome").value("Vinicius"));

    verify(pessoaService).savePessoa(any(PessoaDTO.class));
  }

  @Test
  public void shouldUpdatePessoa() throws Exception {
    Long pessoaId = 1L;

    PessoaDTO pessoaDTO = new PessoaDTO();
    pessoaDTO.setNome("Vinicius Atualizado");
    pessoaDTO.setDepartamento(1L);
    pessoaDTO.setTarefaDTOs(null);

    Pessoa pessoaExistente = new Pessoa();
    pessoaExistente.setId(pessoaId);
    pessoaExistente.setNome("Vinicius");

    Pessoa pessoaAtualizada = new Pessoa();
    pessoaAtualizada.setId(pessoaId);
    pessoaAtualizada.setNome("Vinicius Atualizado");

    when(pessoaService.findById(pessoaId)).thenReturn(pessoaExistente);
    when(pessoaService.uptadPessoa(eq(pessoaId), any(PessoaDTO.class))).thenReturn(pessoaAtualizada);

    mockMvc.perform(put("/put/pessoas/{id}", pessoaId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(pessoaDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(pessoaId))
        .andExpect(jsonPath("$.nome").value("Vinicius Atualizado"));

    verify(pessoaService).findById(pessoaId);
    verify(pessoaService).uptadPessoa(eq(pessoaId), any(PessoaDTO.class));
  }

  @Test
  public void shouldDeletePessoa() throws Exception {
    Long pessoaId = 1L;

    Pessoa pessoaExistente = new Pessoa();
    pessoaExistente.setId(pessoaId);
    pessoaExistente.setNome("Vinicius");

    when(pessoaService.findById(pessoaId)).thenReturn(pessoaExistente);
    doNothing().when(pessoaService).deletePessoa(pessoaId);

    mockMvc.perform(delete("/delete/pessoas/{id}", pessoaId)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(pessoaService).findById(pessoaId);
    verify(pessoaService).deletePessoa(pessoaId);
  }

  @Test
  public void shouldReturnNotFoundWhenPessoaDoesNotExist() throws Exception {
    Long pessoaId = 1L;

    when(pessoaService.findById(pessoaId)).thenReturn(null);

    mockMvc.perform(delete("/delete/pessoas/{id}", pessoaId)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    verify(pessoaService).findById(pessoaId);
  }

  @Test
  public void shouldListPessoasWithTotalHoras() throws Exception {
    // Configuração dos dados simulados
    PessoaHorasDTO pessoa1 = new PessoaHorasDTO();
    pessoa1.setNome("Vinicius");
    pessoa1.setDepartamento("Departamento 1");
    pessoa1.setTotalHoras(10);

    PessoaHorasDTO pessoa2 = new PessoaHorasDTO();
    pessoa2.setNome("Maria Souza");
    pessoa2.setDepartamento("Departamento 2");
    pessoa2.setTotalHoras(15);

    List<PessoaHorasDTO> pessoas = Arrays.asList(pessoa1, pessoa2);

    when(pessoaService.listarPessoasHoras()).thenReturn(pessoas);

    mockMvc.perform(get("/get/pessoas")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].nome").value("Vinicius"))
        .andExpect(jsonPath("$[0].departamento").value("Departamento 1"))
        .andExpect(jsonPath("$[0].totalHoras").value(10))
        .andExpect(jsonPath("$[1].nome").value("Maria Souza"))
        .andExpect(jsonPath("$[1].departamento").value("Departamento 2"))
        .andExpect(jsonPath("$[1].totalHoras").value(15));

    verify(pessoaService).listarPessoasHoras();
  }

  @Test
  public void shouldReturnMediaHorasByNomeAndPeriodo() throws Exception {
    String nome = "Vinicius";
    LocalDate dataInicio = LocalDate.of(2024, 1, 1);
    LocalDate dataFim = LocalDate.of(2024, 12, 31);

    PessoaMediaHorasDTO pessoa1 = new PessoaMediaHorasDTO();
    pessoa1.setNome("Vinicius");
    pessoa1.setHoras(5.0);

    PessoaMediaHorasDTO pessoa2 = new PessoaMediaHorasDTO();
    pessoa2.setNome("Carol");
    pessoa2.setHoras(7.5);

    List<PessoaMediaHorasDTO> pessoas = Arrays.asList(pessoa1, pessoa2);

    when(pessoaService.buscarPessoaNomeEPeriodo(nome, dataInicio, dataFim)).thenReturn(pessoas);

    mockMvc.perform(get("/get/pessoas/gastos")
        .param("nome", nome)
        .param("dataInicio", dataInicio.toString())
        .param("dataFim", dataFim.toString())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].nome").value("Vinicius"))
        .andExpect(jsonPath("$[0].horas").value(5.0))
        .andExpect(jsonPath("$[1].nome").value("Carol"))
        .andExpect(jsonPath("$[1].horas").value(7.5));

    verify(pessoaService).buscarPessoaNomeEPeriodo(nome, dataInicio, dataFim);
  }
}