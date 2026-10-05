# RELEASE · CoreRpg 1.65.45（D214，2026-10-06）

| 项 | 值 |
|---|---|
| 决策 | D214 · 技能组 S2 火痕步（D210 F14c0n1） |
| 内容 | Q05 + 焚烬 2pc → 潜行+Q 替换为火痕步（落点点燃 1、套装同系数×1.0）；技能页 / PAPI / kit info；后撤步 defer |
| 不变 | bv57；踏步 5 格 / 14s；挂机不放身法；无永久乘区 |
| 代码 | `EmberSkillKit`（UNLOCK_STEP/huohen*）；`FlexSkillService.applyHuohenIfActive`；`SkillService.cmdKitInfo`；`CoreRpgExpansion`；TrMenu `ember_skill_kit` |
| 测试 | unit **373 / 0** |
| 配置 | 菜单文案；无数值 yml 改 |
| jar sha256 | `bf18643b2c8c5418761fb4bddb12c12378ef3fb5df9769a16fbe33a35bf5bed1`（JDK 8u504，class 52） |
| 备份 | `/workspace/backup/CoreRpg-1.65.44-pre-1.65.45.jar` |
| 服务器 | 02:33 / 02:34 重启；Enabling CoreRpg v1.65.45；CoreRpg / CoreGacha MySQL connected；SEVERE 0；play PID 2050254 |
| 冒烟 | FreshQ774–776 PASS（`docs/tests/smoke-2026-10-06-d214-huohen.md`）；首会话 papi「Failed to find player」已知；二会话 papi 4/4；资产路径未改 → 不跑 persist-roundtrip |
| 下一步 | 后撤步（套装亲和 + 菜单选型定稿后再做）；守招仍搁置（D212） |
