# 状态 · D291：推荐打法页（机动 / 站桩 / 压上）

**日期：** 2026-10-07（上海时间）  
**上游：** DESIGN playfeel §3.3-2 · CoreRpg **1.65.82**（本窗零 jar）

## 人话

天赋一排三个名字玩家记不住、也感觉不到差别。本窗只立**认知标签**：机动=回身斩、站桩=稳桩、压上=踏步回气；一页看清得失/适合/是否已点，并跳到天赋 / 技能组 / 签名。

## 改了什么

| 处 | 改动 |
|---|---|
| `ember_p1_playstyle.yml` | 新菜单；三标签卡 + PAPI；深链技能/签名/天赋/hub |
| `ember_hub.yml` | B「推荐打法」；成长格旁提示 |
| `ember_skill_kit` / `ember_p1_sig` / `ember_p1_spec` | P 深链 |
| `ember_help.yml` 变强 | 可点进推荐打法 |
| 设计稿 §3.3-2 | 标已施工 D291 |

## 不动

- growth.yml 数值 / mods / 侧移提案（趁隙窗等）
- 六槽 · Pack · VIP/pass · jar

## 验收

- `rg '推荐打法|机动|站桩|压上' plugins/TrMenu/menus/ember_p1_playstyle.yml`
- `trmenu reload`；play 保持 up
