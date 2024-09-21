package RecyclerViewHelpers

import Kevin.alvarado.R
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

    val txtNombreP: TextView = view.findViewById(R.id.txtNombreP)
    val txtEdadP: TextView = view.findViewById(R.id.txtEdadP)
    val txtPesoP: TextView = view.findViewById(R.id.txtPesoP)
    val txtCorreoP: TextView = view.findViewById(R.id.txtCorreoP)
    val imgEditar: ImageView = view.findViewById(R.id.imgEditar)
    val imgBorrar: ImageView = view.findViewById(R.id.imgBorrar)

}