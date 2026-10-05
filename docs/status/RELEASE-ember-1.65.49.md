# RELEASE · CoreRpg 1.65.49（D219，2026-10-06）

| 项 | 值 |
|---|---|
| 决策 | D219 · 技能组身法·后撤步（D217 调研定稿） |
| 内容 | 技能页「身法方向」前冲/后撤（`p1_step_dir@all`）；后撤 4 格、无伤害、共用 14s；严格落点；火痕·后撤起跳点燃；共享 dash 斜角缝 body 采样（±0.3）；PAPI `kit_step_dir`；TrMenu G/H |
| 不变 | bv57；踏步前冲 5 格；火痕步焚烬绑定；挂机不放身法；无资产路径 |
| 测试 | unit **394 / 0**（+8：EmberSkillKit 4 + EmberDash 4） |
| jar sha256 | `33442854c73250ca4cf147d2efce6ea975cd9d3835dfb2d81f6bd78cdcce92fd`（JDK 8u504，class 52） |
| 备份 | `/workspace/backup/CoreRpg-1.65.48-pre-1.65.49.jar` |
| 服务器 | 03:09 重启；Enabling CoreRpg v1.65.49；CoreRpg / CoreGacha MySQL connected；SEVERE 0；play PID 2087958 |
| 冒烟 | FreshQ780–782 PASS（docs/tests/smoke-2026-10-06-d219-backstep.md）：kit 方向开关、后撤步 cast、火痕·后撤起跳点燃、Q01 回归；二会话 papi 9/9 |
| 下一步 | ARCH C18 festival shop / S22 AFK grant / ember-v1-economy.yml；烬爆·炽愈 Q05 身法变体另开调研 |
