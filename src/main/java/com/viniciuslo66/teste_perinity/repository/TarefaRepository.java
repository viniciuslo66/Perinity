package com.viniciuslo66.teste_perinity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.viniciuslo66.teste_perinity.model.Tarefa.Tarefa;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

}