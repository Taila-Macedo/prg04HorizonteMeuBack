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
## 📦 Modelagem de Domínio & Entidades

O sistema é composto por 09 classes:

| # | Classe |  Descrição Resumida |
| :--- | :--- | :--- |
| **1** | `Usuario` |Classe central. Controla acesso via Enum Perfil (USUARIO/ADMINISTRADOR). |
| **2** | `PontoTuristico` | Destino turístico. Cadastrado apenas pelo administrador. |
| **3** | `Foto` | Imagem enviada por usuário para a galeria de um ponto turístico. |
| **4** | `Comentario` | Texto + nota + curtidas + foto opcional publicado em um ponto. |
| **5** | `Favorito` | Ponto salvo pelo usuário. |
| **6** | `Roteiro` | Lista ordenada de pontos para uma viagem. |
| **7** | `RoteiroNoPonto` | Intermediária: ponto em roteiro com ordem e checklist. |
| **8** | `Denuncia` | Reporte com FKs diretas para Foto, Comentario ou Usuario. |
| **9** | `Notificacao` | Aviso automático gerado pelo sistema. |


---

## 🗂️ Enumerações (Enums)

Cada campo do tipo Enum do sistema roda como uma classe Java separada com a anotação `@Enum`:

| Enum | Valores | Usado em |
| :--- | :--- | :--- |
| **Perfil** | `USUARIO`, `ADMINISTRADOR` | `Usuario.perfil` |
| **Status Denuncia** | `PENDENTE`, `RESOLVIDA`, `REJEITADA` | `Denuncia.status`  |
| **TipoNotificacao** | `CURTIDA`, `COMENTARIO` | `Notificacao.tipo`  |
| **Categoria** | `PRAIA`, `MUSEU`, `MONTANHA`, `MONUMENTO`, `PARQUE` | `PontoTuristico.categoria` |

---

## 🔗 Relacionamentos entre Classes

### 📐 Associações 1:N (Um para Muitos)

| Classe Origem | Cardinalidade | Classe Destino | Descrição |
| :--- | :--- | :--- | :--- |
| **Usuario** | 1-N | `Foto` | Um usuário pode enviar várias fotos. |
| **Usuario** | 1-N | `Comentario` | Um usuário pode publicar vários comentários. |
| **Usuario** | 1-N | `Favorito` | Um usuário pode salvar vários pontos como favoritos. |
| **Usuario** | 1-N | `Roteiro` | Um usuário pode criar vários roteiros. |
| **Usuario** | 1-N | `Denuncia` | Um usuário pode fazer várias denúncias. |
| **Usuario** | 1-N | `Notificacao` | Um usuário recebe várias notificações. |
| **PontoTuristico** | 1-N | `Foto` | Um ponto pode tener várias fotos na galeria. |
| **PontoTuristico** | 1-N | `Comentario` | Um ponto pode receber vários comentários. |
| **PontoTuristico** | 1-N | `Favorito` | Um ponto pode ser favoritado por vários usuários[cite: 25]. |
| **Roteiro** | 1-N | `RoteiroNoPonto` | Um roteiro pode ter vários pontos na lista. |
| **Foto** | 1-N | `Denuncia` | Uma foto pode receber várias denúncias. |
| **Comentario** | 1-N | `Denuncia` | Um comentário pode receber várias denúncias. |

### 🔄 Relações N:N via Entidade Intermediária

| Classe Origem | Cardinalidade | Classe Destino | Resolvido Por | Descrição |
| :--- | :--- | :--- | :--- | :--- |
| **Roteiro** | $N\rightarrow N$ | `PontoTuristico` | **RoteiroNoPonto** | Armazena a sequência (`ordem`) e o checklist (`visitado`). |

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
