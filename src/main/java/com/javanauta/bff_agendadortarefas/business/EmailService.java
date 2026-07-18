package com.javanauta.bff_agendadortarefas.business;


import com.javanauta.bff_agendadortarefas.business.dto.out.TarefasDTOResponse;
import com.javanauta.bff_agendadortarefas.infrastructure.client.EmailClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class EmailService {

    private final EmailClient emailClient;

    public void enviarEmail(TarefasDTOResponse dto){
        emailClient.enviarEmail(dto);
    }

}