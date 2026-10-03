# STATUS — Paper 1.12.2 core-modding workspace

**Updated:** 2026-09-09 11:58 Asia/Shanghai (UTC+8)

## Summary

| Area | Outcome |
|------|---------|
| JDK 8 | **OK** Temurin `1.8.0_504` at `/workspace/minecraft/tools/jdk8u504-b01` |
| Maven | **OK** `3.9.16` at `/workspace/minecraft/tools/apache-maven-3.9.16` |
| Paper source checkout | **OK** commit `e9c4141dab5ec2df7194b75b8ab34c7242c482a6` branch `paper-1.12.2-1620` in `/workspace/minecraft/Paper` |
| Paper `./paper jar` / Maven | **OK** `Paper-Server/target/paper-1.12.2.jar` built (BUILD SUCCESS). Paperclip packaging not finished (optional; stock jar used for tests). |
| Stock test server + NeigeItems + CoreSmelt | **OK** |
| Wipe vanilla furnace recipes | **OK** removed **67**; registered **4** NI-only recipes |
| Mineflayer smoke | **OK** (`SMOKE_EXIT=0`) |
| `/coresmelt check` via bot | **OK** — 4 NI recipes present |

## Paths

| What | Absolute path |
|------|----------------|
| Stock Paper jar | `/workspace/minecraft/paper/paper-1.12.2-1620.jar` |
| Rebuilt Paper jar | `/workspace/minecraft/Paper/Paper-Server/target/paper-1.12.2.jar` |
| JDK | `/workspace/minecraft/tools/jdk8u504-b01` |
| Maven | `/workspace/minecraft/tools/apache-maven-3.9.16` |
| Paper source | `/workspace/minecraft/Paper` |
| Plugins dir | `/workspace/minecraft/plugins/` |
| NeigeItems jar | `/workspace/minecraft/plugins/NeigeItems-1.21.151.jar` |
| NI item YAML | `/workspace/minecraft/plugins/NeigeItems/Items/ember-furnace.yml` |
| CoreSmelt jar | `/workspace/minecraft/plugins/CoreSmelt.jar` |
| CoreSmelt source | `/workspace/minecraft/CoreSmelt/` |
| Server runtime | `/workspace/minecraft/server-runtime/` |
| Mineflayer tests | `/workspace/minecraft/mineflayer-tests/` |
| Furnace design | `/workspace/minecraft/docs/design/DESIGN-furnace.md` |
| Furnace status detail | `/workspace/minecraft/docs/status/STATUS-furnace.md` |
| NMS hook notes | `/workspace/minecraft/HOOKS.md` |
| Vanilla jar cache | `/workspace/minecraft/cache/minecraft_server.1.12.2.jar` |
| Build log | `/workspace/minecraft/paper-build.log` |

## How to rebuild Paper from source

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
export PATH="$JAVA_HOME/bin:/workspace/minecraft/tools/apache-maven-3.9.16/bin:$PATH"
export GIT_AUTHOR_NAME='Paper Build' GIT_AUTHOR_EMAIL='paper-build@local'
export GIT_COMMITTER_NAME='Paper Build' GIT_COMMITTER_EMAIL='paper-build@local'
cd /workspace/minecraft/Paper
# Ensure vanilla jar MD5 71728ed3fbd0acd1394bf3ade2649a5c (scripts/remap.sh patched to use BuildData serverUrl)
./paper jar
# Or if patches already applied: mvn clean install && ./scripts/paperclip.sh "$(pwd)"
```

**Blockers fixed during build:**

1. Dead S3 Mojang URL → patched `scripts/remap.sh` to use `BuildData/info.json` `serverUrl`; cached jar under `cache/`.
2. Missing git committer identity → set local git user + `GIT_*` env for patch `am` commits.

**Optional:** `paperclip.jar` packaging via `./scripts/paperclip.sh` (needs Paperclip Maven module). Rebuilt shaded server jar already usable: `Paper-Server/target/paper-1.12.2.jar`.

## How to run the test server

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
cd /workspace/minecraft/server-runtime
./start.sh stock      # official 1620 jar (recommended for NI/CoreSmelt tests)
# ./start.sh rebuilt  # uses Paper-Server target / paperclip if present
./stop.sh
```

**Note:** Prefer keeping Java in a long-lived background shell (sandbox may reap orphaned `nohup` children). Example:

```bash
cd /workspace/minecraft/server-runtime
"$JAVA_HOME/bin/java" -Xms512M -Xmx1536M -jar /workspace/minecraft/paper/paper-1.12.2-1620.jar nogui
```

Config: `eula=true`, `online-mode=false`, `max-players=40`, port `25565`.  
`plugins/` → symlink to `/workspace/minecraft/plugins/` (NeigeItems + CoreSmelt + NI data).

## Furnace redesign (delivered on stock jar)

- Design: `/workspace/minecraft/docs/design/DESIGN-furnace.md`
- Wipe all `FurnaceRecipe` on enable; register NI chain from CoreSmelt `config.yml`
- Non-NI inputs cancelled via `FurnaceSmeltEvent` (Material-keyed recipes on 1.12.2)
- Item IDs: `ore_ember_iron` → `ingot_ember_iron`, `ore_ember_gold` → `ingot_ember_gold`, `ore_ember_coal` → `crystal_ember_carbon`, `raw_ember_flesh` → `steak_ember`

Rebuild CoreSmelt:

```bash
export JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01
export PATH="$JAVA_HOME/bin:/workspace/minecraft/tools/apache-maven-3.9.16/bin:$PATH"
cd /workspace/minecraft/CoreSmelt && mvn -q clean package && cp target/CoreSmelt.jar /workspace/minecraft/plugins/
```

## How to run mineflayer smoke

```bash
cd /workspace/minecraft/mineflayer-tests
npm install   # already done
npm run smoke
# optional: node scripts/check-furnace.js   # runs /coresmelt check as Tester
```

**Note:** Installed `mineflayer@4.39` declares `engines.node >=22`; works on Node 20 with engine warnings.

## Division of labor

| Concern | Owner |
|---------|--------|
| Custom items | NeigeItems only |
| Furnace recipe wipe + NI smelt chain | CoreSmelt plugin (stock jar path) |
| Future brewing / crafting / fishing core | Paper fork patches — see `HOOKS.md` |

## Remaining / next

1. Finish `paperclip.sh` if a bootstrap paperclip jar is preferred over shaded `paper-1.12.2.jar`.
2. Core NMS patches for brewing / crafting / fishing (`HOOKS.md`) when ready — furnace already handled at plugin layer.
3. Optional: NMS cook-time override (1.12.2 Bukkit has no cookTime on FurnaceRecipe).
