# -*- coding: utf-8 -*-
"""Genera el PDF de evidencias de la Práctica 03 - Intents y Navegación."""
from PIL import Image, ImageDraw, ImageFont

EVID = r"C:\Users\alfar\Desktop\practica03\evidencias"
OUT = r"C:\Users\alfar\Desktop\practica03\Practica03_Intents_y_Navegacion.pdf"
FONT = r"C:\Windows\Fonts\arial.ttf"
FONT_B = r"C:\Windows\Fonts\arialbd.ttf"
FONT_I = r"C:\Windows\Fonts\ariali.ttf"

W, H = 1240, 1754  # A4 @ 150dpi
INDIGO = (63, 81, 181)
DARK = (33, 33, 33)
GREY = (117, 117, 117)
WHITE = (255, 255, 255)
LIGHT = (245, 246, 250)


def font(size, bold=False, italic=False):
    path = FONT_B if bold else (FONT_I if italic else FONT)
    return ImageFont.truetype(path, size)


def base_page(title=None):
    img = Image.new("RGB", (W, H), WHITE)
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, W, 10], fill=INDIGO)
    if title:
        d.rectangle([0, 60, W, 116], fill=LIGHT)
        d.text((60, 74), title, font=font(28, bold=True), fill=INDIGO)
    return img


pages = []

# ---------- Portada ----------
img = Image.new("RGB", (W, H), WHITE)
d = ImageDraw.Draw(img)
d.rectangle([0, 0, W, 12], fill=INDIGO)
d.rectangle([0, 300, 12, H], fill=INDIGO)
d.text((90, 340), "Desarrollo Movil Nativo", font=font(30, bold=True), fill=GREY)
d.text((90, 410), "Practica 03", font=font(76, bold=True), fill=DARK)
d.text((90, 520), "Intents y Navegacion entre Pantallas", font=font(44, bold=True), fill=INDIGO)
d.text((90, 640), "Intent explícito, Intent implícito y transferencia de datos (Jetpack Compose)",
       font=font(26), fill=GREY)

y = 850
d.line([90, y, 1150, y], fill=LIGHT, width=2)
y += 60
for label, value in [
    ("Materia", "Desarrollo Movil Nativo"),
    ("Tipo de entrega", "Evidencia: PDF + ZIP del proyecto"),
    ("Alumno", "Luis Alfaro (LCoderCraft)"),
    ("Repositorio GitHub", "https://github.com/LCoderCraft/Practicas-Alfaro"),
    ("Fecha", "Septiembre 2026"),
]:
    d.text((110, y), label, font=font(24, bold=True), fill=INDIGO)
    d.text((420, y), value, font=font(24), fill=DARK)
    y += 58

d.text((90, 1320), "Entregable de Moodle - Actividad 4.2", font=font(24, italic=True), fill=GREY)
d.text((90, 1370), "Screenshots de navegacion en emulador + codigo fuente en GitHub.",
       font=font(22, italic=True), fill=GREY)
pages.append(img)

