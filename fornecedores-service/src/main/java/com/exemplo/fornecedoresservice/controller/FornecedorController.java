package com.exemplo.fornecedoresservice.controller;

import com.exemplo.fornecedoresservice.dto.ProdutoDTO;
import com.exemplo.fornecedoresservice.model.Fornecedor;
import com.exemplo.fornecedoresservice.service.FornecedorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/fornecedores")
public class FornecedorController {

    private final FornecedorService service;

    public FornecedorController(FornecedorService service) {
        this.service = service;
    }

    @GetMapping
    public List<Fornecedor> listarTodos() {
        return service.listarTodos();
    }

        @GetMapping("/{id}")
    public ResponseEntity<Fornecedor> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Fornecedor> criar(@RequestBody Fornecedor fornecedor) {
        Fornecedor salvo = service.salvar(fornecedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/produtos")
    public ResponseEntity<List<ProdutoDTO>> listarProdutos() {
        return ResponseEntity.ok(service.listarProdutos());
    }

}
