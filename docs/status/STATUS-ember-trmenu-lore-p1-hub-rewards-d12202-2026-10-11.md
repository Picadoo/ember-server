# 状态 · D12202：TrMenu lore 压缩 p1/hub/rewards

**日期：** 2026-10-11 16:48–16:55 CST  
**裁决：** TrMenu lore + docs · **PASS**（未改 CoreRpg jar）

## 交付
1. **25 菜单 lore ≤3**（砍空白与说明墙；保留 PAPI / ➥ / 动作）：`ember_hub` · `ember_daily_rewards` · `ember_abyss_rewards` · `ember_p1_abyss/afk/armor/brand/challenge/codex_gear/fest/forge/forge_recipes/gear/gem/modes/playstyle/pledge/reroll/season/shop/shop_buy/short/sig/sig_runconfirm/spec`。
2. **跳过** pet/gacha/hub_legacy（cosmetics 降优先级）。
3. 校验：目标菜单 long-lore(>3)=0 · YAML OK。
4. `trmenu reload` → **77** 菜单 · 147 ms · 无 SEVERE（16:53:29 CST）。live CoreRpg **1.65.723-d12194.local** · jar 未改。

## 禁
改 jar · tip-twin · Six-slot · pet/gacha · p1sim
