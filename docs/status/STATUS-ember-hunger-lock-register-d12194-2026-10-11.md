# STATUS · EmberHungerLock register D12194 · 2026-10-11

- CoreRpg **1.65.723-d12194.local**: register `EmberHungerLock` in `CoreRpgPlugin` (was in jar, never registered).
- Source + unit test committed; default `hunger_lock` true via `m.b("hunger_lock", true)`.
- `server-runtime/ops.json` → `[]` (HEAD had FreshQ ops).
