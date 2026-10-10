# 状态 · D514：日委路径 · PASS

**日期：** 2026-10-10  
**裁决：** **已批 A · 批 M · D514**  
**版本：** jar **1.65.205-d514.local**

## 交付

| 项 | 内容 |
|----|------|
| Java | `EmberDailyPath` · Settle `tellAfterSettle` · Session glance · Q01 offer @270L · `/corerpg p1 dailypath` |
| 禁 | 未改 bounty.daily 表 · 无 ActionBar · 非短本/花样委托双胞 · 非组合粘性/工坊/入场 cue |

## 冒烟 FreshQ1050

| 路径 | 结果 |
|------|------|
| full | `日委路径 0/3 · 再通关 3 局` **PASS** |
| light | `日委路径 0/1 · 再通关 1 局` **PASS** |
| flex | 无 chase 行 **PASS** |
| enter q01 + full | 进本前 glance `0/3` **PASS** |

单元：`EmberDailyPathTest` target/chase PASS。

*D514 · 新日委追猎获取。*
