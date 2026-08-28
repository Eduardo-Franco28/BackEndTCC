---
name: backend
description: Padrões e convenções do backend do Eduka (Spring Boot 4 + Java 21 + PostgreSQL) — como criar entidades, endpoints, consultas, DTOs e regras de progresso seguindo a arquitetura que já existe. Use ao adicionar ou alterar qualquer coisa em src/main/java, ao criar rota nova, ao mexer em progresso/tentativas do aluno, ou quando a aplicação não compilar/subir.
---

# Backend do Eduka

API REST de um app educacional (TCC). O app React Native é outro projeto e
consome esta API. Conteúdo organizado em quatro níveis:
**Matéria → Tópico → Questão → Alternativa**.

Java 21, Spring Boot 4.0.3, PostgreSQL, JWT (Auth0), Lombok, Maven.
O projeto Maven fica em `app-mobile-tcc/` dentro do repositório git.

## Antes de mexer

1. Leia a entidade e o service do domínio que vai alterar — os padrões abaixo são
   seguidos em **todos** eles, e quebrar a consistência é pior que a melhoria
   proposta.
2. `Pre_documentation.txt` na raiz do projeto tem a documentação do TCC com o
   estado de cada funcionalidade.
3. Antes de terminar: `./mvnw compile`. Compilar **não** prova que sobe — derived
   query com nome errado só quebra no startup.

## Arquitetura

Camadas, cada uma com uma responsabilidade:

| Camada       | Faz                                     | Não faz                    |
| ------------ | --------------------------------------- | -------------------------- |
| `controller` | recebe a requisição, devolve a resposta  | nenhuma regra de negócio   |
| `service`    | valida, decide, calcula, orquestra       | não recebe HTTP            |
| `repository` | fala com o banco                         | não decide nada            |
| `entity`     | representa a tabela                      | não busca nada             |
| `dto`        | contrato com o app                       | nunca vaza entidade crua¹  |

¹ Regra a seguir em código novo. `TopicResponse` e `QuestionResponse` ainda
expõem entidade — é pendência, não exemplo a copiar.

**Organização por funcionalidade**, não por camada. Cada domínio tem sua pasta
com as camadas dentro:

```
users/  auth/  subjects/  topics/  questions/  alternatives/  progress/  shared/
   └── controller/ service/ repository/ entity/ dto/{request,response}/ enums/
```

### Regra de dependência (não crie ciclo)

- Service pode usar **o repository do próprio pacote** direto.
- Para dado de **outro** pacote, chame o **service** de lá, não o repository.
- Exceção: dentro do mesmo domínio (`progress` tem dois services usando o mesmo
  repository) — isso é correto, são leitura e escrita do mesmo diário.

Setas atuais, todas numa direção só:

```
TopicService ──> QuestionService ──> AlternativeService
     │
     └────────> QuestionProgressService ──> QuestionAttemptRepository

AttemptService ──> AlternativeService
               └─> QuestionProgressService
```

Se A chama B, B **nunca** chama A. O Spring quebra na subida com dependência
circular.

## Convenções de código

Seguidas em todo o projeto — mantenha:

- `@Autowired` em campo (não injeção por construtor).
- `this.` sempre antes de campo e método do próprio objeto.
- Identificadores em **inglês**, mensagens de erro em **português**.
- Listas de entidade: prefixo `lst` + nome da entidade no singular
  (`lstAlternative`, `lstQuestion`). Não vale para contagem nem para `List<Long>`.
- Records para DTO, sufixo `Request` / `Response`.
- Conversão entidade para DTO num método estático `from()` dentro do próprio
  Response.
- Operações que podem falhar no banco vão dentro de `try/catch` relançando
  `RuntimeException` com mensagem em português.

### Os dois tipos de método no service

Padrão presente em `QuestionService` e `AlternativeService`:

```java
// para OUTROS SERVICES: devolve a ENTIDADE
public Question getQuestion(Long id){
    return this.questionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Questão não encontrada"));
}

// para o CONTROLLER: devolve o DTO
public QuestionResponse get(Long id){
    Question question = this.getQuestion(id);
    return QuestionResponse.from(question, false);
}
```

Quem monta DTO precisa da entidade. Quem já recebeu DTO não consegue montar outro.

## Modelo de dados

```
Subject 1──N Topic 1──N Question 1──N Alternative
                            │
User 1──N QuestionAttempt N──┘
```

