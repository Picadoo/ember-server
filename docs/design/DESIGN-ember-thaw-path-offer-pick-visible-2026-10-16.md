# DESIGN · ThawPath.offerPick 解冻路径选打菜单化 · D13386 · TIP-FC

Clone of TIP-FB RallyPath offerPick thin page for ThawPath (D561).

**Goal:** Weekly / bare thawpath pick opens `ember_p1_thaw_path` TrMenu page (auto/ask/mute/clear + skill kit / adventure / forge). Stop teaching bare `/corerpg p1 thawpath` as main UI.

**Hard bans:** zero damage / CD / drop / stamina price lifts; zero D561 frost semantics change; one tip only (not Duck, not full *Path sweep); Affix Pack still FROZEN.

**W1a:** EmberThawPath.offerPick → openPathPick; applyAndReply next → thin page; glance no bare command; ASK 「改路径」→ `/ember_p1_thaw_path`; EmberCommand usage strings point at page; PAPI `thaw_path*`.

**W1b:** TrMenu `ember_p1_thaw_path.yml` cloned from rally with thaw copy.
