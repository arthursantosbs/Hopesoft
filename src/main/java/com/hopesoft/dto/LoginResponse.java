package com.hopesoft.dto;

public record LoginResponse(
        String token,
        String tipoToken,
        long expiraEmSegundos,
        AuthenticatedUserResponse usuario
) {
}
