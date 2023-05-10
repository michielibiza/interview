package nl.michiel.photogrid.boilerplate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import nl.michiel.photogrid.R
import nl.michiel.photogrid.view.PhotoGridFragment

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
