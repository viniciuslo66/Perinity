package com.viniciuslo66.teste_perinity.model.Departamento;

import java.util.List;

import com.viniciuslo66.teste_perinity.model.Pessoa.Pessoa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Departamento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Size(min = 2, max = 50)
  private String nome;

  @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL)
  private List<Pessoa> pessoas;
}
