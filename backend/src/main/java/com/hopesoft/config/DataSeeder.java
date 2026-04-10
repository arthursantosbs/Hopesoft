package com.hopesoft.config;

import com.hopesoft.model.Categoria;
import com.hopesoft.model.Empresa;
import com.hopesoft.model.Perfil;
import com.hopesoft.model.Usuario;
import com.hopesoft.repository.CategoriaRepository;
import com.hopesoft.repository.EmpresaRepository;
import com.hopesoft.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataSeeder {

    private final PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
    public CommandLineRunner seed(
            EmpresaRepository empresaRepository,
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository
    ) {
        return args -> {
            Empresa empresa = empresaRepository.findByCnpj("12345678000100")
                    .orElseGet(() -> empresaRepository.save(Empresa.builder()
                            .nome("HopeSoft Demo")
                            .cnpj("12345678000100")
                            .ativa(true)
                            .build()));

            if (usuarioRepository.findByEmail("admin@hopesoft.com").isEmpty()) {
                usuarioRepository.save(Usuario.builder()
                        .empresa(empresa)
                        .nome("Administrador HopeSoft")
                        .email("admin@hopesoft.com")
                        .senha(passwordEncoder.encode("hopesoft123"))
                        .perfil(Perfil.ADMIN)
                        .build());
            }

            if (usuarioRepository.findByEmail("operador@hopesoft.com").isEmpty()) {
                usuarioRepository.save(Usuario.builder()
                        .empresa(empresa)
                        .nome("Operador HopeSoft")
                        .email("operador@hopesoft.com")
                        .senha(passwordEncoder.encode("hopesoft123"))
                        .perfil(Perfil.OPERADOR)
                        .build());
            }

            String[] categorias = {
                    "Bebidas",
                    "Alimentos",
                    "Doces",
                    "Lanches",
                    "Padaria",
                    "Congelados",
                    "Limpeza",
                    "Higiene"
            };

            for (String nomeCategoria : categorias) {
                if (!categoriaRepository.existsByNomeAndEmpresa(nomeCategoria, empresa)) {
                    categoriaRepository.save(Categoria.builder()
                            .empresa(empresa)
                            .nome(nomeCategoria)
                            .build());
                }
            }

            log.info("Seed inicial verificado para a empresa HopeSoft Demo");
        };
    }
}
