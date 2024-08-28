package com.viniciuslo66.teste_perinity.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viniciuslo66.teste_perinity.Error.RegraNegocioException;
import com.viniciuslo66.teste_perinity.model.Departamento.Departamento;
import com.viniciuslo66.teste_perinity.model.Departamento.DepartamentoDTO;
import com.viniciuslo66.teste_perinity.model.Departamento.DepartamentoRelatorioDTO;
import com.viniciuslo66.teste_perinity.service.DepartamentoService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping()
@RequiredArgsConstructor
public class DepartamentoController {
  private final DepartamentoService service;

  @GetMapping("get/departamentos")
  public ResponseEntity<List<DepartamentoRelatorioDTO>> listarDepartamentos() {
      List<DepartamentoRelatorioDTO> departamentos = service.listarDepartamentosComResumo();
      return ResponseEntity.ok().body(departamentos);
  }
  
  @PostMapping("post/departamento")
  public ResponseEntity<?> saveDepartamento(@RequestBody DepartamentoDTO dto) {
      try {
        Departamento departamento = service.saveDepartamento(dto);
        return new ResponseEntity<>(departamento, HttpStatus.CREATED);
      } catch (RegraNegocioException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
      }
  }
  
}
