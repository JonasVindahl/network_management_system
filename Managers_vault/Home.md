# Network Management System Vault

This vault documents the Network Management System codebase in English.

## Start Here

- [[Architecture/System Overview|System Overview]]
- [[Architecture/Runtime and Security|Runtime and Security]]
- [[Architecture/Data Access and Persistence|Data Access and Persistence]]
- [[API/API Reference|API Reference]]
- [[Models/Database Schema|Database Schema]]
- [[Models/Java Models and DTOs|Java Models and DTOs]]
- [[Planning/Code Inventory|Code Inventory]]

## Domain Areas

- [[Domain/Normal Sales|Normal Sales]]
- [[Domain/Collective Sales|Collective Sales]]
- [[Domain/Materials Stock and Measurements|Materials, Stock, and Measurements]]
- [[Domain/Cooperatives Analytics Buyers|Cooperatives, Analytics, and Buyers]]
- [[Domain/Reports and PDFs|Reports and PDFs]]
- [[Domain/Notice Board|Notice Board]]
- [[Domain/Gamification|Gamification]]

## Operations

- [[Operations/Setup and Configuration|Setup and Configuration]]
- [[Operations/Build Test Deploy|Build, Test, and Deploy]]
- [[Operations/Runbook|Runbook]]

## Planning

- [[Planning/Known Gaps and Follow-ups|Known Gaps and Follow-ups]]
- [[Improvements/Improvement Log|Improvement Log]]

## Observed Scope

The repository is a Spring Boot backend with server-rendered HTML screens, REST endpoints, PostgreSQL persistence, JWT security, PDF report rendering, scheduled gamification jobs, Docker deployment files, and a starter SQL schema.

Documentation is based on observed files in this repository:

- Java source under `src/main/java/dk/aau/network_management_system`
- templates and static pages under `src/main/resources`
- database schema under `Database/DMS_db_schema.sql`
- application and deployment files at the repository root
- GitHub Actions workflow under `.github/workflows/deploy.yml`

