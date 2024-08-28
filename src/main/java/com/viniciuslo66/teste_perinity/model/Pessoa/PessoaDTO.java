package com.viniciuslo66.teste_perinity.model.Pessoa;

import java.util.List;

import lombok.Data;

@Data
public class PessoaDTO {
  private Long id;
  private String nome;
  private Long departamento;
  private List<Long> tarefaDTOs;
}
