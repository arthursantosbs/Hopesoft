package com.hopesoft.dto;

public record AuthenticatedUserResponse(
        Long id,
        Long empresaId,
        String nome,
        String email,
        String perfil
) {
}
