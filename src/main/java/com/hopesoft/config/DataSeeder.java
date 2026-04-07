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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataSeeder {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seed(
            EmpresaRepository empresaRepository,
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository
    ) {
        return args -> {
            log.info("========================================");
            log.info("     INICIANDO SEEDING DE DADOS");
            log.info("========================================");

            // 1. Criar empresa padrão (se não existir)
            Empresa empresa = empresaRepository.findAll().stream()
                    .filter(e -> e.getNome().equals("HopeSoft Demo"))
                    .findFirst()
                    .orElseGet(() -> {
                        log.info("✅ Criando empresa padrão...");
                        Empresa novaEmpresa = Empresa.builder()
                                .nome("HopeSoft Demo")
                                .cnpj("12.345.678/0001-00")
                                .ativa(true)
                                .build();
                        return empresaRepository.save(novaEmpresa);
                    });

            log.info("✅ Empresa: {} (ID: {})", empresa.getNome(), empresa.getId());

            // 2. Criar usuário admin (se não existir)
            if (usuarioRepository.findByEmail("admin@hopesoft.com").isEmpty()) {
                log.info("✅ Criando usuário administrador...");
                Usuario admin = Usuario.builder()
                        .empresa(empresa)
                        .nome("Administrador HopeSoft")
                        .email("admin@hopesoft.com")
                        .senha(passwordEncoder.encode("hopesoft123"))
                        .perfil(Perfil.ADMIN)
                        .build();
                usuarioRepository.save(admin);
                log.info("   📧 Email: admin@hopesoft.com");
                log.info("   🔐 Senha: hopesoft123");
            } else {
                log.info("✅ Admin já existe, pulando criação");
            }

            // 3. Criar usuário operador (se não existir)
            if (usuarioRepository.findByEmail("operador@hopesoft.com").isEmpty()) {
                log.info("✅ Criando usuário operador...");
                Usuario operador = Usuario.builder()
                        .empresa(empresa)
                        .nome("Operador HopeSoft")
                        .email("operador@hopesoft.com")
                        .senha(passwordEncoder.encode("hopesoft123"))
                        .perfil(Perfil.OPERADOR)
                        .build();
                usuarioRepository.save(operador);
                log.info("   📧 Email: operador@hopesoft.com");
                log.info("   🔐 Senha: hopesoft123");
            } else {
                log.info("✅ Operador já existe, pulando criação");
            }

            // 4. Criar categorias base (se não existirem)
            if (categoriaRepository.findAll().isEmpty()) {
                log.info("✅ Criando categorias base...");
                String[] categoriasNomes = {
                        "Bebidas",
                        "Alimentos",
                        "Doces",
                        "Lanches",
                        "Padaria",
                        "Congelados",
                        "Limpeza",
                        "Higiene"
                };

                for (String nomCategoria : categoriasNomes) {
                    Categoria categoria = Categoria.builder()
                            .empresa(empresa)
                            .nome(nomCategoria)
                            .build();
                    categoriaRepository.save(categoria);
                    log.info("   📂 Categoria criada: {}", nomCategoria);
                }
            } else {
                log.info("✅ Categorias já existem, pulando criação");
            }

            log.info("========================================");
            log.info("  SEEDING DE DADOS CONCLUÍDO COM SUCESSO");
            log.info("========================================");
            log.info("");
            log.info("🎯 CREDENCIAIS INICIAIS:");
            log.info("   📧 Email ADMIN:    admin@hopesoft.com");
            log.info("   🔐 Senha ADMIN:    hopesoft123");
            log.info("");
            log.info("   📧 Email OPERADOR: operador@hopesoft.com");
            log.info("   🔐 Senha OPERADOR: hopesoft123");
            log.info("");
            log.info("🌍 Acesse em: http://localhost:8080");
            log.info("🔓 Faça login em: POST /auth/login");
            log.info("");
        };
    }
}

