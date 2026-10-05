# STATUS · ARCH S2-7 / E1 SoT（CoreRpg 1.65.52 / D224）

## 本窗

| 项 | 内容 |
|---|---|
| `amount()` | 读 `ember-v1-economy.yml`（SoT）；与 Java `golden` 双断言；不一致 → throw |
| 启动 | `EmberMode` 加载 plugins 副本；漂移/损坏 → **SEVERE fail-closed**（不静默回退 classpath）；缺文件则 saveResource / classpath |
| `grantCoin` | yml 未就绪时拒绝发放 |
| 单测 | 加载后 `amount()` == golden；缺/漂移 fail-closed；`economyYmlDrift` 仍钉镜像 |
| 数量 | **不变**（bv57；yml 已与 golden 对齐） |

## 已路由（累计）

来源：S01–S06 / S20–S25；消耗：C03–C14、C18；Delivery 负额扣币；**amount 真源 = yml**。

## 未做 / 下一刀

- C15 外观（暂停）；**E3 p1sim 同读 yml 已完成（D225）**；徽记高图周来源 R1-sim（设计笔记，未改 live）
