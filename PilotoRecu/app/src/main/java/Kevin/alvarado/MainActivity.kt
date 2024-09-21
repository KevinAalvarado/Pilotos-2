package Kevin.alvarado

import Modelo.ClaseConexion
import Modelo.piloto
import RecyclerViewHelpers.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtNombrePiloto = findViewById<EditText>(R.id.txtNombrePiloto)
        val txtEdadPiloto = findViewById<EditText>(R.id.txtEdadPiloto)
        val txtPesoPiloto = findViewById<EditText>(R.id.txtPesoPiloto)
        val txtCorreoPiloto = findViewById<EditText>(R.id.txtCorreoPiloto)
        val btnAgregarPiloto = findViewById<Button>(R.id.btnAgregarPiloto)
        val rcvInfoPiloto = findViewById<RecyclerView>(R.id.rcvInfoPiloto)

        rcvInfoPiloto.layoutManager = LinearLayoutManager(this)

        fun obtenerPilotos(): List<piloto> {

            val objConexion = ClaseConexion().cadenaConexion()

            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("select * from tbPiloto")!!

            val listaPiloto = mutableListOf<piloto>()

            while (resultSet.next()) {

                val UUID_Piloto = resultSet.getString("UUID_Piloto")
                val Nombre_Piloto = resultSet.getString("Nombre_Piloto")
                val Edad_Piloto = resultSet.getInt("Edad_Piloto")
                val Peso_Piloto = resultSet.getDouble("Peso_Piloto")
                val Correo_Piloto = resultSet.getString("Correo_Piloto")

                val valoresJuntos =
                    piloto(UUID_Piloto, Nombre_Piloto, Edad_Piloto, Peso_Piloto, Correo_Piloto)

                listaPiloto.add(valoresJuntos)
            }
            return listaPiloto

        }
        CoroutineScope(Dispatchers.IO).launch {
            val PilotoDB = obtenerPilotos()
            withContext(Dispatchers.Main){
                val adapter = Adaptador(PilotoDB)
                rcvInfoPiloto.adapter = adapter
            }
        }

        btnAgregarPiloto.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val objConexion = ClaseConexion().cadenaConexion()

                try {


                        // Validación de campos vacíos
                        if (txtNombrePiloto.text.isEmpty() || txtEdadPiloto.text.isEmpty() || txtPesoPiloto.text.isEmpty() ||
                            txtCorreoPiloto.text.isEmpty()
                        ) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Por favor, completa todos los campos.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            return@launch
                        }


                        val addTicket =
                            objConexion?.prepareStatement("insert into tbPiloto (UUID_Piloto, Nombre_Piloto, Edad_Piloto, Peso_Piloto, Correo_Piloto) values (?,?,?,?,?)")!!

                        addTicket.setString(1, UUID.randomUUID().toString())
                        addTicket.setString(2, txtNombrePiloto.text.toString())
                        addTicket.setInt(3, txtEdadPiloto.text.toString().toInt())
                        addTicket.setDouble(4, txtPesoPiloto.text.toString().toDouble())
                        addTicket.setString(5, txtCorreoPiloto.text.toString())

                        addTicket.executeUpdate()

                        //Refresco la lista
                        val nuevasMascotas = obtenerPilotos()
                        withContext(Dispatchers.Main) {
                            (rcvInfoPiloto.adapter as? Adaptador)?.actualizarRecyclerView(
                                nuevasMascotas
                            )
                        }

                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "Piloto creado", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                "Error: ${e.message}",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            println("Error: ${e.message}")
                            e.printStackTrace()
                        }

                    }
                }

            }
        }
    }