Duas regiões: **conteúdo** (Subject até Alternative, igual para todos os alunos)
e **progresso** (QuestionAttempt, individual).

| Entidade          | Campos                                             |
| ----------------- | -------------------------------------------------- |
| `User`            | nome, email (único), password (BCrypt)              |
| `Subject`         | name (único)                                        |
| `Topic`           | title, subTitle, subject                            |
| `Question`        | title, topic, level, type, content, lstAlternative  |
| `Alternative`     | description, correct, question                      |
| `QuestionAttempt` | user, question, correct, answeredAt                 |

Schema criado pelo Hibernate (`generate-ddl=true`). Não há ferramenta de migração.

### Nada que varia por aluno vai na entidade de conteúdo

`Question` **não tem** campo `conclued`, e não deve ter. "Concluída" depende de
quem pergunta — sai do diário de tentativas.

O mesmo vale para percentual, posição atual e status: **são derivados, nunca
armazenados.** Guardar cria duas fontes de verdade que acabam divergindo.

`level` é exceção e **precisa** ficar na questão: ele é a ordem da trilha, igual
para todos. Sem ele o banco devolve as questões em ordem arbitrária e o "voltar
de onde parou" abre uma questão aleatória.

## A regra central: progresso

`QuestionAttempt` é um **diário append-only**: uma linha por clique em "Enviar",
com o veredito do envio inteiro. Nunca se atualiza linha, só se acrescenta.

**Acertou a questão** = marcou todas as alternativas corretas e nenhuma errada.
Questões podem ter mais de uma correta.

**Questão concluída** = existe ao menos um envio com `correct = true`. Nunca
acumule alternativas certas ao longo de vários envios — isso deixa passar quem
marca tudo e quem acerta uma por vez.

**Erros são gravados também.** Não condicione o save ao acerto: as linhas `false`
são o que alimenta os indicadores de dificuldade.

**Onde o aluno parou** = primeira questão do tópico, por `level`, sem envio
correto. Comparação em memória entre a lista ordenada e o `Set` de concluídas.

## Autenticação

Token JWT de 60 dias, assinado com `jwt.secret`, subject = **email do usuário**.

- Públicas: `POST /auth/login` e `POST /auth/register`. Todo o resto exige token.
- Identidade **sempre** de `@AuthenticationPrincipal User user`. Nunca aceite
  `userId` vindo da URL ou do body — seria trivial ler o progresso de outro aluno.
- O `SecurityFilter` **não lança exceção** com token inválido: ele apenas não
  autentica e deixa o Spring devolver 403. Mantenha assim — se ele lançar, até o
  login quebra quando há token velho no header.
- Trocar o email invalida o token (o subject é o email). Todo endpoint que altera
  email **precisa** devolver token novo em `AuthResponse`.

## Rotas atuais

| Rota                             | Auth | O que faz                            |
| -------------------------------- | ---- | ------------------------------------ |
| `POST /auth/register`            | não  | cadastra e já devolve token           |
| `POST /auth/login`               | não  | devolve token + usuário               |
| `GET /auth/me`                   | sim  | dados do próprio usuário              |
| `PATCH /auth/profile`            | sim  | nome e/ou email (exige senha atual)   |
| `PATCH /auth/password`           | sim  | troca senha (exige senha atual)       |
| `POST /subject`                  | sim  | cria matéria                          |
| `GET /subject`                   | sim  | lista matérias                        |
| `POST /topic`                    | sim  | cria tópico                           |
| `PUT /topic/{id}`                | sim  | altera tópico                         |
| `DELETE /topic/{id}`             | sim  | remove tópico                         |
| `GET /topic/{subjectId}/subject` | sim  | tópicos de uma matéria                |
| `GET /topic/{id}/activity`       | sim  | trilha do tópico + onde retomar       |
| `POST /question`                 | sim  | cria questão                          |
| `GET /question`                  | sim  | todas as questões (desenvolvimento)   |
| `GET /question/{id}`             | sim  | uma questão                           |
| `POST /alternative`              | sim  | cria alternativa                      |
| `GET /alternative`               | sim  | todas as alternativas (desenvolvimento) |
| `POST /progress/answer`          | sim  | registra resposta e corrige           |

Rotas de leitura são desenhadas **a partir da tela**, não a partir da tabela.
`/activity` devolve tudo que a tela de atividade precisa numa chamada só.

### Rotas com o mesmo formato colidem

