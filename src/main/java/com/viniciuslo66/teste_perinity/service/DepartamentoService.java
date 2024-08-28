package com.viniciuslo66.teste_perinity.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viniciuslo66.teste_perinity.model.Departamento.Departamento;
import com.viniciuslo66.teste_perinity.model.Departamento.DepartamentoDTO;
import com.viniciuslo66.teste_perinity.model.Departamento.DepartamentoRelatorioDTO;
import com.viniciuslo66.teste_perinity.model.Pessoa.Pessoa;
import com.viniciuslo66.teste_perinity.repository.DepartamentoRepository;
import com.viniciuslo66.teste_perinity.repository.PessoaRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class DepartamentoService {

  @Autowired
  DepartamentoRepository repository;

  @Autowired
  PessoaRepository pessoaRepository;

  public DepartamentoService(DepartamentoRepository repository) {
    this.repository = repository;
  }

  public Departamento findById(Long id) {
    return repository.findById(id).orElse(null);
  }

  public List<DepartamentoRelatorioDTO> listarDepartamentosComResumo() {
    List<Departamento> departamentos = repository.findAll();

    return departamentos.stream()
        .map(departamento -> {
          long quantPessoas = departamento.getPessoas().size();
          long quantTarefas = departamento.getPessoas().stream()
              .flatMap(pessoa -> pessoa.getTarefas().stream())
              .count();

          return new DepartamentoRelatorioDTO(departamento.getNome(), quantPessoas, quantTarefas);
        })
        .collect(Collectors.toList());
  }

  @Transactional
  public Departamento saveDepartamento(DepartamentoDTO dto) {
    Departamento departamento = converter(dto, null);
    return repository.save(departamento);
  }

  private Departamento converter(DepartamentoDTO dto, Optional<Departamento> optional) {
    Departamento departamento = Objects.nonNull(optional) ? optional.get() : new Departamento();

    departamento.setNome(dto.getNome());

    if (dto.getPessoas() != null) {
      List<Pessoa> pessoas = dto.getPessoas().stream()
          .map(pessoaId -> pessoaRepository.findById(pessoaId)
              .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada com ID: " + pessoaId)))
          .collect(Collectors.toList());
      departamento.setPessoas(pessoas);
    } else {
      departamento.setPessoas(null);
    }

    return departamento;
  }

}
