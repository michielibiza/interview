package nl.michiel.zenlyinterview

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.activity_main.pagerView
import nl.michiel.zenlyinterview.quiz2.GradientAnimator
import timber.log.Timber
import androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT as BEHAVIOUR

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())

        pagerView.adapter = initAdapter()
        pagerView.background = GradientDrawable().also {
            it.orientation = LEFT_RIGHT
            GradientAnimator(it, getColor(R.color.red), getColor(R.color.blue), getColor(R.color.green), getColor(R.color.yellow))
                .connect(pagerView)
        }
    }

    private fun initAdapter(): PagerAdapter {
        val pages = listOf(
            SimpleFragment(R.layout.start_page),
            SimpleFragment(R.layout.gradient_page),
            SimpleFragment(R.layout.counter_page),
        )

        return object: FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOUR) {
            override fun getCount(): Int = pages.size
            override fun getItem(position: Int): Fragment = pages[position]
        }
    }
}