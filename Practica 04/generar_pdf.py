# -*- coding: utf-8 -*-
"""Genera el PDF de evidencias de la Practica 04 - Controles avanzados en Android."""
from PIL import Image, ImageDraw, ImageFont

EVID = r"C:\Users\alfar\Desktop\practica04\evidencias"
OUT = r"C:\Users\alfar\Desktop\practica04\Practica04_Controles_Avanzados.pdf"
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
d.text((90, 410), "Practica 04", font=font(76, bold=True), fill=DARK)
d.text((90, 520), "Controles avanzados en Android", font=font(44, bold=True), fill=INDIGO)
d.text((90, 640), "Spinner, RadioGroup/RadioButton, CheckBox, Switch y DatePicker (Jetpack Compose)",
       font=font(26), fill=GREY)

y = 850
d.line([90, y, 1150, y], fill=LIGHT, width=2)
y += 60
for label, value in [
    ("Materia", "Desarrollo Movil Nativo"),
    ("Alumno", "Luis Alfaro (LCoderCraft)"),
    ("Repositorio GitHub", "https://github.com/LCoderCraft/Practicas-Alfaro"),
    ("Fecha", "Septiembre 2026"),
]:
    d.text((110, y), label, font=font(24, bold=True), fill=INDIGO)
    d.text((420, y), value, font=font(24), fill=DARK)
    y += 58

d.text((90, 1320), "Entregable de Moodle - Practica 4", font=font(24, italic=True), fill=GREY)
d.text((90, 1370), "Capturas de cada control avanzado en funcionamiento + codigo en GitHub.",
       font=font(22, italic=True), fill=GREY)
pages.append(img)

# ---------- Directorio de capturas ----------
DESC = [
    ("01_formulario_inicial.png", "1. Formulario inicial",
     "Pantalla principal (FormScreen.kt) con todos los controles avanzados maquetados de forma "
     "ordenada: CheckBoxs (Deporte y Lectura), Switch, RadioGroup de nivel, Spinner de ciudad y "
     "DatePicker de fecha de nacimiento."),
    ("02_controles_seleccionados.png", "2. Seleccion de controles (checkboxes, switch, radio)",
     "Se activaron las dos casillas CheckBox, se encendio el Switch de notificaciones y se eligio la "
     "opcion 'Intermedio' del grupo de RadioButton. Cada control captura su evento en tiempo real "
     "mediante onCheckedChange/onSelect."),
    ("03_spinner_abierto.png", "3. Spinner desplegado",
     "El Spinner (ExposedDropdownMenuBox) muestra la lista desplegable con las opciones: "
     "Ciudad de Mexico, Guadalajara, Monterrey y Puebla."),
    ("04_spinner_seleccionado.png", "4. Spinner con opcion seleccionada",
     "Se selecciono 'Guadalajara' mediante el OnItemSelected (DropdownMenuItem onClick). La "
     "seleccion queda reflejada en el campo del formulario."),
    ("05_datepicker_abierto.png", "5. DatePicker abierto",
     "El boton de fecha abre el calendario nativo de Material 3 (DatePickerDialog + DatePicker) "
     "para elegir el dia, mes y anio."),
    ("06_fecha_seleccionada.png", "6. Fecha seleccionada",
     "Tras aceptar el calendario, la fecha se formatea a 'dd/MM/yyyy' y queda capturada en el "
     "campo 'Selecciona tu fecha de nacimiento: 06/09/2026'."),
    ("07_resumen_formulario.png", "7. Procesamiento del formulario",
     "El boton 'Procesar' recolecta el valor de todos los controles y los muestra resumidos en una "
     "tarjeta (Card): Deporte: Si, Lectura: Si, Notificaciones: Activadas, Nivel: Intermedio, "
     "Ciudad: Guadalajara y Fecha: 06/09/2026."),
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
    "Control          | Archivo                     | Evento principal",
    "-----------------|-----------------------------|----------------------",
    "CheckBox         | components/customCheckbox.kt | onCheckedChange: (Boolean)",
    "Switch           | components/customSwitch.kt   | onCheckedChange: (Boolean)",
    "RadioButton      | components/customRadioButton.kt | onSelect: (Int)",
    "Spinner/Dropdown | components/customSpinner.kt  | onSelect: (String)",
    "DatePicker       | components/customDatepicker.kt | onDateChange: (String)",
    "",
    "Estructura del proyecto (Jetpack Compose):",
    "  practica04/app/src/main/java/com/example/practica04/",
    "      MainActivity.kt      -> setContent { FormScreen() }",
    "      FormScreen.kt        -> Formulario: integra todos los controles y el boton Procesar",
    "      components/          -> un archivo independiente por cada control",
    "",
    "Repositorio publico en GitHub (Control de Versiones):",
    "  URL: https://github.com/LCoderCraft/Practicas-Alfaro",
    "",
    "Opcion A - Repositorio dedicado 'practica04':",
    "  cd practica04 && git init && git add . && git commit -m \"Practica 04\"",
    "  git branch -M main && git remote add origin https://github.com/LCoderCraft/practica04.git",
    "  git push -u origin main",
    "",
    "Opcion B - Anadir la carpeta practica04 a tu repo Practicas-Alfaro:",
    "  git clone https://github.com/LCoderCraft/Practicas-Alfaro.git",
    "  copiar practica04 dentro del repositorio clonado",
    "  git add practica04 && git commit -m \"Practica 04: Controles avanzados\" && git push origin main",
]:
    d.text((90, y), line, font=font(26), fill=DARK)
    y += 60
pages.append(img)

img.save(OUT, "PDF", save_all=True, append_images=pages[1:], resolution=150)
print("PDF generado:", OUT)