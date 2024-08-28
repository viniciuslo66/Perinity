package com.viniciuslo66.teste_perinity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.viniciuslo66.teste_perinity.Error.RegraNegocioException;
import com.viniciuslo66.teste_perinity.controller.TarefaController;
import com.viniciuslo66.teste_perinity.model.Departamento.Departamento;
import com.viniciuslo66.teste_perinity.model.Pessoa.Pessoa;
import com.viniciuslo66.teste_perinity.model.Tarefa.Tarefa;
import com.viniciuslo66.teste_perinity.model.Tarefa.TarefaDTO;
import com.viniciuslo66.teste_perinity.service.TarefaService;

@WebMvcTest(TarefaController.class)
public class TarefaControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TarefaService tarefaService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void shouldCreateTarefa() throws Exception {
    TarefaDTO tarefaDTO = new TarefaDTO();
    tarefaDTO.setTitulo("Nova Tarefa");
    tarefaDTO.setDescricao("Descrição da nova tarefa");
    tarefaDTO.setPrazo(LocalDate.of(2024, 9, 3)); 
    tarefaDTO.setDuracao(4);
    tarefaDTO.setDepartamento(1L); // ID do departamento
    tarefaDTO.setPessoa(2L); // ID da pessoa
    tarefaDTO.setFinalizado(false);

    Tarefa tarefaCriada = new Tarefa();
    tarefaCriada.setId(1L);
    tarefaCriada.setTitulo("Nova Tarefa");
    tarefaCriada.setDescricao("Descrição da nova tarefa");
    tarefaCriada.setPrazo(LocalDate.of(2024, 9, 3));
    tarefaCriada.setDuracao(4);
    tarefaCriada.setFinalizado(false);

    when(tarefaService.saveTarefa(any(TarefaDTO.class))).thenReturn(tarefaCriada);

