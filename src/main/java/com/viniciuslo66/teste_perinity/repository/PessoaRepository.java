package com.viniciuslo66.teste_perinity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.viniciuslo66.teste_perinity.model.Pessoa.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

  Pessoa findByNome(String pessoa);

  List<Pessoa> findByNomeContainingIgnoreCase(String nome);
  
}
