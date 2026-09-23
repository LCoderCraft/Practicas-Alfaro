# Practica03 - Intents y Navegacion

Practica 3 de Desarrollo Movil Nativo: navegacion entre pantallas mediante **Intents** (explicitos e
implicitos) y **transferencia de datos** entre activities, con interfaz grafica construida
íntegramente con **Jetpack Compose**.

## Requisitos

- Android Studio (Iguana o superior) / compatibilidad con AGP 9.4
- JDK 17
- Android SDK (compileSdk 37)

## Estructura del proyecto

```
app/src/main/java/com/example/practica03/
    MainActivity.kt       -> setContent { MainScreen() }
    MainScreen.kt         -> Pantalla Principal + Intents (explicito e implicito)
    ProfileActivity.kt    -> Activity de destino: recibe y extrae el Intent
    ProfileScreen.kt      -> Tarjeta/Card que despliega los datos recibidos
app/src/main/AndroidManifest.xml -> declaracion de MainActivity y ProfileActivity
```

## Navegacion implementada

- **Intent explicito:** el boton "Ver Perfil" crea `Intent(context, ProfileActivity::class.java)` y
  adjunta los extras con `putExtra("nombre", ...)` y `putExtra("correo", ...)`. La actividad de
  destino los extrae con `getStringExtra(...)` y los muestra en una tarjeta (Card).
- **Intent implicito:** el boton "Compartir Datos" crea `Intent(Intent.ACTION_SEND)` con
  `type = "text/plain"` y muestra el selector del sistema mediante `Intent.createChooser(...)`.

## Como abrir el proyecto

1. Abrir Android Studio -> File -> Open -> seleccionar la carpeta `practica03`.
2. Esperar a que Gradle sincronice (AGP 9.4.0).
3. Ejecutar en un emulador/dispositivo con el boton Run.

## Evidencias (capturas)

Las capturas de la navegacion activa en el emulador se encuentran en la carpeta `evidencias/` y
tambien estan integradas en `Practica03_Intents_y_Navegacion.pdf`.

## Publicacion en GitHub (Control de Versiones)

Repositorio publico: https://github.com/LCoderCraft/Practicas-Alfaro

**Opcion A - Repositorio dedicado `practica03`:**

```bash
git init
git add .
git commit -m "Practica 03: Intents y navegacion"
git branch -M main
git remote add origin https://github.com/LCoderCraft/practica03.git
git push -u origin main
```

**Opcion B - Anadir la carpeta `practica03` a tu repo `Practicas-Alfaro`:**

```bash
git clone https://github.com/LCoderCraft/Practicas-Alfaro.git
# copiar la carpeta practica03 dentro del repositorio clonado
git add practica03
git commit -m "Practica 03: Intents y navegacion"
git push origin main
```