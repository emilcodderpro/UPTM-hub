import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uptmhub.ItemModel
import com.example.uptmhub.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class testdisplay : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Database reference
        dbRef = FirebaseDatabase.getInstance().getReference("Items")

        // Initialize the TextView
        textView = findViewById(R.id.displayitem)

        // Fetch data from Firebase
        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        // Set up a listener to get the data from Firebase
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Check if data exists
                if (snapshot.exists()) {
                    // Inside the onDataChange method
                    val data = StringBuilder() // StringBuilder to store item details

// Iterate through each child node in the "Items" node
                    for (itemSnapshot in snapshot.children) {
                        // Get the item data
                        val item = itemSnapshot.getValue(ItemModel::class.java)

                        if (item != null) {
                            // Append the basic item details to the StringBuilder
                            data.append("Item Name: ${item.name}\n")
                            data.append("Description: ${item.description}\n")
                            data.append("Price: \$${item.price}\n")

                            // Append the sizes (join list elements into a string)
                            if (item.sizes.isNotEmpty()) {
                                data.append("Sizes: ${item.sizes.joinToString(", ")}\n")
                            } else {
                                data.append("Sizes: Not available\n")
                            }

                            // Append the colors (join list elements into a string)
                            if (item.colors.isNotEmpty()) {
                                data.append("Colors: ${item.colors.joinToString(", ")}\n")
                            } else {
                                data.append("Colors: Not available\n")
                            }

                            // Append a newline for separating items
                            data.append("\n")
                        }
                    }

// Set the data to the TextView after all items are processed
                    textView.text = data.toString()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(this@testdisplay, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
