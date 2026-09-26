# Moves DP monster.yml spawn points that sit inside solid blocks to the nearest reachable open cell.
# Run from plugins/DungeonPlus with a venv that has anvil-parser: python scripts/dp_fix_spawns.py
import re, sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__))); from dp_map_reach import *
for d in ['EmberAbyss','EmberCalamity','EmberGuildBoss','EmberRaid','EmberWeekly']:
    opt=open(f'dungeon/{d}/option.yml').read()
    m=re.search(r'setmap\{name=(\w+)',opt).group(1)
    R=reach(m,-40,65,270)
    p=f'dungeon/{d}/monster.yml'; s=open(p).read()
    changes=[]
    def fix(mm):
        name,x,y,z,rest=mm.group(1),int(mm.group(2)),int(mm.group(3)),int(mm.group(4)),mm.group(5)
        if (x,y,z) in R: return mm.group(0)
        best=min(R,key=lambda c:((c[0]-x)**2+(c[2]-z)**2+(c[1]-y)**2, -((c[0]+40)**2+(c[2]-270)**2)))
        sc=re.search(r'scattered=([\d.]+)',rest)
        if sc and float(sc.group(1))>1.0: rest=rest.replace(sc.group(0),'scattered=1.0')
        changes.append(f'{name} {(x,y,z)}->{best}')
        return f'name={name};location={best[0]},{best[1]},{best[2]}{rest}'
    s2=re.sub(r'name=(\w+);location=(-?\d+),(-?\d+),(-?\d+)((?:;[^}]*)?)',fix,s)
    if s2!=s:
        s2="# 2026-09-27: spawn points moved into the map's open room (old ones were inside solid blocks → mobs/bosses suffocated;\n#   see docs/critic-fixes-20260927.md §10). Map "+m+" room: x "+str(min(c[0] for c in R))+".."+str(max(c[0] for c in R))+", z "+str(min(c[2] for c in R))+".."+str(max(c[2] for c in R))+"\n"+s2
        open(p,'w').write(s2)
    print(d,m,changes)
