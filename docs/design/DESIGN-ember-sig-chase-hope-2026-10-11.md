# DESIGN · 徽记/签名追烙希望表盘（D604 · Stage A4）· 2026-10-11

> **状态：** 已批 A · 方案 M · D604  
> **上游：** 希望环 DESIGN Stage A4 · tip `STATUS-ember-next-hard-debt-sig-chase-hope-need-design-2026-10-11.md`  
> **硬禁：** **通关/首通逻辑零改动** · 不改 STAMP_RATE / IMPRINT_MARKS / ALT_MARKS / 烙印价 · 无 K3 · 无 AFK 表抬 · 无 sx20 · 不碰 gem sock · 无粘性路径

---

## 0. 一句话

把「刷徽记 → 5 枚确定烙印」做成和 D601–D603 同风格的希望表盘；**签名从不挡通关**。

## 1. 诊断

| 症状 | 根因 | 不是根因 |
|------|------|----------|
| 签名 chase 像纯脸 | 追烙进度只在签名页/聊天；hub 希望表盘没挂 | 缺烙印兑换（已有 5 枚+币） |
| 怕卡关 | 叙事易误读成「要出签名才能过」 | 首通实际不读签名 |

## 2. 方案 M（已定）

| 做 | 不做 |
|----|------|
| `EmberHopeMeter.sigChase` + 统一 `EmberSigChase.progressLine` 希望风格（n/5 · 满则可兑烙印） | 改掉率 / 加签名件保底抽 |
| PAPI `sig_chase_hope`（= 希望文案）；hub / 签名页 / 装备入口一眼可见 | 改 `progressFlag` / 首通 / 入场闸 |
| 文案明示「不锁通关」 | 粘性瞬移冒充变强 |

**确定性旁路：** 重打每局 +1 徽记（已有）→ 满 `IMPRINT_MARKS=5` → 烬炉烙印兑换（已有）。本档只让**看得见**。

## 3. 验收

- 单测：progress / ready 文案；含「不锁通关」  
- git：首通/入场相关文件无行为 diff（或仅注释）  
- SEVERE0；未改 STAMP_RATE / IMPRINT_*  

## 4. 批注

- [x] 总控批 **A · 方案 M** · 2026-10-11
