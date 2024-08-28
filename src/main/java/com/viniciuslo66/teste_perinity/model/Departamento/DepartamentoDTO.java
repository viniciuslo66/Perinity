package com.viniciuslo66.teste_perinity.model.Departamento;

import java.util.List;

import lombok.Data;

@Data
public class DepartamentoDTO {

  private Long id;
  private String nome;
  private List<Long> pessoas;

}
