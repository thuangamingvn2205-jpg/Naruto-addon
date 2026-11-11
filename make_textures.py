import zlib, struct, os
OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "src/main/resources/assets/narutomod/textures")

def write_png(path, w, h, px):
    raw = b""
    for y in range(h):
        raw += b"\x00"
        for x in range(w):
            raw += bytes(px.get((x, y), (0, 0, 0, 0)))
    def chunk(t, d):
        c = struct.pack(">I", len(d)) + t + d
        return c + struct.pack(">I", zlib.crc32(t + d) & 0xffffffff)
    png = (b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0))
           + chunk(b"IDAT", zlib.compress(raw)) + chunk(b"IEND", b""))
    os.makedirs(os.path.dirname(path), exist_ok=True)
    open(path, "wb").write(png)

def line(px, x0, y0, x1, y1, c):
    dx, dy = abs(x1 - x0), -abs(y1 - y0)
    sx, sy = (1 if x0 < x1 else -1), (1 if y0 < y1 else -1)
    err = dx + dy
    while True:
        px[(x0, y0)] = c
        if x0 == x1 and y0 == y1: break
        e2 = 2 * err
        if e2 >= dy: err += dy; x0 += sx
        if e2 <= dx: err += dx; y0 += sy

def rect(px, x0, y0, x1, y1, c):
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            px[(x, y)] = c

BLADE=(210,214,224,255); BLADE2=(140,146,160,255); DARK=(40,40,50,255)
BROWN=(95,60,35,255); BLUE=(30,60,150,255); SILVER=(195,200,212,255)
PAPER=(232,215,170,255); PAPER2=(190,165,110,255); RED=(170,40,30,255)

p = {}
line(p,13,2,7,8,BLADE); line(p,13,3,8,8,BLADE2); p[(14,1)] = BLADE
line(p,6,9,3,12,BROWN); line(p,5,9,2,12,(130,85,50,255))
rect(p,1,13,2,14,SILVER); p[(1,13)] = DARK
write_png(f"{OUT}/item/kunai.png",16,16,p)

p = {}
for (a,b,c,d) in [(8,1,8,6),(7,3,7,6),(9,8,14,8),(9,7,12,7),(8,9,8,14),(9,9,9,12),(1,7,6,7),(3,8,6,8)]:
    line(p,a,b,c,d,BLADE)
for (a,b,c,d) in [(8,1,8,2),(14,8,13,8),(8,14,8,13),(1,7,2,7)]:
    line(p,a,b,c,d,BLADE2)
rect(p,7,7,8,8,DARK)
write_png(f"{OUT}/item/shuriken.png",16,16,p)

p = {}
rect(p,1,6,14,9,BLUE); rect(p,1,9,14,9,(20,40,110,255))
rect(p,5,5,10,10,SILVER); rect(p,5,10,10,10,BLADE2)
for q in [(7,7),(8,7),(8,8),(7,8),(7,6)]: p[q] = DARK
line(p,14,10,15,13,BLUE); line(p,13,10,13,13,BLUE)
write_png(f"{OUT}/item/konoha_headband.png",16,16,p)

p = {}
rect(p,3,5,12,10,PAPER); rect(p,3,10,12,10,PAPER2)
rect(p,1,4,2,11,RED); rect(p,13,4,14,11,RED)
rect(p,6,6,9,9,(60,150,255,255)); rect(p,7,7,8,8,(190,230,255,255))
write_png(f"{OUT}/item/rasengan_scroll.png",16,16,p)

p = {}
rect(p,3,5,12,10,PAPER); rect(p,3,10,12,10,PAPER2)
rect(p,1,4,2,11,RED); rect(p,13,4,14,11,RED)
rect(p,6,7,9,9,(235,90,20,255)); rect(p,7,6,8,6,(250,170,30,255)); rect(p,7,8,8,9,(255,220,70,255))
write_png(f"{OUT}/item/fireball_scroll.png",16,16,p)

p = {}
rect(p,2,8,13,9,(245,245,245,255)); rect(p,3,10,12,12,(245,245,245,255)); rect(p,5,13,10,13,(200,200,205,255))
rect(p,2,8,13,8,RED)
rect(p,3,6,12,8,(235,200,90,255))
for x in (4,6,8,10): p[(x,5)] = (235,200,90,255)
rect(p,9,6,10,7,(250,250,250,255)); p[(9,6)] = (250,190,40,255)
rect(p,4,7,5,7,(245,150,170,255))
line(p,12,1,9,6,BROWN); line(p,13,1,10,6,BROWN)
write_png(f"{OUT}/item/ramen.png",16,16,p)

p = {}
rect(p,0,10,31,12,BLUE); rect(p,0,12,31,12,(20,40,110,255))
rect(p,10,9,13,13,SILVER); rect(p,11,11,12,11,DARK)
rect(p,28,13,29,15,BLUE); rect(p,30,13,30,14,BLUE)
write_png(f"{OUT}/models/armor/konoha_layer_1.png",64,32,p)
write_png(f"{OUT}/models/armor/konoha_layer_2.png",64,32,{})
print("OK")
