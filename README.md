# 🧭 Horizonte Meu — Backend

Plataforma interativa para o mapeamento de metas e sonhos de viagem. Explore destinos globais através de um mapa dinâmico, crie roteiros turísticos reais e monte uma lista de desejos completamente personalizada.

> API REST desenvolvida como backend do projeto **Horizonte Meu**, construída com Spring Boot seguindo arquitetura em camadas.

---

## 📁 Estrutura do Projeto

```
src/
└── main/
    └── java/
        └── com.ifba.horizontemeu/
            ├── controller/       → Endpoints da API REST
            ├── entity/           → Classes de modelo (entidades JPA)
            ├── repository/       → Acesso ao banco de dados
            └── service/          → Regras de negócio
```

---

## 📡 Endpoints da API

Base URL: `http://localhost:8080`

### 👤 Usuários `/usuarios`

| Método | Rota | Descrição |
|---|---|---|
| GET | `/usuarios/findall` | Lista todos os usuários |
| GET | `/usuarios/findbyid/{id}` | Busca usuário por ID |
| GET | `/usuarios/findbynome?nome=` | Busca usuário por nome |
| POST | `/usuarios/save` | Cadastra novo usuário |
| PUT | `/usuarios/update/{id}` | Atualiza usuário |
| DELETE | `/usuarios/delete/{id}` | Remove usuário |

---

## 🏗️ Arquitetura

O projeto segue a arquitetura em camadas:

```
Controller → Service → Repository → Banco de Dados
```

- **Controller** — recebe as requisições HTTP e retorna respostas JSON
- **Service** — contém as regras de negócio
- **Repository** — realiza o acesso ao banco de dados via JPA
- **Entity** — representa as tabelas do banco de dados

---
## 🚀 Tecnologias Utilizadas

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java" />
  <img src="https://img.shields.io/badge/Spring Boot-3.5.14-brightgreen?style=for-the-badge&logo=springboot" />
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven" />
  <img src="https://img.shields.io/badge/H2-Database-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Lombok-pink?style=for-the-badge" />
</p>

---
## 👩‍💻 Autora

Desenvolvido por **Taíla Macedo** como parte do projeto **Horizonte Meu** — disciplina PRG04 Web, IFBA Campus Irecê.

---

<p align="center">🧭 Horizonte Meu — Seu Atlas de Sonhos Interativo</p>
