# Cooperfilme Backend

Este é o projeto de backend para a aplicação Cooperfilme, desenvolvido com Spring Boot e Java 17.

## Requisitos

- Java 17 ou superior
- Maven
- Docker Instalado

## Como Executar

1. Navegue ate a raiz do projeto e execute o comando a seguir para levantar o banco de dados postgres: docker-compse up -d
2. Execute a aplicação na IDE que desejar, lembrando de configurar o Lombok


O aplicativo estará disponível em `http://localhost:8081`.

Documentação da aplicação com Swagger (necessário efetuar login com um dos usuarios abaixo):
- http://localhost:8081/swagger-ui/index.html

Para facilitar os testes, usar os arquivos COOPERFILME_ENV.postman_environment e cooperfilme.postman_collection para importar no postman as requisições e ENV

## Credenciais Iniciais

Os seguintes usuários são criados automaticamente na inicialização do aplicativo

- **Analista:**
  - **Email:** `analista@cooperfilme.com.br`
  - **Senha:** `password`

- **Revisor:**
  - **Email:** `revisor@cooperfilme.com.br`
  - **Senha:** `password`

- **Aprovador 1:**
  - **Email:** `aprovador1@cooperfilme.com.br`
  - **Senha:** `password`

- **Aprovador 2:**
  - **Email:** `aprovador2@cooperfilme.com.br`
  - **Senha:** `password`

- **Aprovador 3:**
  - **Email:** `aprovador3@cooperfilme.com.br`
  - **Senha:** `password`

## Carga de Dados
Alguns Scripts serão inseridos ao iniciar a aplicação caso a tabela Script esteja vazia

segue a baixo os Scripts Inseridos:

[
  {
    "content": "**Título: O Último Trem para Aurora**\n\n**Cena 1: Estação Central – Noite**\nSom de apitos e passos apressados. Câmera foca em LUCAS (30), olhando o relógio com ansiedade.\n\n**LUCAS:** (voz trêmula) Se eu perder esse trem, perco tudo.\n\n**Cena 2: Vagão do Trem – Alguns minutos depois**\nLuzes piscam, passageiros quietos. Ele encontra CLARA (28), surpresa.\n\n**CLARA:** Você aqui? Depois de tudo?\n\n**LUCAS:** Não podia deixar você ir sem saber a verdade.\n\n**Cena 3: Flashback – Dia da Separação**\nBriga intensa na sala de estar. LUCAS quebra um vaso sem querer.\n\n**CLARA:** (chorando) Eu te amei, Lucas. Mas não posso mais viver com segredos.\n\n**Cena 4: Estação Aurora – Amanhecer**\nTrem para. Os dois em silêncio, mãos entrelaçadas.\n\n**LUCAS:** Será que podemos tentar de novo?\n\n**CLARA:** Vamos descer e descobrir.\n\n(Câmera se afasta, mostrando a cidade despertando com o sol.)\n\n**FIM.**",
    "clientName": "Marcio Willian",
    "clientEmail": "marcio@gmail.com",
    "clientPhone": "98999999999"
  },
  {
    "content": "Um detetive aposentado é chamado para resolver o mistério do desaparecimento de um influente político em uma cidade litorânea.",
    "clientName": "Joana Mendes",
    "clientEmail": "joana.mendes@email.com",
    "clientPhone": "11987654321"
  },
  {
    "content": "Uma adolescente descobre que sua família guarda um segredo místico milenar que pode salvar ou destruir o mundo moderno.",
    "clientName": "Carlos Silva",
    "clientEmail": "carlos.silva@email.com",
    "clientPhone": "21998877665"
  },
  {
    "content": "Um grupo de astronautas fica preso em uma estação espacial após uma tempestade solar e precisa lutar contra o tempo para retornar à Terra.",
    "clientName": "Marina Rocha",
    "clientEmail": "marina.rocha@email.com",
    "clientPhone": "31997766554"
  },
  {
    "content": "Durante a pandemia, dois estranhos se conhecem por vídeo chamada e desenvolvem um vínculo inesperado, mas escondem segredos perigosos.",
    "clientName": "Diego Albuquerque",
    "clientEmail": "diego.alb@email.com",
    "clientPhone": "61994433221"
  },
  {
    "content": "Um chef falido decide abrir um food truck e acaba se envolvendo com uma gangue que usa o negócio como fachada para lavagem de dinheiro.",
    "clientName": "Fernanda Lopes",
    "clientEmail": "fernanda.lopes@email.com",
    "clientPhone": "47991122334"
  }
]


## Endpoints da API

### Autenticação
- `POST /api/auth/login`
  - Corpo da requisição: `{ "email": "string", "password": "string" }`

### Roteiros (Público)
- `POST /api/scripts/submit`
  - Envia um novo roteiro para análise.
  - Corpo da requisição: `{ "content": "string", "clientName": "string", "clientEmail": "string", "clientPhone": "string" }`
- `GET /api/scripts/status/{id}`
  - Consulta o status de um roteiro pelo ID.

### Roteiros (Protegido - Requer autenticação)
- `GET /api/scripts`
  - Lista todos os roteiros com filtros opcionais (status, email do cliente, data de envio).
  - Parâmetros de query: `status`, `clientEmail`, `startDate` (ISO_DATE_TIME), `endDate` (ISO_DATE_TIME)
- `GET /api/scripts/{id}`
  - Consulta os dados completos de um roteiro pelo ID.
- `PUT /api/scripts/{id}/status`
  - Altera o status de um roteiro.
  - Parâmetros de query: `newStatus`, `justification` (opcional)
- `POST /api/scripts/{id}/vote`
  - Permite que um aprovador vote em um roteiro.
  - Parâmetros de query: `approved` (boolean), `comments` (opcional)

## Fluxo de Status do Roteiro

1. `AGUARDANDO_ANALISE` (1) → `EM_ANALISE` (2) (Assumido por Analista)
2. `EM_ANALISE` (2) → `AGUARDANDO_REVISAO` (3) (Analista, se aprovado para revisão)
3. `EM_ANALISE` (2) → `RECUSADO` (8) (Analista, se recusado)
4. `AGUARDANDO_REVISAO` (3) → `EM_REVISAO` (4) (Assumido por Revisor)
5. `EM_REVISAO` (4) → `AGUARDANDO_APROVACAO` (5) (Revisor, após revisão)
6. `AGUARDANDO_APROVACAO` (5) → `EM_APROVACAO` (6) (Após o primeiro voto de Aprovador)
7. `AGUARDANDO_APROVACAO` (5) → `RECUSADO` (8) (Se houver voto 'não aprovado')
8. `EM_APROVACAO` (6) → `APROVADO` (7) (Se todos os 3 votos forem 'aprovado')
9. `EM_APROVACAO` (6) → `RECUSADO` (8) (Se houver voto 'não aprovado')

Qualquer outra transição de status resultará em erro.


