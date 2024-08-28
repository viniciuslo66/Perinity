package com.viniciuslo66.teste_perinity.model.Pessoa;

import java.util.List;

import com.viniciuslo66.teste_perinity.model.Departamento.Departamento;
import com.viniciuslo66.teste_perinity.model.Tarefa.Tarefa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Pessoa {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 2, max = 100)
  private String nome;

  @ManyToOne
  @JoinColumn(name = "departamento_id")
  private Departamento departamento;

  @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL)
  private List<Tarefa> tarefas;
}
