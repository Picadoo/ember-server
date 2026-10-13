# tip · TIP-CP 短征轻量 variety 房内可感（真 roll · ≠Cast/Aid打磨 · ≠todayRot再拧 · ≠半行 · ≠庭内镜像 · ≠抬表）· 拟 D13321

> **状态：** **CLOSED live** · feat `64f08f47b` · jar `1.65.813-d13321.local` · PASS `1696ccd17` · 批 sha `e976fe6d8` · live `1696ccd17` · 证据 `/workspace/tmp/tip-cp-short-variety/` · 2026-10-14 02:36 Asia/Shanghai  
> **DESIGN** [`DESIGN-ember-short-light-variety-readable-2026-10-14.md`](../design/DESIGN-ember-short-light-variety-readable-2026-10-14.md) · RESEARCH 同日 · backlog `B-short-light-variety-readable`  
> **施工岗（批后）：** 余烬-插件 · **不派怪物/地图/物品**（默认）  
> **硬禁：** 零半行独批 · 零 Cast 粒子主交付 · 零拧 EliteAid/BossAid · 零 todayRot cue 再拧主交 · 零 sx20 · 零抬日表/伤表/S 表 · 零抬 event_rate/event_core/affix_shard · 零新 variety kind · 零庭内再叠顺手  
> **ancestor：** TIP-CO CLOSED live `d4ce8d3ed` · feat `1a3e1a3de` · PASS `bd7a68ce8` · jar `1.65.812-d13320` · 批 sha `31f06f562`

## 一句话
短征首通后再进本时，某房可出现可感词缀精英或房间事件；结算不发核心/碎片；日帽与 Cast/Aid/todayRot cue 不动。

## 为何不是半行
- **主交付：** 插件 **显式 short variety eligible** + 复用池 roll + Director 真跑词缀/事件 + 房内 HUD → 局内真差异  
- 选页/todayRot lore 半行 **不可独批**（REJECT 保持）

## ≠ 清单
- ≠ Cast 粒子二次打磨 · ≠ EliteAid/BossAid 再拧 · ≠ todayRot 半行/cue 再拧 · ≠ 庭内顺手镜像 · ≠ 导航三冲复读 · ≠ 半行空转 · ≠ 抬 event_core

## 施工钉死（批后）
- W1a：短征 + 全员已首通本本 → 显式纳入 variety 滚点（复用既有池/`event_rate`）
- W1b：命中房 HUD/喊话
- W1c：Director 词缀 promote / 房间事件真跑
- W1d：短征 settle **零发** event_core/affix_shard
- W1e（可选辅）：todayRot 事件偏置——不可独批
- **不改**日帽/S40–S58/Cast/Aid/HP/掉落/地图/庭内菜单/`event_rate`/`event_core`/`affix_shard` 配置值

## 方案
- **M（荐）** = W1a+W1b+W1c+W1d 全弧；W1e 可选辅  
- **L** = 仅 todayRot 强制一轻事件；其余后置 · **默认不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 02:21 Asia/Shanghai
- [x] 插件交 short varietyEligible + HUD + Director + settle零发 · jar `1.65.813-d13321.local` · feat `64f08f47b` · 2026-10-14 02:30 Asia/Shanghai
- [x] 抽测：首通无花样、重打可感、settle零核心碎片；零抬表；零改 Cast/Aid；零 sx20 · W1e 仍不做 · PASS `1696ccd17` · 证据 `/workspace/tmp/tip-cp-short-variety/`

## STATUS: **CLOSED live** · `64f08f47b` · PASS `1696ccd17` · D13321 · TIP-CP · 2026-10-14 02:36 Asia/Shanghai
