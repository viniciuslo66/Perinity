package com.viniciuslo66.teste_perinity.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viniciuslo66.teste_perinity.model.Departamento.Departamento;
import com.viniciuslo66.teste_perinity.model.Pessoa.Pessoa;
import com.viniciuslo66.teste_perinity.model.Tarefa.Tarefa;
import com.viniciuslo66.teste_perinity.model.Tarefa.TarefaDTO;
import com.viniciuslo66.teste_perinity.repository.DepartamentoRepository;
import com.viniciuslo66.teste_perinity.repository.PessoaRepository;
import com.viniciuslo66.teste_perinity.repository.TarefaRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class TarefaService {

  @Autowired
  TarefaRepository repository;

  @Autowired
  DepartamentoRepository departamentoRepository;

  @Autowired
  PessoaRepository pessoaRepository;

  public TarefaService(TarefaRepository repository) {
    this.repository = repository;
  }

  public Tarefa findById(Long id) {
    return repository.findById(id).orElse(null);
  }

  public List<Tarefa> tarefasSemPessoas() {
    List<Tarefa> todasTarefas = repository.findAll();

    return todasTarefas.stream()
        .filter(tarefa -> tarefa.getPessoa() == null)
        .sorted(Comparator.comparing(Tarefa::getPrazo))
        .limit(3)
        .collect(Collectors.toList());
  }

  @Transactional
  public Tarefa saveTarefa(TarefaDTO dto) {
    Tarefa tarefa = converter(dto, Optional.empty());
    System.out.println(tarefa.getPrazo());
    System.out.println(dto.getPrazo());
    return repository.save(tarefa);
  }

  @Transactional
  public Tarefa uptadeTarefa(Long id, Long pessoaId) {
    Optional<Tarefa> optional = repository.findById(id);

    if (!optional.isPresent()) {
      throw new EntityNotFoundException("Tarefa não encontrada");
    }

    Tarefa tarefa = optional.get();
    Optional<Pessoa> optionalPessoa = pessoaRepository.findById(pessoaId);

    if (!optionalPessoa.isPresent()) {
      throw new EntityNotFoundException("Pessoa não encontrada");
    }

    Pessoa pessoa = optionalPessoa.get();

    if (!tarefa.getDepartamento().getId().equals(pessoa.getDepartamento().getId())) {
      throw new IllegalArgumentException("A pessoa e a tarefa pertencem a departamentos diferentes");
    }

    tarefa.setPessoa(pessoa);
    return repository.save(tarefa);
  }

  @Transactional
  public Tarefa finalizarTarefa(Long id) {
    Optional<Tarefa> optional = repository.findById(id);
    Tarefa tarefa = optional.get();
    tarefa.setFinalizado(true);
    return repository.save(tarefa);
  }

  public Tarefa converter(TarefaDTO dto, Optional<Tarefa> optional) {
    Tarefa tarefa = optional.orElseGet(Tarefa::new);

    tarefa.setTitulo(dto.getTitulo());
    tarefa.setDescricao(dto.getDescricao());
    tarefa.setPrazo(dto.getPrazo());
    tarefa.setDuracao(dto.getDuracao());
    tarefa.setFinalizado(dto.isFinalizado());

    if (dto.getDepartamento() != null) {
      Departamento departamento = departamentoRepository.findById(dto.getDepartamento())
          .orElseThrow(
              () -> new EntityNotFoundException("Departamento não encontrado com ID: " + dto.getDepartamento()));
      tarefa.setDepartamento(departamento);
    }

    if (dto.getPessoa() != null) {
      Pessoa pessoa = pessoaRepository.findById(dto.getPessoa())
          .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada com ID: " + dto.getPessoa()));
      tarefa.setPessoa(pessoa);
    }

    return tarefa;
  }
}
