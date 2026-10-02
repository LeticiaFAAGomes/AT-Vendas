package com.exemplo.fornecedoresservice.config;

import com.exemplo.fornecedoresservice.model.Fornecedor;
import com.exemplo.fornecedoresservice.repository.FornecedorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Popula o banco H2 em memoria com clientes de teste assim que a aplicacao sobe.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final FornecedorRepository fornecedorRepository;

    public DataInitializer(FornecedorRepository FornecedorRepository) {
        this.fornecedorRepository = FornecedorRepository;
    }

    @Override
    public void run(String... args) {
fornecedorRepository.save(new Fornecedor("Tech Distribuidora", "11.111.111/0001-11"));
        fornecedorRepository.save(new Fornecedor("EmpresaA", "22.222.222/0001-22"));
        fornecedorRepository.save(new Fornecedor("EmpresaB", "33.333.333/0001-33"));
        fornecedorRepository.save(new Fornecedor("EmpresaC", "44.444.444/0001-44"));
        fornecedorRepository.save(new Fornecedor("EmpresaD", "55.555.555/0001-55"));
    }
}
