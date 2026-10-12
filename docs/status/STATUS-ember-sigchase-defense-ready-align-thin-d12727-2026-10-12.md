# STATUS · TIP-AN SigChase 追烙防具侧就绪 D12712–D12727

- **何时**：2026-10-12 20:41 CST
- **裁决**：**CLOSED live** · 批 **A · 方案 M**
- **live**：`1.65.779-d12727.local`
- **hashes**：feat `1ff4769e2b1aecbc7a77e1d7c1669492c620eeaf`
- **证据**：`Enabling CoreRpg v1.65.779-d12727.local` · MySQL connected · Done (4.902s) · Picado OP · 无 CoreRpg SEVERE
- **单测**：`EmberSigHeldReadyTipTest` **8/0 PASS**（charm+头 / charm 优先 / L01 刃回归 / 空 null / 槽名助手 / stampable 甲仍空 等）
- **范围**：sameSlotPiece held/worn 走 fitsDefense + imprintWornTarget（charm→armor0..3）；maybeHeldReadyTip worn 跟目标 uid；maybeReadyCue 槽名跟实际目标件；GrowthService imprintWornTarget 委托同一 static；零功率表；非甲闸再扫
- **HOLD**：L13 施工；L11/L13 mods；扩 stampable；甲闸再扫；densify / ForgeChoice densify / glance twin
- **安装**：`plugins/CoreRpg.jar`（server-runtime/plugins → 同 inode）
