# 余烬 · D356：帮助「变强」分解行 Stage2 护甲零头诚实薄抽/静态（H1–H3）· 线上验收

- **性质：观察期薄抽验 · 显示/菜单可见性 · 未改菜单 / 未动开关 / 未换 jar / 未关观察**
- 上游施工：[`STATUS-ember-six-slot-help-dismantle-honesty-d356-2026-10-09.md`](STATUS-ember-six-slot-help-dismantle-honesty-d356-2026-10-09.md) · 菜单 tip **`178458a9`** · `trmenu reload` 已做（施工号）
- DESIGN：[`DESIGN-ember-six-slot-help-dismantle-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-help-dismantle-honesty-2026-10-09.md) §2.4 / 总控验收
- 现态：bv**62** · `enabled=true` · `migrate=true` · `set_bonus=true` · play PID **580488** · jar **`1.65.101-d335.local`**
- 测号：`D356Spot`（经代理 joinPlay）；**未动真人档**；**未抢** D355 测号/证据（`/workspace/tmp/d355-*` mtime 未变）
- 证据：`/workspace/tmp/d356-spot/`（主结果 `99-results.json`；静态 `00-static.json`；活菜单 `20-help.json` / `21-strengthen.json`）
- 执行：2026-10-09 21:50 CST
- **结论：PASS（H1–H3 全绿；可选活扫 H1_live/H2_live 亦绿）**

## 人话

帮助「变强」分解行已见「刃/护符→胚料……；甲→白板零头（仅分解，见工坊）」。仍提胚料用途；无旧句「多余的掉落件换胚料」独占。强化/升阶/印记/互换/四条路/工坊指针/推荐打法均在。三开关与 bv62 抽前抽后未动。未碰 d355-spot。观察不关。

## 验收表

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| **H1** | help「变强」分解行含「甲→白板零头」（或等价）；仍提胚料 | **PASS** | src+live 一致；行=`刃/护符→胚料（精工·成色·升阶用）；甲→白板零头（仅分解，见工坊）`。`00-static.json` / `H1` |
| **H1_live** | 打开帮助→变强格可见同上 | **PASS** | 测号打开 `ember_help`；变强 lore 含甲→白板零头 + 胚料。`21-strengthen.json` |
| **H2** | 同格其它行未误删 | **PASS** | 强化/升阶/印记/互换/四条路/工坊/推荐 + actions→playstyle 仍在。`00-h2.json` / `H2_live` |
| **H3** | 本号未拧开关；未抢 d355-spot | **PASS** | 抽前/后 enabled/migrate/set_bonus true · bv62；测号 D356Spot；证据仅 `/workspace/tmp/d356-spot/`；d355 `99-results.json` mtime 未变。`00-precheck.json` / `90-endstate.json` |

## 手法与注记

- 静态为主：`rg` / md5 src=live（`6e48611a…`）
- 可选活扫：mineflayer `D356Spot` + `lib/proxy-login`；脚本 `/workspace/tmp/d356-spot/d356-spot.js`
- 开菜单：`/trmenu open ember_help` → dump「变强」lore
- **未**改 yml / jar / set_bonus / bv / 文案；**未**切分支 / stash / reset / `checkout -- .`
- **≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）；**不抢 K3**；**不抢 D355**

## 结束态

| 项 | 值 |
|----|-----|
| set_bonus / enabled / migrate | true / true / true |
| balance_version | 62 |
| play PID | 580488 |
| jar | 1.65.101-d335.local |
| 菜单 tip（D356 施工） | 178458a9 |
| 抽时 HEAD | 4d497f65 |
| 分支 | main |

## 总控签字栏（待签）

| 项 | 签认 |
|----|------|
| **D356 薄抽/静态 PASS** | [ ] |
| H1–H3 | [ ] PASS（测试） |
| 活菜单帮助「变强」可见甲→白板零头且仍提胚料 | [ ] |
| 同格其它行未误删 | [ ] |
| 零改 yml·开关·jar·bv；未抢 d355-spot | [ ] |
| ≠关观察（满窗仍须 ≥2026-10-10 17:40 CST） | [ ] |
| 不抢 K3 | [ ] |
| 签字 / 日期 | |

---

*D356 薄抽/静态 · H1–H3 PASS · 测号 D356Spot · 未改开关 · ≠关观察 · 证据 `/workspace/tmp/d356-spot/`。*
