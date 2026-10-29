# ImpiloHealth-AI 🌸

A unified, AI-powered community health platform for Africa — personal health AI, women's health (cycle tracker + period care shop), smart nutrition, and a community donation hub (Princess Hamper Project). Built for the Huawei Innovation Competition and deployed on Huawei Cloud.

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | Angular |
| Backend | Spring Boot (Java) |
| Database | MySQL (Huawei RDS in production) |
| Cache | Redis (Huawei DCS in production) |
| AI | Gemini API (dev) → Huawei Pangu / ModelArts / HiLens |
| Hosting | Huawei Cloud (ECS, RDS, DCS, CCE) — af-south Johannesburg |

## Repository Structure

```
impilohealth-ai/
├── backend/            Spring Boot API
├── frontend/           Angular app
├── ai-services/        AI integrations (chat, cycle prediction, meal scan)
├── database/           SQL schema + migrations
├── infra/
│   ├── docker/         Dockerfiles
│   └── huawei-cloud/   Deployment notes/config (ECS, RDS, CCE)
├── docs/               Architecture, API docs, diagrams
├── .github/            CI, PR + issue templates
├── docker-compose.yml  Local dev (MySQL + Redis)
└── .env.example        Environment variable template
```

## Getting Started

```bash
git clone https://github.com/<org>/impilohealth-ai.git
cd impilohealth-ai
cp .env.example .env          # fill in your own values — never commit .env
docker compose up -d          # starts MySQL + Redis

# Backend
cd backend && mvn spring-boot:run

# Frontend (new terminal)
cd frontend && npm install && npm start
```

See `backend/README.md` and `frontend/README.md` for details.

## Branching

- `main` — stable, demo/submission-ready. Protected; PRs only.
- `develop` — integration branch. Branch from here.
- `feature/<area>-<short-name>` — e.g. `feature/backend-nutrition-api`, `feature/frontend-period-tracker`
- `fix/<short-name>`

Full workflow in [CONTRIBUTING.md](CONTRIBUTING.md).

## Security (public repo)

Never commit secrets: API keys, DB passwords, JWT secrets, Huawei AK/SK. Use `.env` (git-ignored) locally and environment variables / secret managers in deployment.

## Data & Privacy

Designed with POPIA compliance in mind: three-tier data access (NGO Admin / Regional Coordinator / System Admin).

## Team

Team Her_Code

## Licence

MIT — see [LICENSE](LICENSE).
