package com.viniciuslo66.teste_perinity.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.viniciuslo66.teste_perinity.Error.RegraNegocioException;
import com.viniciuslo66.teste_perinity.model.Pessoa.Pessoa;
import com.viniciuslo66.teste_perinity.model.Pessoa.PessoaDTO;
import com.viniciuslo66.teste_perinity.model.Pessoa.PessoaHorasDTO;
import com.viniciuslo66.teste_perinity.model.Pessoa.PessoaMediaHorasDTO;
import com.viniciuslo66.teste_perinity.service.PessoaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class PessoaController {
  private final PessoaService service;

  @GetMapping("get/pessoas")
  public ResponseEntity<List<PessoaHorasDTO>> listar() {
    List<PessoaHorasDTO> pessoaHorasDTOs = service.listarPessoasHoras();
    return ResponseEntity.ok().body(pessoaHorasDTOs);
  }

  @GetMapping("get/pessoas/gastos")
  public ResponseEntity<List<PessoaMediaHorasDTO>> buscarPessoasPorNomeEPeriodo(
      @RequestParam String nome,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

    List<PessoaMediaHorasDTO> pessoasMediaHoras = service.buscarPessoaNomeEPeriodo(nome, dataInicio, dataFim);
    return ResponseEntity.ok().body(pessoasMediaHoras);
  }

  @PostMapping("post/pessoas")
  public ResponseEntity<?> savePessoa(@RequestBody PessoaDTO dto) {
    try {
      Pessoa pessoa = service.savePessoa(dto);
      return new ResponseEntity<>(pessoa, HttpStatus.CREATED);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PutMapping("put/pessoas/{id}")
  public ResponseEntity<?> updatePessoa(@PathVariable("id") Long id, @RequestBody PessoaDTO dto) {
    try {
      Pessoa pessoa = service.findById(id);
      if (pessoa != null) {
        pessoa = service.uptadPessoa(id, dto);
        return ResponseEntity.ok().body(pessoa);
      } else {
        throw new RegraNegocioException("Pessoa com ID " + id + " não encontrada");
      }
    } catch (RegraNegocioException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @DeleteMapping("delete/pessoas/{id}")
  public ResponseEntity<?> deletePessoa(@PathVariable("id") Long id) {
    try {
      Pessoa pessoa = service.findById(id);
      if (pessoa != null) {
        service.deletePessoa(pessoa.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      } else {
        throw new RegraNegocioException("Pessoa com ID" + id + " não encontrada");
      }
    } catch (RegraNegocioException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

}
