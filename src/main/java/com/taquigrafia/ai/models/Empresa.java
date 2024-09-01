package com.taquigrafia.ai.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "empresas")
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    private String razaoSocial;

    @NotBlank
    @Size(max = 14)
    private String cnpj;

    @NotBlank
    @Size(max = 200)
    private String logradouro;

    @NotBlank
    @Size(max = 100)
    private String numero;

    @NotBlank
    @Size(max = 100)
    private String complemento;

    @NotBlank
    @Size(max = 100)
    private String bairro;

    @NotBlank
    @Size(max = 100)
    private String cidade;

    @NotBlank
    @Size(max = 2)
    private String estado;



}
