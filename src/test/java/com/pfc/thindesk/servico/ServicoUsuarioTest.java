package com.pfc.thindesk.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.pfc.thindesk.dto.CadastroUsuarioDTO;
import com.pfc.thindesk.excecao.RegraDeNegocioException;
import com.pfc.thindesk.model.Perfil;
import com.pfc.thindesk.model.Usuario;
import com.pfc.thindesk.repositorio.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ServicoUsuarioTest {

    @Mock
    private RepositorioUsuario repositorioUsuario;

    private ServicoUsuario servicoUsuario;

    @BeforeEach
    void configurar() {
        servicoUsuario = new ServicoUsuario(repositorioUsuario, new BCryptPasswordEncoder(4));
    }

    @Test
    void deveCadastrarUsuarioComSenhaCriptografada() {
        when(repositorioUsuario.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(repositorioUsuario.save(any(Usuario.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        CadastroUsuarioDTO dados = new CadastroUsuarioDTO(
                "Marina Alves", "marina@thindesk.com", "senhaSegura123", "senhaSegura123");

        Usuario usuario = servicoUsuario.cadastrarUsuario(dados);

        assertEquals(Perfil.USUARIO, usuario.getPerfil());
        assertNotEquals("senhaSegura123", usuario.getSenha());
        assertTrue(usuario.getSenha().startsWith("$2"));
        assertTrue(usuario.isAtivo());
    }

    @Test
    void naoDeveCadastrarComSenhasDivergentes() {
        CadastroUsuarioDTO dados = new CadastroUsuarioDTO(
                "Marina Alves", "marina@thindesk.com", "senhaSegura123", "outraSenha123");

        assertThrows(RegraDeNegocioException.class, () -> servicoUsuario.cadastrarUsuario(dados));
    }

    @Test
    void naoDeveCadastrarEmailDuplicado() {
        when(repositorioUsuario.existsByEmailIgnoreCase("marina@thindesk.com")).thenReturn(true);

        CadastroUsuarioDTO dados = new CadastroUsuarioDTO(
                "Marina Alves", "marina@thindesk.com", "senhaSegura123", "senhaSegura123");

        assertThrows(RegraDeNegocioException.class, () -> servicoUsuario.cadastrarUsuario(dados));
    }
}
