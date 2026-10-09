# 余烬 · D388+D389：生活等级 PAPI + 生活页同屏 · 线上抽测

- **性质：观察期抽测 · `ember_life` I/G/K/H/J 挂 `life_*` · 购/兑同会话 update · 未改配置 / 未动开关 / 未改 life 价表 / 未关观察**
- 上游施工：P1 [`STATUS-ember-life-level-papi-d388-2026-10-10.md`](STATUS-ember-life-level-papi-d388-2026-10-10.md) tip **`fc26f9a0`** · P2 [`STATUS-ember-life-level-menu-d389-2026-10-10.md`](STATUS-ember-life-level-menu-d389-2026-10-10.md) tip **`8bb5e822`**
- DESIGN：[`DESIGN-ember-life-level-papi-2026-10-10.md`](../design/DESIGN-ember-life-level-papi-2026-10-10.md) §2.4 G3–G4
- 现态：bv**62** · `enabled=true` · `migrate=true` · `set_bonus=true` · play PID **929948** · jar **`1.65.103-d388.local`**
- 测号：`D388Life`；经代理 joinPlay；**未动真人档**
- 证据：`/workspace/tmp/d388-d389-life-level/`（`00-precheck` · `21-v1-papi-me` · `30-menu-before` · `40-i-left` · `50/51-buy-update` · `59-retest` · `90-endstate` · `99-results` · `run/retest.log`）
- 执行：2026-10-10 04:11–04:15 CST
- **结论：PASS** — I 三行真数 · G/K/H/J 剩余短签 · I 左键不强制 close · 购兑后 stay-open ≤500ms 刷新 · tip `8bb5e822`+`fc26f9a0`

## 人话

装 jar `1.65.103-d388.local` 后生活页 I 格一眼可见「生活 Lv.N（经验 x/next）」「今日兑尘 已用 a/4 · 剩 b」「本周孵化 已用 a/2 · 剩 b」；旧靴/碎骨与孵化格也有剩次短签。左键 I 只响铃提示、不关菜单；右键才刷聊天完整列表。点旧靴兑尘成功后，菜单还开着约 0.5 秒内剩次 4→3（复测再 3→2），经验 +1。`life.yml` / 三开关 / bv62 未动。观察不关。

## 验收表

| ID | 项 | 结果 | 证据 |
|----|----|------|------|
| **S0** | jar `1.65.103-d388.local` · 三开关 true · bv62 · tip 双齐 | **PASS** | Enabling 日志 · `version CoreRpg` · `00-precheck.json` · tip `8bb5e822`/`fc26f9a0` |
| **S1** | 静态：I 三键 + `update:20`；G/K/H/J 短签；I 左键无 close | **PASS** | `ember_life.yml` · `01-static-fix.json`（左键 tell-only；右键 close+`corerpg life`） |
| **G3/V1** | `/papi parse me` `life_level_line` / `soul_daily_line` / `hatch_weekly_line` 非空真数 | **PASS** | `21-v1-papi-me.json`：`生活 Lv.3（经验 61/150）` · `今日兑尘 已用 1/4 · 剩 3` · `本周孵化 已用 0/2 · 剩 2`（console `papi parse <name>` proxy 局找不到人，改 bot `/papi parse me`，同 D387） |
| **G4/V2** | 开 `ember_life`：I 三行 + G/K 兑尘短签 + H/J 孵化短签；占位已解析 | **PASS** | `30-menu-before.json` |
| **V3** | I 左键不强制 close | **PASS** | `40-i-left.json`：stillOpen=true · tell「生活进度见本格 lore（约 1 秒刷新）」 |
| **V4** | 购/兑后同会话 update 即时 | **PASS** | 首跑：购前剩 4 → stay ≤500ms 剩 3（`50-buy-update.json`）；复测：3→2（`51-retest-update.json`）；聊天 `[生活] 旧靴 → 余烬魂尘…` |
| **V5** | 关页重开仍真数 | **PASS** | `60-menu-after.json`：已用 1/4 · 剩 3 · 经验 61 |
| **E0** | 零改 life.yml / 菜单 / 开关 / bv | **PASS** | life md5 `197d3613…` · menu `4e5abcb4…` · ember-v1 `48b96322…` · 三 true · bv62 |

## tip / 产物

| 项 | 值 |
|----|-----|
| 键 tip（D388） | **`fc26f9a0`** |
| 菜单 tip（D389） | **`8bb5e822`** |
| jar | `1.65.103-d388.local`（sha256 `d7b46183…`） |
| `life.yml` md5 | `197d3613ceeb16f2ec96e94eb7a8d507`（未变） |
| `ember_life.yml` md5 | `4e5abcb461b06ff4b51cb4a27c8b3cc9`（本号只读验） |
| 分支 | main |

## 手法与注记

- mineflayer `D388Life` + `lib/proxy-login`；脚本 `/workspace/tmp/d388-d389-life-level/d388-d389-spot.js` + `d388-retest-papi-update.js`
- 开菜单：console `trmenu open ember_life D388Life`；兑尘：菜单 G 格点击（=`corerpg life buy soul_dust`）
- 造条件：`corerpg coin give` · `ni give junk_ember_boot` · bot op `/corerpg life xp` 至 Lv.3；**未**改 life 价/daily/weekly/`level_xp`
- **未**切分支 / stash / reset / `checkout -- .`；login/proxy/MariaDB **未碰**
- **≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）

## 结束态

| 项 | 值 |
|----|-----|
| set_bonus / enabled / migrate | true / true / true |
| balance_version | 62 |
| play PID | 929948 |
| jar | 1.65.103-d388.local |
| tip 菜单 / 键 | 8bb5e822 / fc26f9a0 |

## 总控签字栏（待签）

| 项 | 签认 |
|----|------|
| **D388+D389 抽测 PASS** | [ ] |
| tip 菜单 `8bb5e822` · 键 `fc26f9a0` | [ ] |
| I 三行 + G/K/H/J 短签 + I 左键不关 + update 即时 | [ ] |
| 零改 life.yml·开关·bv·jar·配置 | [ ] |
| ≠关观察（满窗仍须 ≥2026-10-10 17:40 CST） | [ ] |
| 签字 / 日期 | |

---

*D388+D389 抽测 · PASS · tip `8bb5e822`+`fc26f9a0` · jar `1.65.103-d388.local` · 证据 `/workspace/tmp/d388-d389-life-level/` · ≠关观察。*
