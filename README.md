# 🚛 LogAp Fleet Management System

Sistema de **Gestão de Frotas** desenvolvido como projeto full-stack com **Spring Boot** (Java 21) e **React**, contendo módulo de agendamento de manutenção (CRUD completo) e dashboard de análise com métricas extraídas via SQL.

---

## 📋 Funcionalidades

### Módulo CRUD — Agendamento de Manutenção
- ✅ Seleção de Veículo
- ✅ Data de Início e Data de Finalização Prevista
- ✅ Tipo de Serviço (Troca de Pneus, Motor, Óleo, Freios, Suspensão, Elétrica, Revisão Geral, Outros)
- ✅ Custo Estimado (R$)
- ✅ Status (Pendente, Em Realização, Concluída)
- ✅ Observações opcionais

### Dashboard de Análise (5 Métricas via SQL)
| # | Métrica | Descrição |
|---|---------|-----------|
| 1 | **Total de KM Percorrido** | Soma da quilometragem da frota ou de um veículo específico |
| 2 | **Volume por Categoria** | Quantidade de viagens por tipo de veículo (Leve vs Pesado) |
| 3 | **Cronograma de Manutenção** | Próximas 5 manutenções agendadas ordenadas por data |
| 4 | **Ranking de Utilização** | Veículo com maior quilometragem acumulada |
| 5 | **Projeção Financeira** | Soma do custo estimado das manutenções do mês atual |

### Diferenciais Implementados
- 🔐 **Autenticação JWT** — Tela de login e registro com tokens seguros
- ⚛️ **Frontend React** — SPA moderna com Vite, Recharts e design premium
- 🐳 **Docker** — Ambiente completo via Docker Compose

---

## 🏗️ Arquitetura

### Padrões Arquiteturais Utilizados

**Backend**: Foi adotada uma **Arquitetura em Camadas (Layered Architecture)**, que é o padrão mais sólido no ecossistema Spring Boot. O sistema está logicamente fatiado nas camadas de API e roteamento (`Controllers`), regras de negócio (`Services`), persistência de dados (`Repositories`) e manipulação de transferências usando o padrão de Data Transfer Object (`DTOs`).
* **Por que foi usada?** Garante a premissa de *Separation of Concerns* (Separação de Preocupações). Isolando as regras de negócio de como a sua API é exposta e de como seus dados são salvos em banco, a manutenção, a legibilidade e a implementação de testes se tornam consideravelmente mais fáceis. O uso da camada extra de DTOs em especial aumenta a segurança da API como um todo, impedindo as entidades brutas de serem expostas acidentalmente para o mundo externo.

**Frontend**: Foi adotada uma **Arquitetura Baseada em Componentes (Component-Based Architecture)** implementando uma **Single Page Application (SPA)** com as diretrizes do React. O repositório está subdividido em páginas (`Pages`), elementos modulares de UI (`Components`), consumidores de API (`Services` com Axios) e gerenciamento de contexto para dados unificados (`Context API` para autenticação).
* **Por que foi usada?** A modularização via componentes promove extrema reutilização de pedaços da interface, diminuindo repetição de código e facilitando correções pontuais da UI. Além disso, delegar toda a lógica de negócio do React e a comunicação do servidor em `services` em arquivos separados retira do componente uma complexidade desnecessária para apenas renderizar dados na tela, favorecendo um código mais limpo.

### Infraestrutura e Arquitetura Docker

O projeto foi totalmente containerizado utilizando **Docker** e orquestrado com **Docker Compose**, para garantir a reprodutibilidade do ambiente em qualquer máquina. A arquitetura de infraestrutura é composta por 3 containers isolados, comunicando-se através de uma rede customizada internamente.

**Containers**:
1. `logap-db` **(Banco de Dados)**: Container executando a imagem `postgres:16-alpine`. Mantém os dados da aplicação persistidos fisicamente utilizando um volume Docker (`postgres_data`), garantindo que os registros não sejam perdidos caso o container reinicie. Possui um `healthcheck` configurado para confirmar que o Postgres está pronto para receber conexões.
2. `logap-backend` **(API Spring Boot)**: Container construído a partir de uma imagem Java com Maven. Ele depende estritamente do `db`, aguardando seu *healthcheck* ficar saudável antes de iniciar a execução via `depends_on`, garantindo ausência de erros de conexão na hora de subir a aplicação.
3. `logap-frontend` **(React SPA + Nginx)**: Container gerado via *multi-stage build* que constrói os artefatos estáticos do Vite e os serve usando um servidor web super leve Nginx na porta 80 internamente (entregando na porta 3000 do host local do host nativo).

**Rede Customizada**
Foi criada uma rede do tipo ponte (`bridge`) chamada `logap-network`.
* **Qualificador dessa rede:** Todos os 3 serviços ingressam automaticamente nela, promovendo DNS interno e capacidade de auto-descoberta. Isso significa que o backend pode injetar na URL de host do banco de dados de modo simples `DB_HOST: db` sem se preocupar em qual IP interno o docker entregou para ele. Apenas nas pontas do Frontend e Backend expomos portas públicas (3000 e 8080 respectivamente).

### Estrutura de Diretórios

