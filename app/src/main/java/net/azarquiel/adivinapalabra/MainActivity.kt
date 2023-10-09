package net.azarquiel.adivinapalabra

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import kotlin.random.Random

class MainActivity : AppCompatActivity(), OnClickListener  {
    private lateinit var palabras: Array<String>
    private var terminando: Boolean = false
    private lateinit var tvIntentos: TextView
    private var intentos: Int = 0
    private lateinit var imagen: ImageView
    private var indiceBoton: Int = 0
    private var indices: Array<Int> = arrayOf(0, 0, 0, 0, 0)
    private lateinit var letrasDesordenadas: ArrayList<String>
    private lateinit var letrasPalabra: ArrayList<String>
    private lateinit var random: Random
    private var palabra: String = ""
    private lateinit var images: ArrayList<ImageView>
    private lateinit var llhDown: LinearLayout
    private lateinit var llhLetras: LinearLayout
    private lateinit var llhUp: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvIntentos = findViewById(R.id.tvIntentos)
        llhLetras = findViewById(R.id.llhLetras)
        images = ArrayList(llhLetras.childCount)
        for (i in 0 until llhLetras.childCount) {
            images.add(llhLetras.getChildAt(i) as ImageView)
        }
        random = Random(System.currentTimeMillis())
        palabras = resources.getStringArray(R.array.palabras)
        dibujaPalabra()
        addListeners()
    }

    private fun addListeners() {
        llhUp = findViewById(R.id.llhUp)
        for (i in 0 until llhUp.childCount) {
            llhUp.getChildAt(i).setOnClickListener(this)
        }
        llhDown = findViewById(R.id.llhDown)
        for (i in 0 until llhDown.childCount) {
            llhDown.getChildAt(i).setOnClickListener(this)
        }
    }

    private fun dibujaPalabra() {
        creaPalabra()
        for (i in images.indices) {
            indices[i] = letrasPalabra.indexOf(letrasDesordenadas[i])
            val color = eligeColor(i)

            val letraId = resources.getIdentifier("${letrasDesordenadas[i]}$color", "drawable", packageName)
            images[i].setImageResource(letraId)
        }
    }

    private fun eligeColor(i: Int): String {
        return if (indices[i] == i) {
            "v"
        } else {
            "b"
        }
    }

    private fun creaPalabra() {
        palabra = getPalabrAleatoria()
        letrasPalabra = ArrayList(palabra.length)
        letrasDesordenadas = ArrayList(palabra.length)
        for (i in palabra.indices) {
            letrasPalabra.add(palabra[i].toString().lowercase(Locale.getDefault()))
        }
        letrasDesordenadas = letrasPalabra.shuffled(random) as ArrayList<String>
    }

    private fun getPalabrAleatoria(): String {
        val num = (palabras.indices).random(random)
        return palabras[num]
    }

    override fun onClick(v: View?) {
        if (terminando) {
            return
        }
        val btn = v as Button
        indiceBoton = (btn.tag.toString().toInt())
        imagen = images[indiceBoton]
        intento()

        // poner la imagen de la letra en el imageview correspondiente
        if (btn.parent == llhUp) {
            cambiaImagen(-1)
        } else if (btn.parent == llhDown) {
            cambiaImagen(+1)
        }

        if (compruebaPalabra()) {
            terminando = true
            muestraDialogo()
        }
    }

    private fun muestraDialogo() {
        AlertDialog.Builder(this)
            .setTitle("Felicidades.")
            .setMessage("Lo has conseguido en $intentos intentos.")
            .setCancelable(false)
            .setPositiveButton("New Game") { dialog, which ->
                newGame()
            }
            .setNegativeButton("Exit") { dialog, which ->
                finish()
            }
            .show()
    }

    private fun newGame() {
        terminando = false
        intentos = 0
        tvIntentos.text = "Intentos: $intentos"
        indices = arrayOf(0, 0, 0, 0, 0)
        dibujaPalabra()
    }

    private fun compruebaPalabra(): Boolean {
        for (i in indices.indices) {
            // si hay alguna letra que no esté en su sitio, devolvemos false
            if (letrasPalabra[i] != letrasPalabra[indices[i]]) {
                return false
            }
        }
        return true
    }

    private fun intento() {
        intentos++
        tvIntentos.text = "Intentos: $intentos"
    }

    private fun cambiaImagen(i: Int) {
        indices[indiceBoton] = compruebaIndice(indices[indiceBoton] + i)
        val letra = letrasPalabra[indices[indiceBoton]]
        val letraId = resources.getIdentifier("${letra}${compruebaLetra(letra)}", "drawable", packageName)
        imagen.setImageResource(letraId)
    }

    private fun compruebaLetra(letra: String): String {
        return if (letrasPalabra[indiceBoton] == letra) {
            "v"
        } else {
            "r"
        }
    }

    private fun compruebaIndice(i: Int): Int {
        return if (i < 0) {
            4
        } else if (i > 4) {
            0
        } else {
            i
        }
    }

}