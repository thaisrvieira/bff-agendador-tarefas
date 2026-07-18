package com.javanauta.bff_agendadortarefas.business;

import com.javanauta.bff_agendadortarefas.business.dto.in.LoginRequestDTO;
import com.javanauta.bff_agendadortarefas.business.dto.out.TarefasDTOResponse;
import com.javanauta.bff_agendadortarefas.business.enums.StatusNotificacaoEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class CronService {

    private final TarefasService tarefasService;
    private final EmailService emailService;
    private final UsuarioService usuarioService;

    @Value("${usuario.email}")
    private String email;

    @Value("${usuario.senha}")
    private String senha;

    @Scheduled(cron = "${cron.horario}")
    public void BuscaTarefasProximaHora(){
        String token = login(converterParaLoginRequestDTO());

        log.info("Iniciada a busca de tarefas");

        LocalDateTime horaAtual = LocalDateTime.now();
        LocalDateTime horaFutura = LocalDateTime.now().plusHours(1);

        List<TarefasDTOResponse> listaTarefas = tarefasService.buscaTarefasAgendadasPorPeriodo(horaAtual, horaFutura, token);

        log.info("Tarefas encontradas: " + listaTarefas);

        listaTarefas.forEach(tarefa -> {
            emailService.enviarEmail(tarefa);
            log.info("Email enviado para o usuario " + tarefa.getEmailUsuario());
            tarefasService.alteraStatus(StatusNotificacaoEnum.NOTIFICADO, tarefa.getId(),
                    token);});
        log.info("Finalizada a busca e notificação de tarefas");
    }

    public String login(LoginRequestDTO dto){
        return usuarioService.loginUsuario(dto);
    }

    public LoginRequestDTO converterParaLoginRequestDTO(){
        return LoginRequestDTO.builder()
                .email(email)
                .senha(senha)
                .build();
    }

}
