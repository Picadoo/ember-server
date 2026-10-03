"""D140 boss-move check: clear rate per map at the book reference loadout (normal) and T3+6 (challenge), dodge 0.3 / 0.5 /
0.7, averaged over 5 seeds × 4000 runs, old runs yml vs the current one. Usage (from the repo root):
  git show <old-rev>:plugins/CoreRpg/ember-v1-runs.yml > /tmp/runs-old.yml
  python3 tools/p1sim/bossmoves.py q01 [json list of overrides like '[{"0.every": 10.5}]'] [--old /tmp/runs-old.yml]
"""
import sys, json, os; sys.path.insert(0,'/workspace/ember-p1/tools/p1sim')
import p1config, p1sim, p2econ
OLD=sys.argv[sys.argv.index('--old')+1] if '--old' in sys.argv else '/tmp/runs-old.yml'
if '--old' in sys.argv: i=sys.argv.index('--old'); del sys.argv[i:i+2]
k=sys.argv[1]; N=4000; seeds=[7,11,13,17,19]
def rates(cfg,seed):
    ccfg=p2econ.challenge_cfg(cfg)
    bt,be,ct,ce,lv=p1sim.REF_GEAR[k]
    st=p1sim.stats(cfg,dict(p1sim.item('burst' if bt else 'none','blade',bt),enh=be),dict(p1sim.item('burst' if ct else 'none','charm',ct),enh=ce),lv)
    st3=p1sim.stats(cfg,dict(p1sim.item('burst','blade',3),enh=6),dict(p1sim.item('burst','charm',3),enh=6),30)
    r={}
    for d in (0.3,0.5,0.7):
        r['n %.1f'%d]=p1sim.clear_rate(cfg,k,st,p1sim.Knobs(d),N,seed=seed)
        r['c %.1f'%d]=p1sim.clear_rate(ccfg,k,st3,p1sim.Knobs(d),N,seed=seed)
    return r
def avg(rs): return {x:sum(r[x] for r in rs)/len(rs) for x in rs[0]}
p1config.FILES['runs']=p1config.FILES['runs_src']=OLD
old=p1config.load('current'); base=avg([rates(old,s) for s in seeds])
p1config.FILES['runs']='plugins/CoreRpg/ember-v1-runs.yml'; p1config.FILES['runs_src']='CoreRpg/src/main/resources/ember-v1-runs.yml'
cands=json.loads(sys.argv[2]) if len(sys.argv)>2 else [None]
for c in cands:
    cfg=p1config.load('current')
    if c:
        sk=cfg['maps'][k]['boss']['skills']
        for path,val in c.items():
            o=sk; ps=path.split('.')
            for p in ps[:-1]: o=o[int(p)] if p.isdigit() else o[p]
            o[ps[-1]]=val
    r=avg([rates(cfg,s) for s in seeds])
    dev=max(abs(r[x]-base[x]) for x in r)
    print('%.1f'%(100*dev),k,c,' '.join('%s:%.0f→%.0f(%+.1f)'%(x,100*base[x],100*r[x],100*(r[x]-base[x])) for x in sorted(r)),flush=True)
