package nl.michiel.photogrid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import nl.michiel.photogrid.ui.photogrid.PhotoGridFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PhotoGridFragment()).commitNow()
        }
    }
}