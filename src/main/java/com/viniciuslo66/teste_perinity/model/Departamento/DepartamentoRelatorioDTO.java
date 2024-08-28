package com.viniciuslo66.teste_perinity.model.Departamento;

import lombok.Data;

@Data
public class DepartamentoRelatorioDTO {
  private String nome;
  private Long quantPessoas;
  private Long quantTarefas;

  public DepartamentoRelatorioDTO(String nome, Long quantPessoas, Long quantTarefas) {
    this.nome = nome;
    this.quantPessoas = quantPessoas;
    this.quantTarefas = quantTarefas;
  }
}