`GET /topic/{a}` e `GET /topic/{b}` são a mesma rota para o Spring — ele não
distingue pelo nome do parâmetro. Já quebrou este projeto uma vez. Use um segmento
que diferencie: `/topic/{id}/activity`, `/topic/{id}/subject`.

## Detalhes que já causaram erro

- **`@Enumerated(EnumType.ORDINAL)`** grava a posição do valor no enum, não o
  nome. Inserir um valor no meio do enum muda o significado de todas as linhas já
  gravadas, em silêncio. Use `EnumType.STRING`. `Question.type` ainda está com
  ORDINAL — corrigir.

- **Derived query com nome errado só quebra no startup**, não na compilação. A
  propriedade é `correct`, não `isCorrect` (o `is` é só o getter do Lombok). Para
  comparar com booleano use o sufixo: `...CorrectTrue` / `...CorrectFalse`. Sem o
  sufixo, o Spring exige um parâmetro extra.

- **Relação é `@ManyToOne` + `@JoinColumn`, nunca `@Column`.** `@Column` só serve
  para valor simples.

- **Divisão inteira:** `2 * 100 / 4` está certo; `2 / 4 * 100` dá zero. Sempre
  multiplique antes de dividir, e trate lista vazia antes (divisão por zero).

- **Nunca consulte o banco dentro de laço.** Busque tudo antes, agrupe em memória.
  Toda tela nova deve ter um número **fixo** de consultas, independente da
  quantidade de itens.

- **`@OneToMany` é preguiçoso.** Tocar na coleção sem `JOIN FETCH` dispara uma
  consulta por item. Para listas, use a query com `LEFT JOIN FETCH` e `DISTINCT`
  (`LEFT` para não sumir com quem não tem filhos; `DISTINCT` porque a junção
  multiplica linhas).

- **Acrescentar campo em entidade com `@AllArgsConstructor` muda o construtor** —
  todos os `new` daquela entidade param de compilar.

- **Valide antes de alterar.** Se o `set` acontecer antes da validação, checagens
  que comparam com o valor antigo passam a comparar com o novo e nunca disparam.

- **`PATCH` é atualização parcial:** campo ausente significa "não mexer", nunca
  erro. Todo `set` dentro de `if (campo != null && !campo.isBlank())`. Um `set`
  solto apaga o dado quando o app não envia aquele campo.

- **Nunca devolva `correct` da alternativa.** O app receberia o gabarito. A
  correção acontece no servidor. `AlternativeResponse` tem só id e description.

- **DTO não contém entidade.** Além de vazar estrutura interna, com relação
  bidirecional gera recursão infinita na serialização.

- **Record não tem getter no padrão `getX()`** — o acessador é `x()`.

- **`content` da questão é uma String com JSON dentro**, não objeto. O app faz
  `JSON.parse`. No request ele chega como `JsonNode` (para o Postman ficar
  legível) e vira texto com `.toString()` antes de salvar. Não é validado quanto à
  estrutura — é o custo da flexibilidade de suportar vários tipos de atividade.

- **Nome de coluna reservado:** não use `order`. Prefira `position` ou `level`.

## Pendências conhecidas

Não são padrão a seguir — são coisas a corrigir:

- Todo erro é `RuntimeException`, então tudo vira **500**, inclusive dado inválido
  e registro não encontrado. Falta um tratador global mapeando 400/401/404.
- Sem perfis de acesso: qualquer usuário autenticado cria e apaga conteúdo.
  `UserEnum` existe mas está desligado.
- `AuthController` tem regra de negócio dentro (não há `UserService`) — é o único
  domínio fora do padrão.
- `getBySubject` devolve percentual 0 e status fixo; o cálculo por aluno não foi
  ligado.
- `getAll` de questões e alternativas devolvem sem filtro e sem progresso.
- `UserGrade` está órfão, sem uso.
- Sem testes além do de inicialização.
- Os nomes de variável de ambiente no `compose.yaml` divergem dos que a aplicação
  lê (`TCC_URL`, `TCC_USERNAME`, `TCC_PASSWORD`, `TCC_JWT_SECRET`).

## Antes de terminar

- `./mvnw compile` passa
- A aplicação **sobe** (derived query nova só é validada aí)
- Nenhuma consulta dentro de laço
- Nenhum dado que varia por aluno gravado em entidade de conteúdo
- Nenhum `correct` de alternativa saindo na resposta
- Identidade vindo de `@AuthenticationPrincipal`
- Rota nova não colide em formato com outra existente
