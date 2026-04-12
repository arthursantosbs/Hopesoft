package com.hopesoft.service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class CodigoBarrasService {

    private final AtomicInteger sequence = new AtomicInteger(100);

    public String gerarEan13(String referenciaBase) {
        String digits = referenciaBase == null ? "" : referenciaBase.replaceAll("\\D", "");
        String timestamp = String.valueOf(Math.abs(Instant.now().toEpochMilli()));
        String sequencia = String.format("%03d", sequence.updateAndGet(value -> value >= 999 ? 100 : value + 1));
        String corpo = ("789" + digits + timestamp.substring(Math.max(0, timestamp.length() - 6)) + sequencia);
        String base12 = corpo.substring(0, Math.min(12, corpo.length()));

        if (base12.length() < 12) {
            base12 = String.format("%-12s", base12).replace(' ', '0');
        }

        int digito = calcularDigitoVerificador(base12);
        return base12 + digito;
    }

    private int calcularDigitoVerificador(String base12) {
        int soma = 0;

        for (int i = 0; i < base12.length(); i++) {
            int numero = Character.getNumericValue(base12.charAt(i));
            soma += numero * (i % 2 == 0 ? 1 : 3);
        }

        int resto = soma % 10;
        return resto == 0 ? 0 : 10 - resto;
    }
}
