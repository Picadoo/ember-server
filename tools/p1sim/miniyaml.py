"""Tiny YAML subset reader (stdlib only) for the repo's own config files.

Supports: block mappings, block sequences ("- x"), flow lists/maps ([..], {..}) that may span several lines,
plain / quoted scalars, '#' comments. Enough for ember-v1*.yml and MythicMobs mob files; not a general YAML parser.
"""
import re

_num = re.compile(r'^[-+]?(\d+\.?\d*|\.\d+)([eE][-+]?\d+)?$')


def _scalar(s):
    s = s.strip()
    if s == '' or s in ('~', 'null'):
        return None
    if s[0] in '"\'' and s[-1] == s[0] and len(s) >= 2:
        return s[1:-1]
    if s in ('true', 'True'):
        return True
    if s in ('false', 'False'):
        return False
    if _num.match(s):
        return float(s) if any(c in s for c in '.eE') else int(s)
    return s


def _strip_comment(line):
    out, q = [], None
    for i, c in enumerate(line):
        if q:
            if c == q:
                q = None
        elif c in '"\'':
            q = c
        elif c == '#' and (i == 0 or line[i - 1] in ' \t'):
            break
        out.append(c)
    return ''.join(out).rstrip()


def _depth(s):
    d, q = 0, None
    for c in s:
        if q:
            if c == q:
                q = None
        elif c in '"\'':
            q = c
        elif c in '[{':
            d += 1
        elif c in ']}':
            d -= 1
    return d


def _flow(s):
    """Parse a flow collection / scalar."""
    pos = [0]

    def ws():
        while pos[0] < len(s) and s[pos[0]] in ' \t\n':
            pos[0] += 1

    def val():
        ws()
        c = s[pos[0]]
        if c == '[':
            pos[0] += 1
            out = []
            ws()
            if s[pos[0]] == ']':
                pos[0] += 1
                return out
            while True:
                out.append(val())
                ws()
                c = s[pos[0]]
                pos[0] += 1
                if c == ']':
                    return out
        if c == '{':
            pos[0] += 1
            out = {}
            ws()
            if s[pos[0]] == '}':
                pos[0] += 1
                return out
            while True:
                ws()
                k = tok(':')
                pos[0] += 1  # ':'
                out[k] = val()
                ws()
                c = s[pos[0]]
                pos[0] += 1
                if c == '}':
                    return out
        return _scalar(tok(',]}'))

    def tok(stops):
        ws()
        st = pos[0]
        if s[st] in '"\'':
            q = s[st]
            e = s.index(q, st + 1)
            pos[0] = e + 1
            return s[st + 1:e]
        while pos[0] < len(s) and s[pos[0]] not in stops:
            if s[pos[0]] == ':' and ':' in stops and pos[0] + 1 < len(s) and s[pos[0] + 1] not in ' \t':
                pos[0] += 1
                continue
            pos[0] += 1
        return s[st:pos[0]].strip()

    return val()


def _value(text):
    text = text.strip()
    if text.startswith('[') or text.startswith('{'):
        return _flow(text)
    return _scalar(text)


def loads(text):
    lines = []
    buf, ind = None, 0
    for raw in text.splitlines():
        line = _strip_comment(raw)
        if not line.strip():
            continue
        if buf is not None:
            buf += ' ' + line.strip()
            if _depth(buf) <= 0:
                lines.append((ind, buf))
                buf = None
            continue
        ind = len(line) - len(line.lstrip(' '))
        content = line.strip()
        if _depth(content) > 0:
            buf = content
            continue
        lines.append((ind, content))
    pos = [0]

    def block(indent):
        if pos[0] >= len(lines):
            return None
        ind0, c = lines[pos[0]]
        if c.startswith('- ') or c == '-':
            out = []
            while pos[0] < len(lines):
                ind1, c = lines[pos[0]]
                if ind1 != ind0 or not (c.startswith('- ') or c == '-'):
                    break
                pos[0] += 1
                item = c[1:].strip()
                if item == '':
                    out.append(block(ind0 + 1))
                elif _is_key(item):
                    # inline mapping start inside a sequence item
                    k, v = _split_key(item)
                    d = {k: _value(v) if v else block(ind0 + 2)}
                    if pos[0] < len(lines) and lines[pos[0]][0] > ind0:
                        rest = block(lines[pos[0]][0])
                        if isinstance(rest, dict):
                            d.update(rest)
                    out.append(d)
                else:
                    out.append(_value(item))
            return out
        out = {}
        while pos[0] < len(lines):
            ind1, c = lines[pos[0]]
            if ind1 < ind0:
                break
            if ind1 > ind0:
                pos[0] += 1
                continue
            if c.startswith('- '):
                break
            pos[0] += 1
            k, v = _split_key(c)
            if v:
                out[k] = _value(v)
            else:
                if pos[0] < len(lines) and (lines[pos[0]][0] > ind0 or
                                            (lines[pos[0]][0] == ind0 and lines[pos[0]][1].startswith('- '))):
                    out[k] = block(lines[pos[0]][0])
                else:
                    out[k] = None
        return out

    return block(0) if lines else {}


def _is_key(s):
    if s[0] in '[{"\'':
        return False
    m = re.match(r'^[^:]+:(\s|$)', s)
    return m is not None


def _split_key(s):
    i = re.search(r':(\s|$)', s).start()
    k = s[:i].strip()
    if k and k[0] in '"\'' and k[-1] == k[0]:
        k = k[1:-1]
    return k, s[i + 1:].strip()


def load(path):
    with open(path, encoding='utf-8') as f:
        return loads(f.read())
