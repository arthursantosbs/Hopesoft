package com.hopesoft.service;

import com.hopesoft.exception.UsuarioNotFoundException;
import com.hopesoft.model.Perfil;
import com.hopesoft.model.Usuario;
import com.hopesoft.repository.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioServiceHelper {

    private final UsuarioRepository usuarioRepository;

    public Usuario buscarUsuarioDaEmpresa(Long usuarioId, Long empresaId) {
        return usuarioRepository.findByIdAndEmpresa_Id(usuarioId, empresaId)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario nao encontrado para a empresa informada"));
    }

    public List<Usuario> listarAdminsDaEmpresa(Long empresaId) {
        return usuarioRepository.findByEmpresa_IdAndPerfil(empresaId, Perfil.ADMIN);
    }
}
