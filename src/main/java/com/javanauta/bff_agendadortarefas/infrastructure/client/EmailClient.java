package com.javanauta.bff_agendadortarefas.infrastructure.client;

import com.javanauta.bff_agendadortarefas.business.dto.out.TarefasDTOResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notificacao", url = "${notificacao.url}")

public interface EmailClient {

    void enviarEmail(@RequestBody TarefasDTOResponse dto){

    }

}
