# Huawei Technologies in ImpiloHealth-AI

Source: the team's FINAL Proposal (Section 6) and roadmap (Section 11). The competition requires the
Huawei technologies to be shown in the architecture/flow diagram or in code — this file is the map.

Status: **Built** = in the repo now. **Ready** = code path/config exists, needs Huawei credentials/deployment. **Planned** = roadmap.

| Huawei technology | Role in the product | Where in this repo | Status |
|---|---|---|---|
| **Pangu NLP** | Multilingual health chatbot | `backend/.../ai/PanguChatClient.java`, `app.ai.*` in `application.yml` | Ready (set `AI_PROVIDER=pangu`, endpoint, key) |
| **Pangu Prediction** | Cycle prediction, mood patterns | `backend/.../cycle/` (next) — rolling average of last 3 cycles first, Pangu later | Planned |
| **Pangu Multimodal** | Voice / image input | `backend/.../ai/` (next) | Planned |
| **HiLens** | Meal scanner (local food recognition) | `backend/.../nutrition/` (next) behind a `MealScanClient` interface | Planned |
| **ModelArts** | Fine-tune/host localised health model | `ai-services/` (training notes + model config) | Planned |
| **EI Health** | Validate symptom guidance | `ai-services/` | Planned |
| **GaussDB** | User health profiles, cycle logs, chat history | Same JDBC config (`DB_URL`); GaussDB for MySQL is MySQL-compatible, so migrating from local MySQL is a connection-string change | Ready (confirm on your instance) |
| **DataArts Studio** | Anonymise/aggregate data (k-anonymity) before sharing | `database/` + `docs/` pipeline notes | Planned |
| **ECS** | Hosts the Spring Boot backend | `infra/docker/backend.Dockerfile` | Ready |
| **CCE** | Containerised deployment, CI/CD | `infra/docker/backend.Dockerfile`, `infra/huawei-cloud/` manifests (next) | Ready (image), manifests next |
| **FunctionGraph** | Donation triggers, SMS reminders, scheduled cycle reminders | `infra/huawei-cloud/` (next) | Planned |
| **GeminiDB** | Sessions, time-series health trends | — | Planned |
| **IoTDA** | Wearables | — | Planned |
| **MindSpore** | Lightweight on-device models (Android) | — | Planned |
| **Huawei Cloud Stack** | On-premises for government | — | Planned |

## Backend design choices that support this

- **Provider interface** (`HealthChatClient`): Pangu in production, Gemini as a dev fallback, a mock for tests and offline demos. Switch with `AI_PROVIDER`; no other code changes.
- **Unified Health Profile**: every chat turn is stored in `chat_logs` and the last 10 turns are sent to the model on the next message, giving the "AI remembers you" behaviour from the proposal.
- **POPIA audit logging**: `audit_logs` records who did what, to which resource, when (AI chat today; data export/modify/delete as those modules arrive).
- **Three-tier access**: `Role` enum (`USER`, `NGO_ADMIN`, `REGIONAL_COORDINATOR`, `SYSTEM_ADMIN`) is already in JWTs and Spring Security authorities.
- **Region**: deploy to the Johannesburg region (`af-south-1`) so health data stays in-country.

## What to show the judges

The architecture diagram (Screen 14 in the Figma prompt) and a short code snippet of `PanguChatClient` plus `application.yml` (`app.ai.provider: pangu`). Before the demo, run at least the chat endpoint against a real Pangu deployment so the claim is demonstrably true.

## Open items (need your Huawei Cloud account)

1. Pangu / ModelArts Studio: deploy a model, copy the API endpoint, key and auth header into `.env`. Check the request/response shape matches `PanguChatClient` (it assumes an OpenAI-style chat-completions format).
2. Create GaussDB instance in `af-south-1`; set `DB_URL`, `MYSQL_USER`, `MYSQL_PASSWORD`.
3. Push the Docker image to SWR and deploy to ECS or CCE.