```
logap-fleet/
├── backend/                    # Spring Boot 3.2 (Java 21)
│   ├── src/main/java/com/logap/fleet/
│   │   ├── config/            # Security, CORS, Exception Handler
│   │   ├── controller/        # REST Controllers
│   │   ├── dto/               # Data Transfer Objects
│   │   ├── entity/            # JPA Entities
│   │   ├── repository/        # Spring Data JPA Repositories
│   │   ├── security/          # JWT Token Provider & Filter
│   │   └── service/           # Business Logic
│   ├── src/main/resources/
│   │   ├── db/schema.sql      # DDL do banco de dados
│   │   ├── db/data.sql        # Dados iniciais de demonstração
│   │   └── application.yml    # Configurações
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                   # React 18 + Vite
│   ├── src/
│   │   ├── components/        # Layout, Sidebar
│   │   ├── pages/             # Dashboard, Manutenção, Login
│   │   ├── services/          # Axios API client
│   │   └── context/           # AuthContext (JWT)
│   ├── Dockerfile
│   └── nginx.conf
├── docker-compose.yml
└── README.md
```

### Stack Tecnológico

| Camada | Tecnologia |
|--------|-----------|
| Backend | Spring Boot 3.2, Spring Security, Spring Data JPA |
| Frontend | React 18, Vite 5, React Router, Recharts |
| Autenticação | JWT (jjwt 0.12.5) |
| Banco de Dados | PostgreSQL 16 |
| DevOps | Docker, Docker Compose, Nginx |

---

## 🚀 Como Rodar o Projeto

### Opção 1: Docker Compose (Recomendado)

Pré-requisitos: Docker e Docker Compose instalados.

```bash
# Clonar o repositório
git clone https://github.com/seu-usuario/logap-fleet.git
cd logap-fleet

# Subir todos os serviços
docker-compose up --build

# Acessar:
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080
```

### Opção 2: Execução Local

**Backend:**
```bash
# Pré-requisitos: Java 21, Maven, PostgreSQL rodando na porta 5432
cd backend

# Criar o banco de dados
# CREATE DATABASE logap_fleet;

# Rodar a aplicação
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
# Acesse: http://localhost:5173
```

---

## 🔑 Credenciais de Acesso

O sistema já vem com um usuário administrador pré-cadastrado:

| Campo | Valor |
|-------|-------|
| Email | `admin@logap.com` |
| Senha | `admin123` |

Novos usuários podem se registrar pela tela de login.

---

## 🗃️ Decisões Técnicas

### Banco de Dados
O banco foi modelado com **4 tabelas**:
- `usuarios` — Autenticação e controle de acesso
- `veiculos` — Cadastro da frota com categorias Leve/Pesado
- `manutencoes` — CRUD de agendamento com tipos de serviço e status
- `viagens` — Histórico de viagens para cálculo de KM

Foram adicionadas as tabelas `usuarios` (para JWT auth) e `viagens` (para métricas de dashboard) além do escopo base.

### Consultas SQL do Dashboard
Cada métrica usa query nativa otimizada com `@Query(nativeQuery)` nos repositórios JPA:
- **Total KM**: `SUM(quilometragem)` com filtro opcional por veículo
- **Volume por Categoria**: `JOIN veiculos` + `GROUP BY categoria` + `COUNT`
- **Cronograma**: `WHERE data_inicio >= CURRENT_DATE AND status <> 'CONCLUIDA' ORDER BY data_inicio`
- **Ranking**: `LEFT JOIN viagens` + `GROUP BY veiculo` + `SUM(quilometragem) DESC`
- **Projeção Financeira**: `SUM(custo_estimado)` filtrado por `EXTRACT(MONTH/YEAR)`

### Segurança
- Senhas criptografadas com BCrypt
- Tokens JWT com expiração de 24h
- Interceptor Axios para refresh automático
- Rotas protegidas no frontend e backend

### Frontend
- Design System customizado com CSS Variables
- Tema dark com glassmorphism e gradientes
- Responsivo para desktop e mobile
- Gráficos interativos com Recharts
- Notificações toast para feedback ao usuário

---

## 📝 Script do Banco de Dados

O esquema completo está em [`backend/src/main/resources/db/schema.sql`](backend/src/main/resources/db/schema.sql) e os dados iniciais em [`backend/src/main/resources/db/data.sql`](backend/src/main/resources/db/data.sql).

### Justificativa das alterações
- **Tabela `usuarios`**: Necessária para implementar autenticação JWT
- **Tabela `viagens`**: Necessária para calcular as métricas de quilometragem e volume por categoria no dashboard
- **Campos de categoria no veículo**: Permite filtrar por Leve/Pesado conforme especificado nas métricas
- **Índices**: Adicionados para otimizar as consultas SQL do dashboard

---

## 🧪 Endpoints da API

### Autenticação
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/auth/login` | Login com email e senha |
| POST | `/api/auth/register` | Registro de novo usuário |

### Manutenções (CRUD)
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/manutencoes` | Listar todas as manutenções |
| GET | `/api/manutencoes/{id}` | Buscar manutenção por ID |
| POST | `/api/manutencoes` | Criar nova manutenção |
| PUT | `/api/manutencoes/{id}` | Atualizar manutenção |
| DELETE | `/api/manutencoes/{id}` | Deletar manutenção |

### Dashboard
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/dashboard` | Dados completos do dashboard |
| GET | `/api/dashboard/km/{veiculoId}` | KM de um veículo específico |

### Veículos
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/veiculos` | Listar todos os veículos |
| GET | `/api/veiculos/{id}` | Buscar veículo por ID |
