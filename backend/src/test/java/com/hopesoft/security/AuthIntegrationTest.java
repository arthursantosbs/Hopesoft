package com.hopesoft.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hopesoft.model.Empresa;
import com.hopesoft.model.Perfil;
import com.hopesoft.model.Usuario;
import com.hopesoft.repository.CategoriaRepository;
import com.hopesoft.repository.EmpresaRepository;
import com.hopesoft.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        categoriaRepository.deleteAll();
        empresaRepository.deleteAll();
    }

    @Test
    void loginShouldReturnTokenForValidCredentials() throws Exception {
        persistUsuario("admin@hopesoft.com", "123456", Perfil.ADMIN);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@hopesoft.com",
                                  "senha": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipoToken").value("Bearer"))
                .andExpect(jsonPath("$.usuario.email").value("admin@hopesoft.com"))
                .andExpect(jsonPath("$.usuario.perfil").value("ADMIN"));
    }

    @Test
    void loginShouldRejectInvalidCredentials() throws Exception {
        persistUsuario("operador@hopesoft.com", "123456", Perfil.OPERADOR);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "operador@hopesoft.com",
                                  "senha": "senha-errada"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("invalid_credentials"));
    }

    @Test
    void protectedEndpointShouldRequireToken() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("unauthorized"));
    }

    @Test
    void operadorShouldAccessOperadorAreaButNotAdminArea() throws Exception {
        persistUsuario("caixa@hopesoft.com", "123456", Perfil.OPERADOR);
        String token = loginAndExtractToken("caixa@hopesoft.com", "123456");

        mockMvc.perform(get("/operador/ping")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.area").value("operador"));

        mockMvc.perform(get("/admin/ping")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("forbidden"));
    }

    @Test
    void adminShouldAccessAdminAreaAndSeeOwnProfile() throws Exception {
        persistUsuario("gestor@hopesoft.com", "123456", Perfil.ADMIN);
        String token = loginAndExtractToken("gestor@hopesoft.com", "123456");

        mockMvc.perform(get("/admin/ping")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.area").value("admin"));

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("gestor@hopesoft.com"))
                .andExpect(jsonPath("$.perfil").value("ADMIN"));
    }

    private void persistUsuario(String email, String senhaBruta, Perfil perfil) {
        Empresa empresa = empresaRepository.save(Empresa.builder()
                .nome("Empresa de Teste")
                .cnpj(buildCnpjForEmail(email))
                .build());

        usuarioRepository.save(Usuario.builder()
                .empresa(empresa)
                .nome("Usuario de Teste")
                .email(email)
                .senha(passwordEncoder.encode(senhaBruta))
                .perfil(perfil)
                .build());
    }

    private String loginAndExtractToken(String email, String senha) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "senha": "%s"
                                }
                                """.formatted(email, senha)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.get("token").asText();
    }

    private String buildCnpjForEmail(String email) {
        long numeric = Integer.toUnsignedLong(email.hashCode());
        return String.format("%014d", numeric);
    }
}
