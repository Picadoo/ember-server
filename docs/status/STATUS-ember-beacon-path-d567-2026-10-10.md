# 状态 · D567：护灯路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D567**  
**jar：** `1.65.258-d567.local`  
**实现：** `EmberBeaconPath` + pathBeaconGuard + beacon place hook + `beaconpath`/`beacon`/`beaconseed`

| 验 | 结果 |
|----|------|
| AUTO | beaconseed → 护灯·自动 待命/传送 |
| ASK | `[护灯]` |
| MUTE | 静默 |
| unit | EmberBeaconPathTest |

*D567 · 新护灯事件获取环（≠ Hold 占点 / Guard 护兔 / Smash 砸晶）。*
