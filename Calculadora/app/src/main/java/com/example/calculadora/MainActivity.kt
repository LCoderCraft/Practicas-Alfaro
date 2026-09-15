package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadora.ui.theme.CalculadoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraTheme {
                Calculadora()
            }
        }
    }
}

@Composable
fun Calculadora() {
    // Valor que se muestra en la pantalla
    var display by remember { mutableStateOf("0") }

    // Guarda el primer numero de la operacion
    var memoria by remember { mutableStateOf<Double?>(null) }

    // Guarda la operacion elegida (+, -, x, /)
    var operador by remember { mutableStateOf<String?>(null) }

    // Indica si el proximo digito inicia un numero nuevo
    var esperandoNumero by remember { mutableStateOf(false) }

    fun formatear(numero: Double): String {
        // Muestra los numeros sin decimales cuando son enteros
        return if (numero % 1.0 == 0.0) numero.toLong().toString() else numero.toString()
    }

    fun limpiar() {
        display = "0"
        memoria = null
        operador = null
        esperandoNumero = false
    }

    fun calcular() {
        val numero = display.toDoubleOrNull() ?: return

        // Si no hay operacion pendiente, el numero actual es el primero
        if (operador == null) {
            memoria = numero
            return
        }

        val primero = memoria ?: return

        var resultado = 0.0
        when (operador) {
            "+" -> resultado = primero + numero
            "-" -> resultado = primero - numero
            "x" -> resultado = primero * numero
            "/" -> {
                // Evita caer en una division entre cero
                if (numero == 0.0) {
                    display = "Error"
                    esperandoNumero = true
                    return
                }
                resultado = primero / numero
            }
        }
        display = formatear(resultado)
        memoria = resultado
        esperandoNumero = true
    }

    fun digitar(digito: String) {
        // Si hay un error, el siguiente digito reinicia todo
        if (display == "Error") {
            display = "0"
            memoria = null
            operador = null
        }
        if (esperandoNumero) {
            display = digito
            esperandoNumero = false
        } else {
            display = if (display == "0") digito else display + digito
        }
    }

    fun elegirOperador(op: String) {
        if (display == "Error") {
            limpiar()
        }
        calcular()
        operador = op
        esperandoNumero = true
    }

    fun presionarIgual() {
        if (display == "Error") return
        calcular()
        operador = null
    }

    fun retroceder() {
        if (display == "Error") limpiar()
        if (display == "0") return
        display = if (display.length == 1) "0" else display.dropLast(1)
    }

    fun punto() {
        if (display == "Error") return
        if (esperandoNumero) {
            display = "0."
            esperandoNumero = false
        } else if (!display.contains(".")) {
            display += "."
        }
    }

    fun porcentaje() {
        if (display == "Error") return
        val numero = display.toDoubleOrNull() ?: return
        display = formatear(numero / 100.0)
    }

    fun signo() {
        if (display == "Error" || display == "0") return
        display = if (display.startsWith("-")) display.drop(1) else "-" + display
    }

    // Colores de la calculadora
    val colorFondo = Color(0xFF1C1C1C)
    val colorPantalla = Color(0xFF111111)
    val colorDigito = Color(0xFF3A3A3C)
    val colorOperador = Color(0xFFFF9500)
    val colorAlt = Color(0xFFA5A5A5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo)
            .safeDrawingPadding()
    ) {
        // Pantalla de resultados
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = colorPantalla
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = display,
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    textAlign = TextAlign.End,
                    modifier = Modifier.testTag("pantalla")
                )
            }
        }

        // Fila 1: limpiar, borrar, porcentaje, division
        FilaBotones("C", "Del", "%", "/", colorFondo, colorDigito, colorOperador, colorAlt, Modifier.fillMaxWidth().weight(1f)) { etiqueta ->
            when (etiqueta) {
                "C" -> limpiar()
                "Del" -> retroceder()
                "%" -> porcentaje()
                else -> elegirOperador(etiqueta)
            }
        }

        // Fila 2: 7 8 9 x
        FilaBotones("7", "8", "9", "x", colorFondo, colorDigito, colorOperador, colorAlt, Modifier.fillMaxWidth().weight(1f)) { etiqueta ->
            when (etiqueta) {
                "7", "8", "9" -> digitar(etiqueta)
                else -> elegirOperador(etiqueta)
            }
        }

        // Fila 3: 4 5 6 -
        FilaBotones("4", "5", "6", "-", colorFondo, colorDigito, colorOperador, colorAlt, Modifier.fillMaxWidth().weight(1f)) { etiqueta ->
            when (etiqueta) {
                "4", "5", "6" -> digitar(etiqueta)
                else -> elegirOperador(etiqueta)
            }
        }

        // Fila 4: 1 2 3 +
        FilaBotones("1", "2", "3", "+", colorFondo, colorDigito, colorOperador, colorAlt, Modifier.fillMaxWidth().weight(1f)) { etiqueta ->
            when (etiqueta) {
                "1", "2", "3" -> digitar(etiqueta)
                else -> elegirOperador(etiqueta)
            }
        }

        // Fila 5: 0 (ocupa dos columnas), punto, igual
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            BotonCalculadora("0", colorDigito, Modifier.weight(2f)) { digitar("0") }
            BotonCalculadora(".", colorDigito, Modifier.weight(1f)) { punto() }
            BotonCalculadora("=", colorOperador, Modifier.weight(1f)) { presionarIgual() }
        }
    }
}

// Boton individual de la calculadora
@Composable
fun BotonCalculadora(
    etiqueta: String,
    colorFondo: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colorTexto = if (colorFondo == Color(0xFFA5A5A5)) Color.Black else Color.White
    Button(
        onClick = onClick,
        modifier = modifier.padding(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorFondo,
            contentColor = colorTexto
        )
    ) {
        Text(text = etiqueta, fontSize = 26.sp)
    }
}

// Fila generica de cuatro botones
@Composable
fun FilaBotones(
    e1: String,
    e2: String,
    e3: String,
    e4: String,
    colorFondo: Color,
    colorDigito: Color,
    colorOperador: Color,
    colorAlt: Color,
    modifier: Modifier = Modifier,
    onPresionar: (String) -> Unit
) {
    Row(
        modifier = modifier
    ) {
        val esOperador = { etiqueta: String ->
            etiqueta == "/" || etiqueta == "x" || etiqueta == "-" || etiqueta == "+"
        }
        val esAlt = { etiqueta: String ->
            etiqueta == "C" || etiqueta == "Del" || etiqueta == "%"
        }
        listOf(e1, e2, e3, e4).forEach { etiqueta ->
            val color = when {
                esOperador(etiqueta) -> colorOperador
                esAlt(etiqueta) -> colorAlt
                else -> colorDigito
            }
            BotonCalculadora(etiqueta, color, Modifier.weight(1f)) { onPresionar(etiqueta) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VistaPrevia() {
    CalculadoraTheme {
        Calculadora()
    }
}