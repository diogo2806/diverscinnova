package com.diverscinnova.site;

/**
 * Payload do formulário de contato.
 * {@code website} é um honeypot: campo escondido que humanos deixam vazio e bots costumam preencher.
 */
public record ContactForm(String name, String email, String message, String website) {
}
