package com.example.practica06.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practica06.Contacto
import com.example.practica06.R

/**
 * ui/ContactoAdapter.kt
 *
 * PATRON ADAPTER: es el puente entre la coleccion de datos y el RecyclerView.
 *
 * - ViewHolder  -> mantiene las referencias a las vistas de un solo item
 *                 (evita el findViewById repetido, que es el cuello de
 *                 botella clasico del RecyclerView).
 * - Adapter     -> crea los ViewHolder, los recicla y les inyecta los datos
 *                 de cada posicion.
 *
 * Solo se inflan las vistas visibles; las que salen de pantalla se reciclan.
 */
class ContactoAdapter(
    private val contactos: List<Contacto>,
    private val onItemClick: (Contacto) -> Unit
) : RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder>() {

    /**
     * Devuelve el identificador unico del item. El RecyclerView lo usa para
     * distinguir un item de otro y reciclar correctamente.
     *
     * En RecyclerView 1.4.0 hasStableIds() es final y se activa solo cuando
     * getItemId devuelve un valor distinto de NO_ID.
     */
    override fun getItemId(position: Int): Long = contactos[position].id.toLong()

    /**
     * Solo se invoca cuando el RecyclerView necesita UN ViewHolder nuevo,
     * no en cada scroll.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contacto, parent, false)
        return ContactoViewHolder(vista) { contacto -> onItemClick(contacto) }
    }

    /**
     * Se invoca en cada elemento visible para rellenar sus datos.
     * Aqui ocurre el "binding".
     */
    override fun onBindViewHolder(holder: ContactoViewHolder, position: Int) {
        holder.bind(contactos[position])
    }

    /** Cantidad total de elementos de la coleccion. */
    override fun getItemCount(): Int = contactos.size

    /**
     * ViewHolder: conserva las referencias a las vistas del item.
     * Al reciclar el item se reutiliza la misma instancia.
     */
    class ContactoViewHolder(
        itemView: View,
        private val onClick: (Contacto) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val txtInicial: TextView = itemView.findViewById(R.id.txtInicial)
        private val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        private val txtTelefono: TextView = itemView.findViewById(R.id.txtTelefono)
        private val txtCorreo: TextView = itemView.findViewById(R.id.txtCorreo)
        private val txtCiudad: TextView = itemView.findViewById(R.id.txtCiudad)
        private val imgChevron: ImageView = itemView.findViewById(R.id.imgChevron)

        /** Inyecta los datos de un contacto en las vistas del item. */
        fun bind(contacto: Contacto) {
            txtInicial.text = contacto.inicial
            txtNombre.text = contacto.nombre
            txtTelefono.text = contacto.telefonoFormateado
            txtCorreo.text = contacto.correo
            txtCiudad.text = contacto.ciudad

            // Captura del evento de clic sobre el item.
            itemView.setOnClickListener {
                onClick(contacto)
            }
        }
    }
}