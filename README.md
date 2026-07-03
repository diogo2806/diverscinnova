# diverSCInnova — Site institucional

Landing page da **diverSCInnova** (*Neuro-Analytics & Human Capital*): decodificamos
assinaturas neurais com IA e Big Data para transformar a gestão de pessoas com
precisão científica e acessibilidade universal.

A página é estática (HTML + **Tailwind CSS** compilado localmente) e é servida por
uma pequena aplicação **Spring Boot (Java 21)**, que também expõe o endpoint do
**formulário de contato**, enviando os leads por e-mail via **SMTP do SendPulse**.

## Recursos

- 🌐 **Bilíngue (PT/EN)** com troca de idioma via `data-i18n` (sem recarregar a página)
- ♿ **Acessibilidade**: alto contraste e controle de tamanho de fonte (A- / A / A+)
- 🎠 Carrosséis de narrativa e depoimentos
- 🔢 Contadores animados na seção de impacto
- ✨ Animações de *reveal* ao rolar a página (`IntersectionObserver`)
- 📱 Layout responsivo (mobile-first)
- ✉️ **Formulário de contato** que envia e-mail via SMTP (SendPulse), com honeypot anti-spam

## Estrutura

```
.
├── index.html                                   # Página (marcação + scripts do front)
├── src/input.css                                # Entrada do Tailwind
├── assets/tailwind.css                          # CSS compilado (versionado)
├── tailwind.config.js  ·  package.json          # Build do CSS (Node)
├── pom.xml                                       # Build da app (Maven)
└── src/main/
    ├── java/com/diverscinnova/site/
    │   ├── SiteApplication.java                  # Bootstrap Spring Boot
    │   ├── ContactController.java                # POST /api/contato -> envia e-mail
    │   └── ContactForm.java                      # Payload do formulário
    └── resources/application.properties          # Configuração (porta, SMTP via env)
```

O `index.html` e `assets/` são embutidos no jar em `classpath:/static/` durante o
build do Maven (por isso o CSS precisa estar compilado antes — ver abaixo).

## Como o e-mail funciona

O front-end faz `POST /api/contato` (JSON). O `ContactController`:

1. valida os campos (nome, e-mail, mensagem) e descarta bots via honeypot;
2. monta o e-mail (com `Reply-To` = e-mail de quem preencheu, para você responder direto);
3. envia via `JavaMailSender` usando o SMTP configurado.

> 🔐 As credenciais SMTP **nunca** ficam no front-end nem no código — vêm de
> variáveis de ambiente (ver abaixo). Isso é o motivo de existir um back-end: um
> site puramente estático não pode guardar a senha do SendPulse com segurança.

### Variáveis de ambiente (SMTP SendPulse)

Configure no EasyPanel (valores em `.env.example`):

| Variável         | Descrição                                            | Padrão           |
|------------------|------------------------------------------------------|------------------|
| `SMTP_HOST`      | Servidor SMTP                                         | `smtp-pulse.com` |
| `SMTP_PORT`      | Porta SMTP (587 STARTTLS / 465 SSL)                  | `587`            |
| `SMTP_USER`      | Login SMTP do SendPulse                               | — (obrigatório)  |
| `SMTP_PASS`      | Senha SMTP do SendPulse                               | — (obrigatório)  |
| `SMTP_STARTTLS`  | Usar STARTTLS (porta 587)                             | `true`           |
| `SMTP_SSL`       | Usar SSL (porta 465; se `true`, ponha `STARTTLS=false`) | `false`       |
| `MAIL_TO`        | Para onde os contatos chegam                          | usa `SMTP_USER`  |
| `MAIL_FROM`      | Remetente (precisa ser verificado no SendPulse)       | usa `SMTP_USER`  |

As credenciais SMTP estão em **Configurações → SMTP** no painel do SendPulse.
Enquanto `SMTP_USER`/`MAIL_TO` não estiverem definidos, o endpoint responde
`503 not_configured` e o formulário mostra uma mensagem de erro amigável.

## Desenvolvimento

Pré-requisitos: **Node.js 18+**, **JDK 21** e **Maven**.

```bash
npm install && npm run build     # compila o assets/tailwind.css (necessário antes do jar)
mvn package                      # gera target/diverscinnova-site.jar (com o site embutido)

# rodar localmente (defina as variáveis SMTP para o envio funcionar):
SMTP_USER=... SMTP_PASS=... MAIL_TO=... MAIL_FROM=... java -jar target/diverscinnova-site.jar
# http://localhost:8080
```

Ao mexer nas classes do `index.html`, rode `npm run build` (ou `npm run watch`)
para regenerar o CSS antes de reconstruir o jar.

## Deploy no EasyPanel (Docker)

O deploy de produção roda no **EasyPanel**, a partir do `Dockerfile` (build
multi-stage: Node compila o CSS → Maven gera o jar → runtime `eclipse-temurin` roda
a app). A imagem final contém só o JRE e o jar.

1. Crie um serviço do tipo **App** apontando para este repositório.
2. Em **Build**, selecione **Dockerfile** (detectado na raiz).
3. Em **Ports/Proxy**, aponte o proxy para a porta **8080**.
4. Em **Environment**, defina as variáveis SMTP da tabela acima.
5. Configure o domínio e faça o deploy.

Testar a imagem localmente:

```bash
docker build -t diverscinnova-site .
docker run --rm -p 8080:80 \
  -e SMTP_USER=... -e SMTP_PASS=... -e MAIL_TO=... -e MAIL_FROM=... \
  -e PORT=80 diverscinnova-site        # http://localhost:8080
```

## Notas

- A fonte **Montserrat** é carregada via Google Fonts (com fallback para `sans-serif`).
- As imagens (logos, fotos do time, selos de prêmios) são servidas a partir de `diverscinnova.com`.
- O workflow `.github/workflows/deploy.yml` (GitHub Pages) publica **apenas a parte
  estática** e **não** inclui o envio de e-mail (que exige o back-end). O deploy
  completo é o do EasyPanel.
