# Practica04 - Controles avanzados en Android

Practica 4 de Desarrollo Movil Nativo: formulario con **controles avanzados** de seleccion usando
**Jetpack Compose**, modularizando cada control en archivos independientes.

## Controles implementados

| Control          | Archivo                        | Evento principal               |
|------------------|--------------------------------|--------------------------------|
| CheckBox         | components/customCheckbox.kt   | `onCheckedChange: (Boolean) -> Unit` |
| Switch           | components/customSwitch.kt     | `onCheckedChange: (Boolean) -> Unit` |
| RadioButton      | components/customRadioButton.kt | `onSelect: (Int) -> Unit`      |
| Spinner/Dropdown | components/customSpinner.kt    | `onSelect: (String) -> Unit`   |
| DatePicker       | components/customDatepicker.kt | `onDateChange: (String) -> Unit` |

## Estructura del proyecto

```
app/src/main/java/com/example/practica04/
    MainActivity.kt          -> setContent { FormScreen() }
    FormScreen.kt            -> Formulario: integra todos los controles y el boton Procesar
    components/              -> un archivo independiente por cada control
        customCheckbox.kt
        customSwitch.kt
        customRadioButton.kt
        customSpinner.kt
        customDatepicker.kt
```

## Requisitos

- Android Studio (compatibilidad con AGP 9.4)
- JDK 17
- Android SDK (compileSdk 37)
- Dependencia `androidx.compose.material:material-icons-extended` (iconos Material extendidos)

## Funcionamiento

- Cada control captura su seleccion en tiempo real mediante su escuchador de eventos.
- El boton **Procesar** recolecta el valor de todos los controles y lo muestra resumido en una
  tarjeta (Card) al final del formulario.
- El **DatePicker** usa el calendario nativo de Material 3 y formatea la fecha a `dd/MM/yyyy`.

## Evidencias

Las capturas de la interaccion con cada control estan en `evidencias/` y tambien integradas en
`Practica04_Controles_Avanzados.pdf`.

## Publicacion en GitHub

Repositorio publico: https://github.com/LCoderCraft/Practicas-Alfaro

**Opcion A - Repositorio dedicado `practica04`:**

```bash
git init
git add .
git commit -m "Practica 04: Controles avanzados"
git branch -M main
git remote add origin https://github.com/LCoderCraft/practica04.git
git push -u origin main
```

**Opcion B - Anadir la carpeta `practica04` a tu repo `Practicas-Alfaro`:**

```bash
git clone https://github.com/LCoderCraft/Practicas-Alfaro.git
# copiar la carpeta practica04 dentro del repositorio clonado
git add practica04
git commit -m "Practica 04: Controles avanzados"
git push origin main
```