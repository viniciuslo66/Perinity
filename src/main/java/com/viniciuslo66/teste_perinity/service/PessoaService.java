package com.viniciuslo66.teste_perinity.service;

import java.time.LocalDate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viniciuslo66.teste_perinity.model.Departamento.Departamento;
import com.viniciuslo66.teste_perinity.model.Pessoa.Pessoa;
import com.viniciuslo66.teste_perinity.model.Pessoa.PessoaDTO;
import com.viniciuslo66.teste_perinity.model.Pessoa.PessoaHorasDTO;
import com.viniciuslo66.teste_perinity.model.Pessoa.PessoaMediaHorasDTO;
import com.viniciuslo66.teste_perinity.model.Tarefa.Tarefa;
import com.viniciuslo66.teste_perinity.repository.DepartamentoRepository;
import com.viniciuslo66.teste_perinity.repository.PessoaRepository;
import com.viniciuslo66.teste_perinity.repository.TarefaRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class PessoaService {

  @Autowired
  PessoaRepository repository;
  @Autowired
  DepartamentoRepository departamentoRepository;
  @Autowired
  TarefaRepository tarefaRepository;
  @Autowired
  DepartamentoService departamentoService;

  public PessoaService(PessoaRepository repository) {
    this.repository = repository;
  }

  public List<PessoaHorasDTO> listarPessoasHoras() {
    List<Pessoa> pessoas = repository.findAll();

    return pessoas.stream().map(pessoa -> {
      PessoaHorasDTO dto = new PessoaHorasDTO();
      dto.setNome(pessoa.getNome());
      dto.setDepartamento(pessoa.getDepartamento().getNome());

      int totalHoras = pessoa.getTarefas().stream().mapToInt(Tarefa::getDuracao).sum();

      dto.setTotalHoras(totalHoras);
      return dto;
    }).collect(Collectors.toList());
  }

  public List<PessoaMediaHorasDTO> buscarPessoaNomeEPeriodo(String nome, LocalDate inicio, LocalDate fim) {
    List<Pessoa> pessoas = repository.findByNomeContainingIgnoreCase(nome);

    return pessoas.stream()
        .map(pessoa -> {

          List<Tarefa> tarefasFiltradas = pessoa.getTarefas().stream()
              .filter(tarefa -> !tarefa.getPrazo().isBefore(inicio) && !tarefa.getPrazo().isAfter(fim))
              .collect(Collectors.toList());

          double media = tarefasFiltradas.stream().mapToInt(Tarefa::getDuracao).average().orElse(0.0);

          PessoaMediaHorasDTO dto = new PessoaMediaHorasDTO();
          dto.setNome(pessoa.getNome());
          dto.setHoras(media);
          return dto;

        }).collect(Collectors.toList());
  }

  public Pessoa findById(Long id) {
    return repository.findById(id).orElse(null);
  }

  @Transactional
  public Pessoa savePessoa(PessoaDTO dto) {
    Pessoa pessoa = converter(dto, null);
    return repository.save(pessoa);
  }

  @Transactional
  public Pessoa uptadPessoa(Long id, PessoaDTO dto) {
    Optional<Pessoa> optional = repository.findById(id);

    if (!optional.isPresent()) {
      throw new EntityNotFoundException("Pessoa não encontada");
    }

    Pessoa pessoa = converter(dto, optional);
    return repository.save(pessoa);
  }

  @Transactional
  public void deletePessoa(Long id) {
    Optional<Pessoa> optional = repository.findById(id);
    Pessoa pessoa = optional.orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada"));

    repository.delete(pessoa);
  }

  public Pessoa converter(PessoaDTO dto, Optional<Pessoa> optional) {
    Pessoa pessoa = Objects.nonNull(optional) ? optional.get() : new Pessoa();

    pessoa.setNome(dto.getNome());

    if (dto.getDepartamento() != null) {
      Departamento departamento = departamentoRepository.findById(dto.getDepartamento())
          .orElseThrow(
              () -> new EntityNotFoundException("Departamento não encontrado com ID: " + dto.getDepartamento()));
      pessoa.setDepartamento(departamento);
    }

    if (dto.getTarefaDTOs() != null) {
      List<Tarefa> tarefas = dto.getTarefaDTOs().stream()
          .map(tarefaId -> tarefaRepository.findById(tarefaId)
              .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com ID: " + tarefaId)))
          .collect(Collectors.toList());
      pessoa.setTarefas(tarefas);
    } else {
      pessoa.setTarefas(null);
    }

    return pessoa;
  }
}