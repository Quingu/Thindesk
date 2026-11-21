package com.pfc.thindesk.config;

import com.pfc.thindesk.entity.Chamado;
import com.pfc.thindesk.entity.Cliente;
import com.pfc.thindesk.entity.Comentario;
import com.pfc.thindesk.entity.HorarioAtendimento;
import com.pfc.thindesk.entity.PrioridadeChamado;
import com.pfc.thindesk.entity.SituacaoChamado;
import com.pfc.thindesk.entity.TipoChamado;
import com.pfc.thindesk.model.Perfil;
import com.pfc.thindesk.model.Usuario;
import com.pfc.thindesk.repositorio.RepositorioChamado;
import com.pfc.thindesk.repositorio.RepositorioCliente;
import com.pfc.thindesk.repositorio.RepositorioHorarioAtendimento;
import com.pfc.thindesk.repositorio.RepositorioUsuario;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class ServicoSemente implements CommandLineRunner {

    private static final String SENHA_PADRAO = "Thindesk@2026";

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioCliente repositorioCliente;
    private final RepositorioChamado repositorioChamado;
    private final RepositorioHorarioAtendimento repositorioHorario;
    private final PasswordEncoder codificadorSenha;

    public ServicoSemente(RepositorioUsuario repositorioUsuario, RepositorioCliente repositorioCliente,
                          RepositorioChamado repositorioChamado,
                          RepositorioHorarioAtendimento repositorioHorario,
                          PasswordEncoder codificadorSenha) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioCliente = repositorioCliente;
        this.repositorioChamado = repositorioChamado;
        this.repositorioHorario = repositorioHorario;
        this.codificadorSenha = codificadorSenha;
    }

    @Override
    public void run(String... args) {
        if (repositorioUsuario.count() > 0) {
            return;
        }

        Usuario gustavo = criarUsuario("Gustavo Quintiliano", "admin@thindesk.com", Perfil.ADMINISTRADOR);
        Usuario bruno = criarUsuario("Bruno Carvalho", "gerente@thindesk.com", Perfil.GERENTE);
        Usuario carla = criarUsuario("Carla Souza", "usuario@thindesk.com", Perfil.USUARIO);
        Usuario diego = criarUsuario("Diego Lima", "tecnico@thindesk.com", Perfil.GERENTE);
        Usuario elisa = criarUsuario("Elisa Rocha", "elisa@thindesk.com", Perfil.USUARIO);

        Cliente technova = criarCliente("TechNova Sistemas", "(11) 3344-1200", "contato@technova.com.br", "Tecnologia");
        Cliente aurora = criarCliente("Grupo Aurora", "(11) 3222-8800", "suporte@grupoaurora.com.br", "Comercial");
        Cliente vida = criarCliente("Hospital Vida", "(21) 3555-4100", "ti@hospitalvida.com.br", "Saúde");
        Cliente meridiano = criarCliente("Banco Meridiano", "(11) 4002-8922", "infra@bancomeridiano.com.br", "Financeiro");
        Cliente logiexpress = criarCliente("LogiExpress", "(41) 3033-7700", "operacoes@logiexpress.com.br", "Logística");

        criarHorarios();

        Chamado primeiro = criarChamado(1, "Impressora fiscal não imprime cupom",
                "A impressora do caixa 3 parou de emitir cupons após a atualização do sistema. O LED fica vermelho e o trabalho fica na fila.",
                SituacaoChamado.ABERTO, PrioridadeChamado.ALTA, TipoChamado.IMPRESSORA,
                technova, carla, null, 0, 8, 40, null);

        Chamado vpn = criarChamado(2, "VPN cai a cada 15 minutos no setor fiscal",
                "Os analistas do setor fiscal perdem a conexão com o ERP a cada 15 minutos. O problema começou após a troca do link de internet.",
                SituacaoChamado.EM_ANDAMENTO, PrioridadeChamado.CRITICA, TipoChamado.REDE,
                meridiano, elisa, bruno.getEmail(), 0, 9, 15, null);

        criarChamado(3, "Solicitação de acesso ao ERP",
                "Novo colaborador do comercial precisa de acesso de consulta ao módulo de pedidos do ERP.",
                SituacaoChamado.RESOLVIDO, PrioridadeChamado.MEDIA, TipoChamado.ACESSO,
                aurora, carla, diego.getEmail(), 0, 11, 5,
                "Usuário criado no ERP com perfil de consulta e senha temporária enviada ao gestor do setor.");

        Chamado notebook = criarChamado(4, "Notebook não liga após queda de energia",
                "O notebook da recepção não liga desde a oscilação de energia de ontem à noite. O carregador acende o LED, mas a tela permanece apagada.",
                SituacaoChamado.EM_ANDAMENTO, PrioridadeChamado.ALTA, TipoChamado.HARDWARE,
                vida, elisa, bruno.getEmail(), 1, 8, 30, null);

        criarChamado(5, "Erro ao emitir relatório de estoque",
                "O relatório de posição de estoque retorna erro 500 ao selecionar o período do mês atual.",
                SituacaoChamado.RESOLVIDO, PrioridadeChamado.MEDIA, TipoChamado.SOFTWARE,
                logiexpress, carla, diego.getEmail(), 1, 14, 10,
                "Índice da coleção de movimentações reconstruído e cache da aplicação limpo. Relatório emitido com sucesso.");

        Chamado wifi = criarChamado(6, "Wi-Fi instável no 3º andar",
                "Os colaboradores do terceiro andar relatam quedas constantes do Wi-Fi. O sinal oscila entre uma e três barras.",
                SituacaoChamado.AGUARDANDO_CLIENTE, PrioridadeChamado.MEDIA, TipoChamado.REDE,
                technova, elisa, bruno.getEmail(), 2, 9, 0, null);

        criarChamado(7, "Troca de toner da impressora do RH",
                "A impressora do RH sinaliza toner vazio e interrompeu as impressões de documentos admissionais.",
                SituacaoChamado.RESOLVIDO, PrioridadeChamado.BAIXA, TipoChamado.IMPRESSORA,
                aurora, carla, diego.getEmail(), 2, 15, 20,
                "Toner substituído por unidade do estoque e contador da impressora resetado.");

        criarChamado(8, "Sistema de ponto fora do ar",
                "Nenhum colaborador consegue registrar ponto na catraca eletrônica desde as 7h.",
                SituacaoChamado.RESOLVIDO, PrioridadeChamado.CRITICA, TipoChamado.SOFTWARE,
                vida, elisa, bruno.getEmail(), 3, 8, 50,
                "Serviço de integração reiniciado e banco de dados do ponto religado. Registros pendentes foram sincronizados.");

        criarChamado(9, "Bloqueio de acesso ao e-mail corporativo",
                "A conta de e-mail da diretoria foi bloqueada após tentativas de acesso suspeitas.",
                SituacaoChamado.RESOLVIDO, PrioridadeChamado.ALTA, TipoChamado.ACESSO,
                meridiano, carla, diego.getEmail(), 3, 10, 40,
                "Conta desbloqueada após validação com o usuário, senha redefinida e MFA reconfigurado.");

        criarChamado(10, "Monitor com listras na tela",
                "O monitor do posto de conferência apresenta listras horizontais intermitentes.",
                SituacaoChamado.CANCELADO, PrioridadeChamado.BAIXA, TipoChamado.HARDWARE,
                logiexpress, elisa, null, 4, 9, 30, null);

        criarChamado(11, "Backup automático não executa",
                "A rotina de backup das 2h não é executada há três dias e o repositório está sem novas cópias.",
                SituacaoChamado.RESOLVIDO, PrioridadeChamado.ALTA, TipoChamado.SOFTWARE,
                technova, carla, bruno.getEmail(), 4, 16, 0,
                "Serviço de backup reconfigurado com nova credencial de armazenamento e execução de teste concluída.");

        criarChamado(12, "Novo usuário no setor comercial",
                "Criar usuário de rede para a nova analista comercial, com acesso à pasta compartilhada de propostas.",
                SituacaoChamado.RESOLVIDO, PrioridadeChamado.MEDIA, TipoChamado.ACESSO,
                aurora, elisa, diego.getEmail(), 5, 8, 20,
                "Usuário criado no diretório, grupos de acesso aplicados e estação configurada.");

        criarChamado(13, "Cabo de rede rompido na recepção",
                "O ponto de rede da recepção foi danificado durante a reforma e a estação ficou sem conexão.",
                SituacaoChamado.RESOLVIDO, PrioridadeChamado.MEDIA, TipoChamado.REDE,
                vida, carla, bruno.getEmail(), 5, 13, 45,
                "Cabo recrimpado e ponto testado com certificador. Conexão restabelecida em 100 Mbps.");

        criarChamado(14, "Atualização do antivírus falhando",
                "A atualização de definições do antivírus falha em cinco estações do setor de crédito.",
                SituacaoChamado.RESOLVIDO, PrioridadeChamado.BAIXA, TipoChamado.SOFTWARE,
                meridiano, elisa, diego.getEmail(), 6, 10, 10,
                "Servidor de atualização apontado para o espelho interno e políticas reaplicadas nas estações.");

        criarChamado(15, "Impressora sem conexão após troca de switch",
                "Após a troca do switch do galpão, a impressora de etiquetas não responde na rede.",
                SituacaoChamado.RESOLVIDO, PrioridadeChamado.MEDIA, TipoChamado.IMPRESSORA,
                logiexpress, carla, bruno.getEmail(), 6, 14, 30,
                "Porta do switch reconfigurada para a VLAN correta e IP reservado no DHCP.");

        criarChamado(16, "Acesso remoto lento para home office",
                "Colaboradores em home office relatam lentidão ao acessar os sistemas internos pela VPN.",
                SituacaoChamado.RESOLVIDO, PrioridadeChamado.MEDIA, TipoChamado.REDE,
                technova, elisa, diego.getEmail(), 1, 17, 0,
                "Perfil de banda da VPN ajustado e túnel redirecionado para o servidor com menor latência.");

        criarChamado(17, "Solicitação de segundo monitor",
                "A analista de dados solicita um segundo monitor para acompanhar os painéis de indicadores.",
                SituacaoChamado.RESOLVIDO, PrioridadeChamado.BAIXA, TipoChamado.HARDWARE,
                aurora, carla, bruno.getEmail(), 2, 11, 30,
                "Monitor adicional instalado e configuração de tela estendida aplicada.");

        criarChamado(18, "Erro 500 ao salvar cadastro no CRM",
                "O cadastro de clientes no CRM retorna erro 500 ao salvar registros com mais de um endereço.",
                SituacaoChamado.RESOLVIDO, PrioridadeChamado.CRITICA, TipoChamado.SOFTWARE,
                meridiano, elisa, diego.getEmail(), 3, 16, 20,
                "Correção aplicada na validação de endereços e deploy realizado em produção.");

        adicionarComentario(vpn, bruno.getEmail(), "GERENTE",
                "Triagem concluída. Identificamos perda de pacotes no link principal e acionamos a operadora.");
        adicionarComentario(vpn, elisa.getEmail(), "USUARIO",
                "O problema continua acontecendo. Consegui registrar o horário das últimas três quedas.");
        adicionarComentario(vpn, bruno.getEmail(), "GERENTE",
                "Recebido. Estamos acompanhando a estabilização após a troca da rota.");

        adicionarComentario(notebook, bruno.getEmail(), "GERENTE",
                "Equipamento recolhido para bancada. Vamos testar a fonte e a placa-mãe.");
        adicionarComentario(notebook, elisa.getEmail(), "USUARIO",
                "Obrigada! A recepção está usando uma máquina reserva temporariamente.");

        adicionarComentario(wifi, bruno.getEmail(), "GERENTE",
                "Ajuste de canal realizado. Precisamos validar com os usuários do terceiro andar.");
        adicionarComentario(wifi, elisa.getEmail(), "USUARIO",
                "Vou confirmar com a equipe e retorno ainda hoje.");

        adicionarComentario(primeiro, carla.getEmail(), "USUARIO",
                "A impressora voltou a aceitar os trabalhos, mas continua sem imprimir o cupom fiscal.");
    }

    private Usuario criarUsuario(String nome, String email, Perfil perfil) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(codificadorSenha.encode(SENHA_PADRAO));
        usuario.setPerfil(perfil);
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now().minusDays(30));
        usuario.setDataAtualizacao(LocalDateTime.now().minusDays(2));
        return repositorioUsuario.save(usuario);
    }

    private Cliente criarCliente(String nome, String telefone, String email, String setor) {
        Cliente cliente = new Cliente();
        cliente.setNome(nome);
        cliente.setTelefone(telefone);
        cliente.setEmail(email);
        cliente.setSetor(setor);
        cliente.setAtivo(true);
        cliente.setDataCadastro(LocalDateTime.now().minusDays(90));
        return repositorioCliente.save(cliente);
    }

    private void criarHorarios() {
        List<HorarioAtendimento> horarios = List.of(
                new HorarioAtendimento(null, "Suporte", "Segunda-feira", "08:00", "18:00"),
                new HorarioAtendimento(null, "Suporte", "Terça-feira", "08:00", "18:00"),
                new HorarioAtendimento(null, "Suporte", "Quarta-feira", "08:00", "18:00"),
                new HorarioAtendimento(null, "Infraestrutura", "Segunda-feira", "07:00", "19:00"),
                new HorarioAtendimento(null, "Infraestrutura", "Quinta-feira", "07:00", "19:00"),
                new HorarioAtendimento(null, "Field Service", "Sexta-feira", "09:00", "17:00"));

        repositorioHorario.saveAll(horarios);
    }

    private Chamado criarChamado(int sequencia, String titulo, String descricao,
                                 SituacaoChamado situacao, PrioridadeChamado prioridade,
                                 TipoChamado tipo, Cliente cliente, Usuario solicitante,
                                 String tecnico, int diasAtras, int hora, int minuto, String solucao) {
        LocalDateTime abertura = LocalDate.now().minusDays(diasAtras).atTime(hora, minuto);

        Chamado chamado = new Chamado();
        chamado.setProtocolo(String.format("TD-%d-%04d", Year.now().getValue(), sequencia));
        chamado.setTitulo(titulo);
        chamado.setDescricao(descricao);
        chamado.setSituacao(situacao);
        chamado.setPrioridade(prioridade);
        chamado.setTipo(tipo);
        chamado.setClienteId(cliente.getId());
        chamado.setClienteNome(cliente.getNome());
        chamado.setSetor(cliente.getSetor());
        chamado.setSolicitanteEmail(solicitante.getEmail());
        chamado.setSolicitanteNome(solicitante.getNome());
        chamado.setTecnico(tecnico);
        chamado.setDataAbertura(abertura);
        chamado.setSolucao(solucao);

        if (situacao == SituacaoChamado.RESOLVIDO) {
            chamado.setDataResolucao(abertura.plusHours(2 + (sequencia % 9)).plusMinutes(20));
            chamado.setDataAtualizacao(chamado.getDataResolucao());
        } else {
            chamado.setDataAtualizacao(abertura.plusHours(1));
        }

        return repositorioChamado.save(chamado);
    }

    private void adicionarComentario(Chamado chamado, String autor, String perfil, String mensagem) {
        Comentario comentario = new Comentario();
        comentario.setAutor(autor);
        comentario.setPerfil(perfil);
        comentario.setMensagem(mensagem);
        comentario.setDataCriacao(chamado.getDataAbertura().plusHours(1));

        chamado.getComentarios().add(comentario);
        repositorioChamado.save(chamado);
    }
}
