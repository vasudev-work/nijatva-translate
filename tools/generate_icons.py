"""Generate Nijatva Translate launcher icons.

Design: white outline speech bubble centred on a dark midnight navy rounded-square tile.
Rendered at 1024px and downsampled so every mipmap is crisp.
"""
import math
import os

from PIL import Image, ImageDraw

OUT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RES = os.path.join(OUT, "app", "src", "main", "res")
DOCS = os.path.join(OUT, "docs")
MASTER = 2048
S = MASTER / 108.0

NAVY_TOP = (26, 44, 70)
NAVY_MID = (17, 28, 48)
NAVY_BOT = (8, 14, 26)
WHITE = (255, 255, 255, 255)
STROKE = 3.6


def gradient_bg(size=MASTER):
    img = Image.new("RGB", (size, size))
    draw = ImageDraw.Draw(img)
    for y in range(size):
        t = y / (size - 1)
        if t < 0.5:
            k = t / 0.5
            c = tuple(int(NAVY_TOP[i] + (NAVY_MID[i] - NAVY_TOP[i]) * k) for i in range(3))
        else:
            k = (t - 0.5) / 0.5
            c = tuple(int(NAVY_MID[i] + (NAVY_BOT[i] - NAVY_MID[i]) * k) for i in range(3))
        draw.line([(0, y), (size, y)], fill=c)
    return img


def arc(cx, cy, r, a0, a1, steps=96):
    return [
        (cx + r * math.cos(math.radians(a0 + (a1 - a0) * i / steps)),
         cy + r * math.sin(math.radians(a0 + (a1 - a0) * i / steps)))
        for i in range(steps + 1)
    ]


def bubble_points():
    pts = [(39, 36), (69, 36)]
    pts += arc(69, 45, 9, -90, 0)
    pts += [(78, 61)]
    pts += arc(69, 61, 9, 0, 90)
    pts += [(52, 70), (40, 80), (45, 70), (39, 70)]
    pts += arc(39, 61, 9, 90, 180)
    pts += [(30, 45)]
    pts += arc(39, 45, 9, 180, 270)
    return [(x * S, y * S) for x, y in pts]


def master(mask_shape="squircle"):
    bg = gradient_bg()
    tile = Image.new("L", (MASTER, MASTER), 0)
    d = ImageDraw.Draw(tile)
    if mask_shape == "circle":
        d.ellipse([0, 0, MASTER - 1, MASTER - 1], fill=255)
    else:
        radius = int(108 * 0.225 * S)
        d.rounded_rectangle([0, 0, MASTER - 1, MASTER - 1], radius=radius, fill=255)

    canvas = Image.new("RGBA", (MASTER, MASTER), (0, 0, 0, 0))
    canvas.paste(bg, (0, 0), tile)

    layer = Image.new("RGBA", (MASTER, MASTER), (0, 0, 0, 0))
    ld = ImageDraw.Draw(layer)
    pts = bubble_points()
    ld.line(pts + [pts[0]], fill=WHITE, width=int(STROKE * S), joint="curve")
    canvas = Image.alpha_composite(canvas, layer)
    return canvas


def save(img, path, size):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.resize((size, size), Image.LANCZOS).save(path, "PNG", optimize=True)


DENSITIES = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}

squircle = master("squircle")
circle = master("circle")

for density, size in DENSITIES.items():
    save(squircle, os.path.join(RES, f"mipmap-{density}", "ic_launcher.png"), size)
    save(circle, os.path.join(RES, f"mipmap-{density}", "ic_launcher_round.png"), size)

os.makedirs(DOCS, exist_ok=True)
save(squircle, os.path.join(DOCS, "icon.png"), 512)
save(squircle, os.path.join(OUT, "app", "src", "main", "res", "drawable-nodpi", "icon_preview.png"), 432)

print("icons written")