# ---------- Directorio de capturas ----------
DESC = [
    ("01_pantalla_principal.png", "1. Pantalla Principal",
     "Pantalla inicial creada con Jetpack Compose: campos de texto (Nombre, Correo electronico y "
     "Mensaje) y los botones 'Ver Perfil' (Intent explícito) y 'Compartir Datos' (Intent implícito)."),
    ("02_principal_nombre.png", "2. Captura de datos - Nombre",
     "Se captura el dato 'Nombre' en el primer campo de texto de la pantalla principal."),
    ("03_principal_datos.png", "3. Captura de datos - Nombre y Correo",
     "Se capturan el 'Nombre' y el 'Correo electronico'. Estos valores se adjuntaran al Intent "
     "mediante putExtra() para transferirlos a la siguiente pantalla."),
    ("04_perfil_destino.png", "4. Pantalla de Destino (navegacion con Intent explícito)",
     "Al presionar 'Ver Perfil' se ejecuta un Intent explícito hacia ProfileActivity. La activity "
     "recibe el Intent, extrae los extras (getStringExtra) y la tarjeta (Card) despliega los datos "
     "transferidos sin perdida de informacion."),
    ("05_regreso_principal.png", "5. Regreso a la Pantalla Principal",
     "El boton 'Regresar' ejecuta la funcion lambda onBackClick -> finish() y se vuelve a la "
     "pantalla principal, comprobando la navegacion en ambos sentidos."),
    ("06_mensaje_escrito.png", "6. Mensaje para compartir",
     "Se escribe un mensaje en el campo correspondiente para probar el Intent implícito."),
    ("07_compartir_selector.png", "7. Intent implícito (Share/Send)",
     "Al presionar 'Compartir Datos' se crea un Intent de accion ACTION_SEND y el sistema operativo "
     "muestra el selector de aplicaciones (Quick Share, Gmail, Chrome, Drive, etc.)."),
    ("08_cierre_selector.png", "8. Regreso tras cerrar el selector",
     "Se cierra el selector ('Atras') y la app permanece sobre la pantalla principal, sin errores ni "
     "cierre inesperado."),
]

for fname, title, desc in DESC:
    img = base_page(title)
    d = ImageDraw.Draw(img)
    shot = Image.open(rf"{EVID}\{fname}").convert("RGB")
    target_w = W - 180
    target_h = H - 360
    scale = min(target_w / shot.width, target_h / shot.height)
    nw, nh = int(shot.width * scale), int(shot.height * scale)
    shot = shot.resize((nw, nh), Image.LANCZOS)
    x = (W - nw) // 2
    y = 170
    img.paste(shot, (x, y))
    d.rectangle([x - 6, y - 6, x + nw + 6, y + nh + 6], outline=INDIGO, width=3)
    d.text((70, y + nh + 40), desc, font=font(26), fill=DARK)
    pages.append(img)

# ---------- Pagina del codigo / repositorio ----------
img = base_page("Codigo fuente y Repositorio GitHub")
d = ImageDraw.Draw(img)
y = 160
for line in [
    "Estructura del proyecto (Jetpack Compose):",
    "",
    "  practica03/",
    "    app/src/main/java/com/example/practica03/",
    "        MainActivity.kt     -> setContent { MainScreen() }",
    "        MainScreen.kt       -> Pantalla Principal + Intents (explícito e implícito)",
    "        ProfileActivity.kt  -> Activity destino: recibe y extrae el Intent",
    "        ProfileScreen.kt    -> Tarjeta (Card) que despliega los datos recibidos",
    "    app/src/main/AndroidManifest.xml -> declaracion de MainActivity y ProfileActivity",
    "",
    "Navegacion implementada:",
    "  - Intent EXPLICITO: Intent(context, ProfileActivity::class.java) + putExtra(nombre/correo)",
    "      -> el destino usa intent.getStringExtra(...) y lo muestra en la tarjeta.",
    "  - Intent IMPLICITO: Intent(ACTION_SEND, type text/plain) + createChooser.",
    "",
    "Repositorio publico en GitHub (Control de Versiones - 20%):",
    "  URL: https://github.com/LCoderCraft/Practicas-Alfaro",
    "",
    "Opcion A - Repositorio dedicado 'practica03':",
    "  cd practica03",
    "  git init && git add . && git commit -m \"Practica 03: Intents y navegacion\"",
    "  git branch -M main",
    "  git remote add origin https://github.com/LCoderCraft/practica03.git",
    "  git push -u origin main",
    "",
    "Opcion B - Anadir a tu repo 'Practicas-Alfaro' (carpeta practica03):",
    "  git clone https://github.com/LCoderCraft/Practicas-Alfaro.git",
    "  copiar la carpeta practica03 dentro del repositorio clonado",
    "  git add practica03 && git commit -m \"Practica 03: Intents y navegacion\"",
    "  git push origin main",
]:
    d.text((90, y), line, font=font(26), fill=DARK)
    y += 58
pages.append(img)

img.save(OUT, "PDF", save_all=True, append_images=pages[1:], resolution=150)
print("PDF generado:", OUT)