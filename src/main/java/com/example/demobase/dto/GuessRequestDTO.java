package com.example.demobase.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GuessRequestDTO {
    private Long idJugador;
    private Character letra;


}



