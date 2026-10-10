# DESIGN · p1sim modelGaps stub 政策（2026-10-11 · D1026）

> **状态：文档。禁止假 FULL。**

`EmberEconomy.modelGaps()` 列出 NONE/PARTIAL 行（含 S60 admin、C17 life、C12/C13 insignia 等历史缺口）。

## 政策

1. **禁止**为了单测绿把未建模行改成 `Model.FULL` 空 stub。  
2. 真进 p1sim：独立 tip → 账户语义 → `p2econ`/`rules.py` 改。  
3. Stage1 amount() 读表 **不**等于 p1sim FULL。  
4. C19 OUT / C15–C16 暂停 — 不进 power model。

## 现状指针

见 `EmberEconomyTest` modelGaps pin；REG §5；Stage0 §3。