    mockMvc.perform(post("/post/tarefas")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(tarefaDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.titulo").value("Nova Tarefa"))
        .andExpect(jsonPath("$.descricao").value("Descrição da nova tarefa"))
        .andExpect(jsonPath("$.prazo[0]").value(2024))
        .andExpect(jsonPath("$.prazo[1]").value(9))
        .andExpect(jsonPath("$.prazo[2]").value(3))
        .andExpect(jsonPath("$.duracao").value(4))
        .andExpect(jsonPath("$.finalizado").value(false));

    verify(tarefaService).saveTarefa(any(TarefaDTO.class));
  }

  @Test
  public void shouldAllocatePessoaToTarefa() throws Exception {
    Long tarefaId = 1L;
    Long pessoaId = 2L;

    Departamento departamento = new Departamento();
    departamento.setId(1L);
    departamento.setNome("Departamento 1");

    Tarefa tarefaExistente = new Tarefa();
    tarefaExistente.setId(tarefaId);
    tarefaExistente.setTitulo("Tarefa 1");
    tarefaExistente.setDepartamento(departamento);

    Pessoa pessoaExistente = new Pessoa();
    pessoaExistente.setId(pessoaId);
    pessoaExistente.setNome("Pessoa 1");
    pessoaExistente.setDepartamento(departamento);

    Tarefa tarefaAtualizada = new Tarefa();
    tarefaAtualizada.setId(tarefaId);
    tarefaAtualizada.setTitulo("Tarefa 1");
    tarefaAtualizada.setDepartamento(departamento);
    tarefaAtualizada.setPessoa(pessoaExistente);

    when(tarefaService.findById(tarefaId)).thenReturn(tarefaExistente);
    when(tarefaService.uptadeTarefa(eq(tarefaId), eq(pessoaId))).thenReturn(tarefaAtualizada);

    mockMvc.perform(put("/put/tarefas/alocar/{id}", tarefaId)
        .param("idPessoa", pessoaId.toString())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(tarefaId))
        .andExpect(jsonPath("$.titulo").value("Tarefa 1"))
        .andExpect(jsonPath("$.pessoa.id").value(pessoaId))
        .andExpect(jsonPath("$.pessoa.nome").value("Pessoa 1"));

    verify(tarefaService).findById(tarefaId);
    verify(tarefaService).uptadeTarefa(eq(tarefaId), eq(pessoaId));
  }

  @Test
  public void shouldReturnBadRequestWhenPessoaNotInSameDepartamento() throws Exception {
    Long tarefaId = 1L;
    Long pessoaId = 2L;

    Departamento departamentoTarefa = new Departamento();
    departamentoTarefa.setId(1L);
    departamentoTarefa.setNome("Departamento 1");

    Departamento departamentoPessoa = new Departamento();
    departamentoPessoa.setId(2L);
    departamentoPessoa.setNome("Departamento 2");

    Tarefa tarefaExistente = new Tarefa();
    tarefaExistente.setId(tarefaId);
    tarefaExistente.setTitulo("Tarefa 1");
    tarefaExistente.setDepartamento(departamentoTarefa);

    Pessoa pessoaExistente = new Pessoa();
    pessoaExistente.setId(pessoaId);
    pessoaExistente.setNome("Pessoa 1");
    pessoaExistente.setDepartamento(departamentoPessoa);

    when(tarefaService.findById(tarefaId)).thenReturn(tarefaExistente);
    when(tarefaService.uptadeTarefa(eq(tarefaId), eq(pessoaId)))
        .thenThrow(new RegraNegocioException("A pessoa e a tarefa pertencem a departamentos diferentes"));

    mockMvc.perform(put("/put/tarefas/alocar/{id}", tarefaId)
        .param("idPessoa", pessoaId.toString())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    verify(tarefaService).findById(tarefaId);
    verify(tarefaService).uptadeTarefa(eq(tarefaId), eq(pessoaId));
  }

  @Test
  public void shouldFinalizeTarefa() throws Exception {
    Long tarefaId = 1L;

    Tarefa tarefaExistente = new Tarefa();
    tarefaExistente.setId(tarefaId);
    tarefaExistente.setTitulo("Tarefa 1");
    tarefaExistente.setFinalizado(false);

    Tarefa tarefaFinalizada = new Tarefa();
    tarefaFinalizada.setId(tarefaId);
    tarefaFinalizada.setTitulo("Tarefa 1");
    tarefaFinalizada.setFinalizado(true);

    when(tarefaService.findById(tarefaId)).thenReturn(tarefaExistente);
    when(tarefaService.finalizarTarefa(tarefaId)).thenReturn(tarefaFinalizada);

    mockMvc.perform(put("/put/tarefas/finalizar/{id}", tarefaId)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(tarefaId))
        .andExpect(jsonPath("$.titulo").value("Tarefa 1"))
        .andExpect(jsonPath("$.finalizado").value(true));

    verify(tarefaService).findById(tarefaId);
    verify(tarefaService).finalizarTarefa(tarefaId);
  }

  @Test
  public void shouldReturnBadRequestWhenTarefaNotFound() throws Exception {
    Long tarefaId = 1L;

    when(tarefaService.findById(tarefaId)).thenReturn(null);

    mockMvc.perform(put("/put/tarefas/finalizar/{id}", tarefaId)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest()); 

    verify(tarefaService).findById(tarefaId);
  }

  @Test
  public void shouldReturnPendentesWithOldestPrazos() throws Exception {
    Tarefa tarefa1 = new Tarefa();
    tarefa1.setId(1L);
    tarefa1.setTitulo("Tarefa 1");
    tarefa1.setDescricao("Descrição da Tarefa 1");
    tarefa1.setPrazo(LocalDate.of(2024, 1, 10));
    tarefa1.setDuracao(4);

    Tarefa tarefa2 = new Tarefa();
    tarefa2.setId(2L);
    tarefa2.setTitulo("Tarefa 2");
    tarefa2.setDescricao("Descrição da Tarefa 2");
    tarefa2.setPrazo(LocalDate.of(2024, 1, 5));
    tarefa2.setDuracao(3);

    Tarefa tarefa3 = new Tarefa();
    tarefa3.setId(3L);
    tarefa3.setTitulo("Tarefa 3");
    tarefa3.setDescricao("Descrição da Tarefa 3");
    tarefa3.setPrazo(LocalDate.of(2024, 1, 1));
    tarefa3.setDuracao(2);

    List<Tarefa> tarefasPendentes = Arrays.asList(tarefa3, tarefa2, tarefa1);

    when(tarefaService.tarefasSemPessoas()).thenReturn(tarefasPendentes);

    mockMvc.perform(get("/get/tarefas/pendentes")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(3L))
        .andExpect(jsonPath("$[0].titulo").value("Tarefa 3"))
        .andExpect(jsonPath("$[0].prazo[0]").value(2024))
        .andExpect(jsonPath("$[0].prazo[1]").value(1))
        .andExpect(jsonPath("$[0].prazo[2]").value(1))
        .andExpect(jsonPath("$[1].id").value(2L))
        .andExpect(jsonPath("$[1].titulo").value("Tarefa 2"))
        .andExpect(jsonPath("$[1].prazo[0]").value(2024))
        .andExpect(jsonPath("$[1].prazo[1]").value(1))
        .andExpect(jsonPath("$[1].prazo[2]").value(5))
        .andExpect(jsonPath("$[2].id").value(1L))
        .andExpect(jsonPath("$[2].titulo").value("Tarefa 1"))
        .andExpect(jsonPath("$[2].prazo[0]").value(2024))
        .andExpect(jsonPath("$[2].prazo[1]").value(1))
        .andExpect(jsonPath("$[2].prazo[2]").value(10));
        
    verify(tarefaService).tarefasSemPessoas();
  }
}
