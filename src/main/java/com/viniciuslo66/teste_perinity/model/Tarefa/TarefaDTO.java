package com.viniciuslo66.teste_perinity.model.Tarefa;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TarefaDTO {

  private Long id;
  private String titulo;
  private String descricao;
  private LocalDate prazo;
  private int duracao;
  private Long departamento;
  private Long pessoa;
  private boolean finalizado;

}
