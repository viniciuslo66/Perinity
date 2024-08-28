package com.viniciuslo66.teste_perinity.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viniciuslo66.teste_perinity.Error.RegraNegocioException;
import com.viniciuslo66.teste_perinity.model.Tarefa.Tarefa;
import com.viniciuslo66.teste_perinity.model.Tarefa.TarefaDTO;
import com.viniciuslo66.teste_perinity.service.TarefaService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class TarefaController {
  private final TarefaService service;

  @GetMapping("get/tarefas/pendentes")
  public ResponseEntity<List<Tarefa>> pendentes() {
      List<Tarefa> tarefas = service.tarefasSemPessoas();
      return ResponseEntity.ok().body(tarefas);
  }

  @PostMapping("post/tarefas")
  public ResponseEntity<?> salvar(@RequestBody TarefaDTO dto) {
    try {
      Tarefa tarefa = service.saveTarefa(dto);
      return new ResponseEntity<>(tarefa, HttpStatus.CREATED);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PutMapping("put/tarefas/alocar/{id}")
  public ResponseEntity<?> alocarPessoa(@PathVariable("id") Long id, @RequestParam Long idPessoa) {
    try {
      Tarefa tarefa = service.findById(id);
      if (tarefa != null) {
        tarefa = service.uptadeTarefa(id, idPessoa);
        return ResponseEntity.ok().body(tarefa);
      } else {
        throw new RegraNegocioException("Tarefa com ID" + id + " não encontrada");
      }
    } catch (RegraNegocioException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PutMapping("put/tarefas/finalizar/{id}")
  public ResponseEntity<?> finalizado(@PathVariable("id") Long id) {
    try {
      Tarefa tarefa = service.findById(id);
      if (tarefa != null) {
        tarefa = service.finalizarTarefa(id);
        return ResponseEntity.ok().body(tarefa);
      } else {
        throw new RegraNegocioException("Tarefa com ID" + id + " não encontrada");
      }
    } catch (RegraNegocioException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

}
