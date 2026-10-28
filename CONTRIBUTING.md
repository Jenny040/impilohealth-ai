# Contributing

## Workflow

1. Pull latest: `git checkout develop && git pull`
2. Branch: `git checkout -b feature/<area>-<name>`
   - Areas: `backend`, `frontend`, `ai`, `infra`, `docs`
3. Commit small and often using the format below.
4. Push and open a Pull Request into `develop` (never directly into `main`).
5. At least one teammate reviews before merging.
6. `main` is updated only from `develop` when stable (demo / submission points).

## Commit messages

`type(scope): short description`

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

Examples:
- `feat(backend): add donation endpoints`
- `feat(frontend): period tracker calendar`
- `chore(infra): add docker-compose for mysql and redis`

## Ownership (suggested)

| Area | Folder | Owner |
|---|---|---|
| Backend APIs | `backend/` | Jennifer |
| AI + Huawei Cloud | `ai-services/`, `infra/` | Tsholofelo |
| Angular UI | `frontend/` | Ofentse |

To avoid merge conflicts, stay inside your own folder where possible.

## Rules for a public repo

- No secrets, ever (keys, passwords, AK/SK). If one leaks, rotate it immediately — deleting the commit is not enough.
- No real user or health data. Use dummy seed data only.
