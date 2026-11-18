package com.pfc.thindesk.servico;

import com.pfc.thindesk.dto.AlteracaoSenhaDTO;
import com.pfc.thindesk.dto.CadastroUsuarioDTO;
import com.pfc.thindesk.excecao.RegraDeNegocioException;
import com.pfc.thindesk.model.Perfil;
import com.pfc.thindesk.model.Usuario;
import com.pfc.thindesk.repositorio.RepositorioUsuario;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ServicoUsuario {

    private final RepositorioUsuario repositorioUsuario;
    private final PasswordEncoder codificadorSenha;

    public ServicoUsuario(RepositorioUsuario repositorioUsuario, PasswordEncoder codificadorSenha) {
        this.repositorioUsuario = repositorioUsuario;
        this.codificadorSenha = codificadorSenha;
    }

    public Usuario cadastrarUsuario(CadastroUsuarioDTO dados) {
        if (!dados.getSenha().equals(dados.getSenhaConfirmacao())) {
            throw new RegraDeNegocioException("As senhas não coincidem.");
        }
        if (repositorioUsuario.existsByEmailIgnoreCase(dados.getEmail().trim())) {
            throw new RegraDeNegocioException("Já existe uma conta com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dados.getNome().trim());
        usuario.setEmail(normalizarEmail(dados.getEmail()));
        usuario.setSenha(codificadorSenha.encode(dados.getSenha()));
        usuario.setPerfil(Perfil.USUARIO);
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDataAtualizacao(LocalDateTime.now());
        return repositorioUsuario.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return repositorioUsuario.findByEmailIgnoreCase(normalizarEmail(email))
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado."));
    }

    public List<Usuario> listarUsuarios() {
        return repositorioUsuario.findAllByOrderByNomeAsc();
    }

    public Usuario atualizarPerfil(String id, Perfil novoPerfil) {
        Usuario usuario = buscarPorId(id);

        if (usuario.getPerfil() == Perfil.ADMINISTRADOR && novoPerfil != Perfil.ADMINISTRADOR
                && repositorioUsuario.countByPerfil(Perfil.ADMINISTRADOR) <= 1) {
            throw new RegraDeNegocioException("O sistema precisa de ao menos um administrador ativo.");
        }

        usuario.setPerfil(novoPerfil);
        usuario.setDataAtualizacao(LocalDateTime.now());
        return repositorioUsuario.save(usuario);
    }

    public Usuario alternarAtivo(String id, String emailAutenticado) {
        Usuario usuario = buscarPorId(id);

        if (usuario.getEmail().equalsIgnoreCase(emailAutenticado)) {
            throw new RegraDeNegocioException("Você não pode desativar a própria conta.");
        }

        usuario.setAtivo(!usuario.isAtivo());
        usuario.setDataAtualizacao(LocalDateTime.now());
        return repositorioUsuario.save(usuario);
    }

    public void alterarSenha(String email, AlteracaoSenhaDTO dados) {
        if (!dados.getNovaSenha().equals(dados.getConfirmacao())) {
            throw new RegraDeNegocioException("A confirmação da nova senha não confere.");
        }

        Usuario usuario = buscarPorEmail(email);
        if (!codificadorSenha.matches(dados.getSenhaAtual(), usuario.getSenha())) {
            throw new RegraDeNegocioException("A senha atual informada está incorreta.");
        }

        usuario.setSenha(codificadorSenha.encode(dados.getNovaSenha()));
        usuario.setDataAtualizacao(LocalDateTime.now());
        repositorioUsuario.save(usuario);
    }

    public Usuario buscarPorId(String id) {
        return repositorioUsuario.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado."));
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }
}
