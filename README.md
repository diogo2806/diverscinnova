# diverSCInnova — Site institucional

Landing page da **diverSCInnova** (*Neuro-Analytics & Human Capital*): decodificamos
assinaturas neurais com IA e Big Data para transformar a gestão de pessoas com
precisão científica e acessibilidade universal.

Site estático de página única (`index.html`), estilizado com **Tailwind CSS**
compilado localmente — sem dependência de CDN em produção.

## Recursos

- 🌐 **Bilíngue (PT/EN)** com troca de idioma via `data-i18n` (sem recarregar a página)
- ♿ **Acessibilidade**: alto contraste e controle de tamanho de fonte (A- / A / A+)
- 🎠 Carrosséis de narrativa e depoimentos
- 🔢 Contadores animados na seção de impacto
- ✨ Animações de *reveal* ao rolar a página (`IntersectionObserver`)
- 📱 Layout responsivo (mobile-first)

## Estrutura

```
.
├── index.html            # Página completa (marcação + scripts)
├── src/input.css         # Entrada do Tailwind (@tailwind base/components/utilities)
├── assets/tailwind.css   # CSS compilado (versionado; é o que a página carrega)
├── tailwind.config.js    # Configuração do Tailwind (scan de index.html)
└── package.json          # Scripts de build
```

## Desenvolvimento

Pré-requisitos: Node.js 18+.

```bash
npm install        # instala o Tailwind
npm run build      # gera assets/tailwind.css minificado
npm run watch      # recompila a cada alteração no HTML
npm run serve      # serve o site em http://localhost:8080
```

Ao adicionar ou remover classes utilitárias no `index.html`, rode `npm run build`
(ou deixe o `npm run watch` ativo) para regenerar o `assets/tailwind.css`.

## Publicação (GitHub Pages)

Por ser um site estático, basta servir o conteúdo do repositório. O workflow em
`.github/workflows/deploy.yml` compila o CSS e publica no GitHub Pages
automaticamente a cada push na branch padrão. Para ativar:

1. Em **Settings → Pages**, defina **Source: GitHub Actions**.
2. Faça merge para a branch padrão — o deploy roda sozinho.

O `assets/tailwind.css` também é versionado, então o `index.html` pode ser
publicado em qualquer host estático (Netlify, Vercel, S3, etc.) sem etapa de build.

## Notas

- A fonte **Montserrat** é carregada via Google Fonts (com fallback para
  `sans-serif`).
- As imagens (logos, fotos do time, selos de prêmios) são servidas a partir de
  `diverscinnova.com`.
