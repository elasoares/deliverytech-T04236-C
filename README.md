
# 🚀 Delivery API

API REST de sistema de delivery desenvolvida com Spring Boot, com autenticação JWT, métricas e monitoramento e cache.

---

## 📋 Sumário
- [Tecnologias](#tecnologia)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Como rodar](#como-rodar)
- [Endpoints](#endpoints)
- [Autenticação JWT](#autenticação-jwt)
- [Monitoramento](#monitoramento)
- [Testes](#testes)
- [Docker](#docker)
- [CI/CD](#cicd)


## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
| --- | --- | --- |
| Java | 21 | Linguagen principal
| Spring Boot | 3.4.5 | Framework principal |
| Spring Security | 6.x | Autenticação e autorização|
| Spring Data JPA | 6.x | Persistência de dados|
| H2 Database | - | Banco de dados em mémoria|
| JWT (JJWT) | 0.12.6 | Token de autenticação |
| Lombok | - | Redução de boilerplate |
| SpringDoc OpenAPI | 2.8.9 | Documentação Swagger |
| Micrometer + Prometheus | - | Métricas |
| Grafana | - | Dashboards |
| Zipkin | - | Rastreamento distribuído |
| Docker | - | Containerização |
| GitHub Actions |  - | CI/CD |

---

## 🏗️ Arquitetura

```
src/
|--- main/
|    |--java/com/deliverytech/delivery/
|    |   |-- config/
|    |   |-- controller/
|    |   |-- dto/
|    |   |    |-- request/
|    |   |    |-- response/
|    |   |-- enums/
|    |   |-- exception/
|    |   |-- health/
|    |   |-- metrics/
|    |   |-- model/
|    |   |-- repository/
|    |   |-- security/
|    |   |-- service/
|    |   |-- validation/
|    |__ resources
|         |-- application.properties 
|         |-- logback-spring.xml
|__ test/
    |__ java/com/deliverytech/delivery/
        |__ config
        |__ controller
        |__ service

```
--- 

## ✅ Pré-requisitos

- Java 21
- Maven 3.9+
- Docker Desktop
---

## ▶️ Como rodar

### sem docker
```bash
# Clona o repositório
github clone https://github.com/elasoares/deliverytech-T04236-C

# Sobre a aplicação
mvn spring-boot:run
``` 