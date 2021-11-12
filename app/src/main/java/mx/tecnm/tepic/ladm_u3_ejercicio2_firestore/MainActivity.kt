package mx.tecnm.tepic.ladm_u3_ejercicio2_firestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()
    var datalista = ArrayList<String>()
    var listaID = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        baseRemota.collection("persona")
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    mensaje(error.message!!)
                    return@addSnapshotListener
                }
                datalista.clear()
                listaID.clear()
                for (document in querySnapshot!!){
                    var cadena = "${document.getString("nombre")} -- ${document.get(("telefono"))}"
                    datalista.add(cadena)

                    listaID.add(document.id.toString())
                }
                listapersonas.adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, datalista)
                listapersonas.setOnItemClickListener { adapterView, view, posicion, l ->
                    dialogoEliminarActualiza(posicion)
                }
            }

        button.setOnClickListener {
            insertar()
        }
    }

    private fun dialogoEliminarActualiza(posicion: Int) {
        var idElegido = listaID.get(posicion)

        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage("¿QUE DESEA HACER CON\n${datalista.get(posicion)}?")
            .setPositiveButton("ELIMINAR"){d,i -> eliminar(idElegido)}
            .setNeutralButton("ACTUALIZAR"){d,i->}
            .setNegativeButton("CANCELAR"){d,i->}
            .show()
    }

    private fun eliminar(idElegido: String) {
        baseRemota.collection("persona")
            .document(idElegido)
            .delete()
            .addOnSuccessListener {
                alerta("SE ELIMINO CON EXITO")
            }
            .addOnFailureListener {
                mensaje("ERROR: ${it.message!!}")
            }
    }

    private fun insertar() {
        //para INSERTAR el método a usar es ADD
        //ADD espera todos los campos del documento
        //con formato CLAVE VALOR

        var datosInsertar = hashMapOf(
            "nombre" to nombre.text.toString(),
            "domicilio" to domicilio.text.toString(),
            "telefono" to telefono.text.toString()
        )

        baseRemota.collection("persona")
            .add(datosInsertar as Any)
            .addOnSuccessListener {
                alerta("SE INSERTO CORRECTAMENTE EN LA NUBE")
            }
            .addOnFailureListener {
                mensaje("ERROR: ${it.message!!}")
            }
        nombre.setText("")
        domicilio.setText("")
        telefono.setText("")
    }

    private fun alerta(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){d,i->}
            .show()
    }
}