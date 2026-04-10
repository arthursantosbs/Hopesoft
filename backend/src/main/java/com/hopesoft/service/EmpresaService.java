package com.hopesoft.service;

import com.hopesoft.exception.EmpresaNotFoundException;
import com.hopesoft.model.Empresa;
import com.hopesoft.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public Empresa buscarEmpresaPorId(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new EmpresaNotFoundException("Empresa nao encontrada"));
    }
}
