# tip · TIP-CO 短征「今日推」本局内可感（真 session · ≠Cast/Aid复读 · ≠半行 · ≠庭内镜像 · ≠抬表）· 拟 D13320

> **状态：** APPROVED · 批 **A · 方案 M** · 已派 **余烬-插件**（CoreRpg）· 2026-10-14 02:01 Asia/Shanghai · **勿勾 CLOSED**（等 live CLOSED）· W1d 选页半行本窗不做（可选辅默认关）  
> **DESIGN** [`DESIGN-ember-short-today-inrun-feel-readable-2026-10-14.md`](../design/DESIGN-ember-short-today-inrun-feel-readable-2026-10-14.md) · RESEARCH 同日 · backlog `B-short-today-inrun-feel-readable`  
> **施工岗（批后）：** 余烬-插件 · 物品可选辅 · **不派怪物/地图**（默认）  
> **硬禁：** 零半行独批 · 零 Cast 粒子主交付 · 零拧 EliteAid/BossAid · 零 sx20 · 零抬日表/伤表/S 表 · 零庭内再叠顺手  
> **ancestor：** TIP-CN CLOSED live `931274777` · tip `021d8e351` · PASS `17f05e0f1`

## 一句话
进「今日推」短征本时开局/开房/结算有专属可感反馈；非今日本无此标记；日帽与 Cast/Aid 不动。

## 为何不是半行
- **主交付：** 插件 **session todayRot** + 进本 title/AB + 分房提示 + 结算完成句/再进按钮 → 局内真差异  
- 选页 lore 半行 **不可独批**（REJECT 保持）

## ≠ 清单
- ≠ Cast 粒子二次打磨 · ≠ EliteAid/BossAid 再拧 · ≠ 庭内顺手镜像 · ≠ 导航三冲复读 · ≠ 半行空转

## 施工钉死（批后）
- W1a：进本 `key==todayKey` → todayRot + title/AB/tell「今日推·本」
- W1b：todayRot 开房 AB「今日推 · 第N房」（节流）
- W1c：todayRot 有奖结算「今日推完成」+ `[再进今日推]`→`enter short-today`
- W1d（可选辅）：选页 D lore「进本有今日标记」——不可独批
- **不改**日帽/S40–S58/Cast/Aid/HP/掉落/地图/庭内菜单

## 方案
- **M（荐）** = W1a+W1b+W1c 全弧；W1d 可选辅  
- **L** = 仅 W1a；分房/结算后置 · **默认不批 L**

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 02:01 Asia/Shanghai
- [x] 插件交 todayRot 进本/分房/结算 · jar `1.65.812-d13320.local` · 2026-10-14 02:05 Asia/Shanghai
- [ ] 抽测：今日本有标记、非今日无；零抬表；零改 Cast/Aid；零 sx20

## STATUS: APPROVED · 批 A·M · 已派插件 · 拟 D13320 · TIP-CO · **勿勾 CLOSED** · 2026-10-14 02:01 Asia/Shanghai
