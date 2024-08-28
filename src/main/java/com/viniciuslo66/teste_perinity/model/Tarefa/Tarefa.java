package com.viniciuslo66.teste_perinity.model.Tarefa;

import java.time.LocalDate;

import com.viniciuslo66.teste_perinity.model.Departamento.Departamento;
import com.viniciuslo66.teste_perinity.model.Pessoa.Pessoa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Tarefa {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 2, max = 100)
  private String titulo;

  @NotNull
  @Size(min = 2, max = 1000)
  private String descricao;

  @NotNull
  private LocalDate prazo;

  @NotNull
  private int duracao;

  @ManyToOne
  @JoinColumn(name = "departamento_id")
  private Departamento departamento;

  @ManyToOne
  @JoinColumn(name = "pessoa_id")
  private Pessoa pessoa;

  @NotNull
  private boolean finalizado;
}
