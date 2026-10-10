# 状态 · D455：签名触发可感短闪 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D455 · PASS**  
**版本：** jar **1.65.147-d455.local** · MySQL×2 · SEVERE0  
**冒烟：** FreshQ986 · PASS=5 FAIL=0 · `sig test stamp charm L02` → **门楼余烬 生效** · mods `dodge_heal` · Enabling 1.65.147  
**单测：** `EmberSigFeelTest` PASS=3  
**硬禁：** 未改 ALTS / 掉率 / 烙印价 / AFK 日表 / K3 / sx20 / 局内热换

## 交付

| 项 | 内容 |
|----|------|
| `EmberSigFeel` | owner / procLine / runLine / debounce 800ms |
| `EmberGrowthService` | `flashSigOwned` · `announceSigFeel` · onDodge/onTeleHit/shield 钩 |
| `SkillService` | skill_ignite 点燃短闪 |
| `EmberSetService` | burn_spread 传火短闪 |
| `EmberRunService.potionCheck` | 进本一眼本局签名 |
| TrMenu | `ember_p1_sig` Open 半行 |

*D455 PASS · 签名成长手感 · 行为可感优先于数值。*
