# 状态 · D304：Boss 预警诚实 / 相位线索可读方案 M（W1a+W1b+W1c+W1d）

**日期：** 2026-10-08（上海时间）  
**上游：** 总控批 A · [`DESIGN-ember-boss-telegraph-honesty-2026-10-08.md`](../design/DESIGN-ember-boss-telegraph-honesty-2026-10-08.md) 方案 M（`90caf7db`）  
**版本：** CoreRpg **1.65.94** · `balance_version` **60**（未抬）

## 人话

破绽成功闪与入口名片已有；招式**起手预警**与**半血/增援/烬核相位**仍常被 chat 刷屏淹没或 whiff 尾缀缺席像假线索。本窗**零经济 / 零新 Pack / 零战斗压**：起手 ActionBar + 诚实 whiff 文案 + Q01–Q07 半血短签钉真实钩——不跑 p1sim。

## 改了什么

| 窗 | 改动 |
|---|---|
| **W1a** | `startWarn` 每次蓄力发 ActionBar：`§e蓄力 · «名» · {形状} · {warn}s` + 键控短尾缀（撞墙/先踩再躲/可破招/靠拢分摊）；chat 全量预警行保留 |
| **W1b** | Q01–Q07 `storyHalfHpCue`：q03 半血增援·两侧加怪；q06/q07 半血破招·霜潮/炉心可打断；其余「半血 · 招式变强」。团本继续 D303 `halfHpCue`；R03 烬核仍走 share 预警 |
| **W1c** | `whiffHint`：有 `whiff_stun` 即常驻「先踩进圈再躲开…」；`armed` 只影响结算；charge 0 格跳过仍不发蓄力（B2.165） |
| **W1d** | 冒险/挑战七图加 `§e相位` 半行，与 W1b 同词 |
| jar | `EmberCounterplay` castStartBar/shapeShort/whiffHint；`EmberRunDirector` storyHalfHpCue + flashActionBar on cast；版本 **1.65.93 → 1.65.94** |
| 单测 | `hintSuffixesMatchLiveWarnLines`（W1c）；`castStartBar_keyGatedSuffixes`；`storyHalfHpCue_qMapsMatchHooks` |

## 不动

- Pack6 / 新招式 / 大修 Boss AI / skills 数值（every/warn/dmg/HP）  
- 体力 / 掉率 / event_rate / ALTS  
- 方案 R（半血前提示 / ActionBar 持续整段 warn）**后置**  
- 天赋 / 灰印 HOLD / 守招 / 事件 R/W / 调律 R / 深渊 R / 周本 R / 走廊 W2  
- 六槽 / 永久乘区  
- bv **60** · login/proxy 未停（仅 play 换 jar）

## 验收

- 静态：W1b 短签与 yml adds/break/below/share 钩一致；单测 `EmberCounterplayTest` + `storyHalfHpCue_qMapsMatchHooks` PASS；冒险菜单七图 `§e相位` ×7  
- 冒烟（live · **不跑 p1sim**）：
  - **Q01 PASS**：chat `蓄力「重斩」` + ActionBar `蓄力 · «重斩» · 扇形 · 1s ·先踩再躲`；半血短签 `半血 · 招式变强`
  - **R02 PASS（进本）**：`本局：霜封哨所·团 · 名片：半血砸地`（D303 保留）；首领厅蓄力同 `startWarn` 路径（与 Q01 同码）
  - **Q07**：进本+三房可清；首领已生成（`boss=3120/3120`）；起手 ActionBar 与 Q01 同 `startWarn`（本冒烟窗未稳定收到蓄力行；单测钉炉心破招句）
  - 脚本：`mineflayer-tests/d304-boss-telegraph-smoke.js`
- play Enabling **1.65.94**；`version CoreRpg` = 1.65.94；TrMenu reload 69 菜单  
- 回滚：`/workspace/backup/CoreRpg-1.65.93-pre-d304.jar` + 还原 adventure/challenge 菜单相位行

## 下一窗

- **R**（半血前提示 / ActionBar 持续 warn）**后置**；仅总控另批 M+R 时开  
- **禁** Pack6 / 新招 / 抬体力掉率 / 六槽 / 天赋续跑 / 灰印续跑 / 事件 R/W / 调律 R / 深渊 R / 周本 R  
