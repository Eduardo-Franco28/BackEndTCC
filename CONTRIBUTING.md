# Como contribuir no BackEndTCC

Guia para abrir um Pull Request (PR) neste repositório. PR é um pedido de
alteração: você propõe mudanças numa branch separada e alguém revisa antes de
entrar no código principal.

Nada é enviado direto para `main` ou `develop` — sempre passa por PR.

## As branches

| Branch      | Para que serve                                      |
| ----------- | --------------------------------------------------- |
| `main`      | versão estável, só recebe merge da `develop`         |
| `develop`   | onde o trabalho se junta — **é aqui que seu PR entra** |
| `feat/...`  | sua branch de trabalho                               |

## Antes de começar

1. Peça ao Eduardo para te adicionar como colaborador
   (Settings > Collaborators). Você recebe um convite por e-mail — precisa aceitar.
2. Clone o repositório:

```
git clone https://github.com/Eduardo-Franco28/BackEndTCC.git
```

3. O projeto Maven fica na pasta `app-mobile-tcc/` dentro do repositório.
   As variáveis de ambiente (`TCC_URL`, `TCC_USERNAME`, `TCC_PASSWORD`,
   `TCC_JWT_SECRET`) precisam estar configuradas ou a aplicação não sobe.

## O passo a passo

### 1. Atualize a develop

Sempre comece do código mais recente, senão seu PR nasce desatualizado.

```
git checkout develop && git pull
```

### 2. Crie sua branch

```
git checkout -b feat/nome-da-alteracao
```

### 3. Faça as alterações e commite

```
git add . && git commit -m "feat: descricao do que voce fez"
```

Commits pequenos e frequentes são melhores que um commit gigante — facilitam a
revisão.

### 4. Envie para o GitHub

```
git push -u origin feat/nome-da-alteracao
```

O `-u` só é necessário no primeiro push da branch. Depois basta `git push`.

### 5. Abra o PR

Ao abrir o repositório no GitHub aparece um aviso **"Compare & pull request"**.
Clique nele.

**Confira o campo `base`.** O GitHub sugere `main` por padrão — troque para
`develop`.

```
base: develop   <-  compare: feat/nome-da-alteracao
```

Escreva um título curto e, na descrição, o que mudou e por quê. Se resolve algo
que foi conversado, mencione.

### 6. Aguarde a revisão

O Eduardo recebe a notificação, abre a aba **Files changed** e pode:

- **Approve** — aprovado, ele faz o merge
- **Request changes** — pediu ajustes, veja os comentários
- **Comment** — só comentou, sem decisão

Se pedirem ajustes, é só commitar e dar push **na mesma branch**. O PR atualiza
sozinho, não precisa abrir outro.

## Padrão de nomes

Branches e commits usam o mesmo prefixo:

| Prefixo   | Quando usar                          |
| --------- | ------------------------------------ |
| `feat:`   | funcionalidade nova                  |
| `fix:`    | correção de erro                     |
| `refact:` | reorganização sem mudar comportamento |
| `docs:`   | só documentação                      |

Exemplos: `feat/endpoint-atividade`, `fix/validacao-email`.

## Problemas comuns

- **"Method 'GET' is not supported"** — a rota existe para outro verbo. Confira
  no controller se o método que você está chamando está mapeado.

- **PR apontando para `main`** — o mais comum. Dá para corrigir sem fechar o PR:
  clique em **Edit** ao lado do título e troque a base para `develop`.

- **Conflito de merge** — alguém alterou o mesmo trecho antes de você. Atualize
  sua branch com `git pull origin develop`, resolva os conflitos nos arquivos
  marcados, commite e dê push.

- **Esqueceu de criar a branch e commitou na `develop`** — não dê push. Crie a
  branch a partir de onde está (`git checkout -b feat/nome`), que os commits vão
  junto.

- **A aplicação não sobe** — provavelmente as variáveis de ambiente não estão
  configuradas. Sem elas o banco não conecta.

## Antes de marcar como pronto

- O projeto compila (`./mvnw compile`)
- A aplicação sobe sem erro
- Você testou o que alterou (Postman ou pelo app)
- Não subiu senha, token nem arquivo de IDE
