# STATUS · 断塔环廊防坠（wave2b 刷点内收）

**日期：** 2026-09-28 10:58 CST（Asia/Shanghai）  
**岗：** 余烬-插件  
**依据：** 总控【派工 · 断塔环廊防坠方案 A】· tip `f65816f` · 设计 `878c58b` · `docs/design/design-ember-daily-spire-ringfall.md` §4/§5  
**Verdict：** ✅ · `dp reload` ✅ 10:58:49 CST · **未 push**

---

## 一句话

`EmberDailySpire` wave2b 两条卫尸刷点内收：南缘离崖、近阶微调到可站木桩台；door2 / `$kill`×2 / 链式 / boss_prep **零动**。

---

## 坐标前后

| 点 | 现网（改前） | 设计建议 | **落地** | 可站核验（MCA `ember_daily_spire`） |
|----|-------------|----------|----------|-------------------------------------|
| 南缘卫尸 | `(0,70,-6)` | `(0,70,-4)` | **`(0,70,-4)`** | ✅ foot=stonebrick(98)，body/head=AIR |
| 近阶卫尸 | `(0,70,2)` | `(0,70,1)` | **`(-1,69,1)`** | 设计格 `(0,70,1)` 与旧点同为中轴空洞（y64–72 AIR）；±1 于 y70 仍空洞。微调到旁侧木桩台：foot=log(17)@y68，body/head=AIR@y69/70。**未外扩回南崖** |

文件：`plugins/DungeonPlus/dungeon/EmberDailySpire/monster.yml`（与 `server-runtime/...` **同 inode**）。

不变：`EmberDailySpireZombie`；amount=1×2；scattered=0.5；`$kill{断塔卫尸;amount=2}`；wave2a.start→wave2b delay=2；door2 九格；boss_prep (±3,76,2)；boss。

---

## 禁项确认

| 禁项 | 勾 |
|------|----|
| door2 `$operation-block` 坐标 / 门宽 | [x] 零 diff |
| `$kill`×2 / 品种 / kill-any | [x] 仍 `断塔卫尸;amount=2`；无 kill-any |
| wave2a / 链式 delay / wave2a.end 无 door | [x] |
| boss_prep / boss | [x] |
| 他线 / MM / HP / 体力 / 掉落 | [x] |
| 外扩回崖 / 新挖 MCA | [x] 仅 YAML location |

---

## 热更

FIFO `dp reload` · **10:58:49** CST · 插件重载完毕 · `EmberDailySpire` 初始化完毕。

---

## 验收对照（设计 §5 · 测岗另派）

1. 乱序清对射时卫尸不因坠崖导致 `$kill`×2 不满；door2 能开  
2. 房2 链式零回归（wave2a.start→delay2 wave2b；wave2a.end 无 door）  
3. door2 仍仅挂 wave2b.end；无 kill-any  
4. boss_prep 零回归  
5. 仅本文件 wave2b location diff  

## Git

本地 commit；**未 push**（交总控代推后派测）· 勿纳入 runtime/worlds 脏文件。
