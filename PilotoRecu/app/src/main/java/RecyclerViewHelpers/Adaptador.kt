package RecyclerViewHelpers

import Kevin.alvarado.R
import Modelo.ClaseConexion
import Modelo.piloto
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Adaptador (private var Datos: List<piloto>): RecyclerView.Adapter<ViewHolder>() {

    fun actualizarRecyclerView(nuevaLista: List<piloto>){
        Datos = nuevaLista
        notifyDataSetChanged() //Notifica que hay datos nuevos
    }

    fun actualicePantalla(uuid: String, nuevoNombreP: String){
        val index = Datos.indexOfFirst { it.UUID_Piloto == uuid }
        Datos[index].Nombre_Piloto = nuevoNombreP
        notifyDataSetChanged()
    }

    fun EliminarP(Nombre_Piloto: String, Edad_Piloto: Int, Peso_Piloto: Double, Correo_Piloto: String,  posicion: Int){
        //Notificar al adaptador
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        //Quitar de la base de datos
        GlobalScope.launch(Dispatchers.IO){
            //Dos pasos para eliminar de la base de datos

            //1- Crear un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- Creo una variable que contenga un PrepareStatement
            val deleteProducto = objConexion?.prepareStatement("delete tbPiloto where UUID_Piloto = ?")!!
            deleteProducto.setString(1, Nombre_Piloto)
            deleteProducto.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()
        }

        //Notificamos el cambio para que refresque la lista
        Datos = listaDatos.toList()
        //Quito los datos de la lista
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }
    fun actualizarDato(nuevoNombreP: String, UUID_Piloto: String){
        GlobalScope.launch(Dispatchers.IO){

            //1- Creo un objeto de la clase de conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- creo una variable que contenga un PrepareStatement
            val updateTicket = objConexion?.prepareStatement("update tbPiloto set Nombre_Piloto = ? where UUID_Piloto = ?")!!
            updateTicket.setString(1, nuevoNombreP)
            updateTicket.setString(2, UUID_Piloto)
            updateTicket.executeUpdate()

            withContext(Dispatchers.Main){
                actualicePantalla(UUID_Piloto, nuevoNombreP)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_card_piloto, parent, false)
        return ViewHolder(vista)

    }

    override fun getItemCount() = Datos.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = Datos[position]
        holder.txtNombreP.text = item.Nombre_Piloto
        holder.txtEdadP.text = item.Edad_Piloto.toString()
        holder.txtPesoP.text = item.Peso_Piloto.toString()
        holder.txtCorreoP.text = item.Correo_Piloto

        //todo: clic al icono de eliminar
        holder.imgBorrar.setOnClickListener {

            //Creamos un Alert Dialog
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Desea eliminar el Ticket?")

            //Botones
            builder.setPositiveButton("Si") { dialog, which ->
                EliminarP(item.Nombre_Piloto, item.Edad_Piloto, item.Peso_Piloto, item.Correo_Piloto, position)
            }

            builder.setNegativeButton("No"){dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

        }


        //Todo: Clic a la card completa

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            //Cambiar de pantalla a la pantalla de detalle
            val pantallaDetalle = Intent(context, piloto::class.java)
            //enviar a la otra pantalla todos mis valores
            pantallaDetalle.putExtra("UUID_Piloto", item.UUID_Piloto)
            pantallaDetalle.putExtra("Nombre_Piloto", item.Nombre_Piloto)
            pantallaDetalle.putExtra("Edad_Piloto", item.Edad_Piloto)
            pantallaDetalle.putExtra("Peso_Piloto", item.Peso_Piloto)
            pantallaDetalle.putExtra("Correo_Piloto", item.Correo_Piloto)
            context.startActivity(pantallaDetalle)
        }
    }

}