# Graph Report - src  (2026-10-08)

## Corpus Check
- Corpus is ~5,305 words - fits in a single context window. You may not need a graph.

## Summary
- 373 nodes · 1001 edges · 15 communities (11 shown, 4 thin omitted)
- Extraction: 95% EXTRACTED · 5% INFERRED · 0% AMBIGUOUS · INFERRED: 55 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Autenticação e Usuário
- Alternativas
- Questões e Progresso (API)
- Repositórios e Consultas
- Tentativas e Correção
- Tabuleiro (Board)
- Entidades JPA
- Tópicos e Atividades
- JWT e Filtro de Segurança
- Configuração Spring Security
- Matérias (Subjects)
- Série Escolar (UserGrade)
- Papéis de Usuário
- Bootstrap da Aplicação
- Testes

## God Nodes (most connected - your core abstractions)
1. `User` - 45 edges
2. `Question` - 35 edges
3. `QuestionResponse` - 25 edges
4. `Alternative` - 24 edges
5. `Topic` - 22 edges
6. `QuestionService` - 21 edges
7. `Subject` - 18 edges
8. `TopicService` - 18 edges
9. `AlternativeService` - 17 edges
10. `Board` - 17 edges

## Surprising Connections (you probably didn't know these)
- `QuestionResponse` --references--> `AlternativeResponse`  [EXTRACTED]
  main/java/com/iment/app_mobile_tcc/questions/dto/response/QuestionResponse.java → main/java/com/iment/app_mobile_tcc/alternatives/dto/response/AlternativeResponse.java
- `AlternativeRepository` --references--> `Alternative`  [EXTRACTED]
  main/java/com/iment/app_mobile_tcc/alternatives/repository/AlternativeRepository.java → main/java/com/iment/app_mobile_tcc/alternatives/entity/Alternative.java
- `AlternativeService` --references--> `QuestionRepository`  [EXTRACTED]
  main/java/com/iment/app_mobile_tcc/alternatives/service/AlternativeService.java → main/java/com/iment/app_mobile_tcc/questions/repository/QuestionRepository.java
- `CorrectionService` --references--> `AlternativeService`  [EXTRACTED]
  main/java/com/iment/app_mobile_tcc/progress/service/CorrectionService.java → main/java/com/iment/app_mobile_tcc/alternatives/service/AlternativeService.java
- `QuestionService` --references--> `AlternativeService`  [EXTRACTED]
  main/java/com/iment/app_mobile_tcc/questions/service/QuestionService.java → main/java/com/iment/app_mobile_tcc/alternatives/service/AlternativeService.java

## Import Cycles
- None detected.

## Communities (15 total, 4 thin omitted)

### Community 0 - "Autenticação e Usuário"
Cohesion: 0.07
Nodes (10): AuthService, AuthController, LoginRequest, NewPasswordRequest, NewProfileRequest, RegisterRequest, AuthResponse, UserResponse (+2 more)

### Community 1 - "Alternativas"
Cohesion: 0.09
Nodes (11): AlternativeController, AlternativeRequest, AlternativeResponse, AlternativeRepository, AlternativeService, QuestionTypeEnum, DRAG_DOTS, DRAG_LETTERS (+3 more)

### Community 2 - "Questões e Progresso (API)"
Cohesion: 0.13
Nodes (7): ProgressController, ProgressService, QuestionController, QuestionRequest, QuestionResponse, QuestionService, TopicService

### Community 3 - "Repositórios e Consultas"
Cohesion: 0.10
Nodes (4): QuestionAttemptRepository, QuestionRepository, SubjectRepository, TopicRepository

### Community 4 - "Tentativas e Correção"
Cohesion: 0.14
Nodes (9): AttemptAlternativeController, CorrectionResult, AttemptAlternativeRequest, AnsweredAlternativeResponse, AttemptService, CorrectionService, QuestionSlot, QuestionSlotContent (+1 more)

### Community 5 - "Tabuleiro (Board)"
Cohesion: 0.13
Nodes (7): BoardController, BoardSlot, BoardRequest, BoardResponse, Board, BoardRepository, BoardService

### Community 6 - "Entidades JPA"
Cohesion: 0.20
Nodes (5): Alternative, QuestionAttempt, Question, Subject, Topic

### Community 7 - "Tópicos e Atividades"
Cohesion: 0.11
Nodes (8): TopicController, TopicRequest, ActivityResponse, TopicResponse, TopicStatusEnum, CONCLUIDO, EM_ANDAMENTO, NAO_INICIADO

### Community 10 - "Matérias (Subjects)"
Cohesion: 0.21
Nodes (4): SubjectController, SubjectRequest, SubjectResponse, SubjectService

### Community 11 - "Série Escolar (UserGrade)"
Cohesion: 0.22
Nodes (8): UserGrade, NONO_ANO, OITAVO_ANO, PRIMEIRO_MEDIO, SEGUNDO_MEDIO, SETIMO_ANO, SEXTO_ANO, TERCEIRO_MEDIO

### Community 12 - "Papéis de Usuário"
Cohesion: 0.33
Nodes (3): UserEnum, ADMIN, USER

## Knowledge Gaps
- **17 isolated node(s):** `DRAG_DOTS`, `DRAG_TO_SLOTS`, `DRAG_LETTERS`, `DRAG_SLOTS_TO_GROUP`, `DRAG_SUBTRACTION` (+12 more)
  These have ≤1 connection - possible missing edges. (Counts symbols only; 103 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **4 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `User` connect `Autenticação e Usuário` to `Questões e Progresso (API)`, `Repositórios e Consultas`, `Tentativas e Correção`, `Entidades JPA`, `Tópicos e Atividades`, `JWT e Filtro de Segurança`?**
  _High betweenness centrality (0.191) - this node is a cross-community bridge._
- **What connects `DRAG_DOTS`, `DRAG_TO_SLOTS`, `DRAG_LETTERS` to the rest of the system?**
  _17 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Autenticação e Usuário` be split into smaller, more focused modules?**
  _Cohesion score 0.06848357791754019 - nodes in this community are weakly interconnected._
- **Why does `Question` connect `Entidades JPA` to `Alternativas`, `Questões e Progresso (API)`, `Repositórios e Consultas`, `Tentativas e Correção`, `Tabuleiro (Board)`?**
  _High betweenness centrality (0.072) - this node is a cross-community bridge._
- **Should `Alternativas` be split into smaller, more focused modules?**
  _Cohesion score 0.08771929824561403 - nodes in this community are weakly interconnected._
- **Why does `QuestionTypeEnum` connect `Alternativas` to `Questões e Progresso (API)`, `Entidades JPA`?**
  _High betweenness centrality (0.046) - this node is a cross-community bridge._
- **Should `Questões e Progresso (API)` be split into smaller, more focused modules?**
  _Cohesion score 0.12762762762762764 - nodes in this community are weakly interconnected._