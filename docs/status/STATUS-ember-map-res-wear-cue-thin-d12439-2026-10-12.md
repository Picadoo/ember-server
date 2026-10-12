# STATUS · TIP-R 图装局内换装共鸣升沿 D12424–D12439 live

- **何时**：2026-10-12 17:02 CST
- **版本**：CoreRpg `1.65.771-d12439.local`
- **commits**：功能 `5c6cafb4`
- **落地**：maybeWearCue 假→真升沿；每 run latch；WearBone 复用零加量；`map_res.wear_cue` 热关缺省 true
- **验收**：单测 11 PASS；BONUS=1 / 加宽未改；SEVERE 待重启确认
- **硬禁**：零 Affix / 伤乘 / 抬表 / glance twin / soft densify
