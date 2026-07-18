package com.javanauta.bff_agendadortarefas.business.dto.in;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class LoginRequestDTO {

    private String email;
    private String senha;

}
