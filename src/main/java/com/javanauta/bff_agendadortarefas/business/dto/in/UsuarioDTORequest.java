package com.javanauta.bff_agendadortarefas.business.dto.in;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UsuarioDTORequest {

    private String nome;
    private String email;
    private String senha;
    private List<EnderecoDTORequest> enderecos;
    private List<TelefoneDTORequest> telefones;

}
