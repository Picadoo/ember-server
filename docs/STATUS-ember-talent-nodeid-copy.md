# STATUS · B2.14 talent `nodeId` / 前置 copy A

| Field | Value |
|---|---|
| Date | 2026-09-29 00:19 Asia/Shanghai |
| Role | 余烬-插件 executor |
| Design tip | `9e129cc` |
| Approval | `1460fa0` · APPROVED A |
| Scope | `plugins/TrMenu/menus/ember_talent.yml` only |
| Result | 13 `§8nodeId:` lore lines deleted; 7 prerequisite lines humanized |

## Static acceptance

- `rg '§8nodeId:' plugins/TrMenu/menus/ember_talent.yml` → 0
- `rg '§8前置：.*(blaze_|ash_|warden_)' plugins/TrMenu/menus/ember_talent.yml` → 0
- Chinese prerequisite total → 7: `燃眼 + 饮烬` ×1, `余烬脉` ×2, `灰纱` ×2, `叠壁` ×2
- Target diff → 13 complete-line deletions + 7 exact prerequisite replacements; `git diff --check` clean

## Reload / delivery

- Hot reload issued: `printf 'trmenu reload\n' > server-runtime/console.in`
- Runtime evidence: `server-runtime/logs/latest.log` at `00:18:52` Asia/Shanghai — TrMenu automatically reloaded `ember_talent.yml` successfully (6ms), followed by 36 menus loaded successfully at `00:19:36` (38ms).
- Suggested commit: `fix(trmenu): B2.14 talent §8 nodeId/prereq humanize`
- Push target: `origin/main`

## Prohibited scope

EMPTY. No plan B, bare attribute keys, `talent.yml`, unlock arguments, other menus, or runtime/player/world files were changed. B0.1 is not claimed cleared.
