package nl.michiel.zenlyinterview

import android.graphics.Color.green
import android.graphics.Color.red
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.activity_main.pagerView
import androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT as BEHAVIOR

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pagerView.adapter = initAdapter()
        pagerView.background = GradientDrawable().also {
            it.orientation = LEFT_RIGHT
            GradientAnimator(it, getColor(R.color.red), getColor(R.color.blue), getColor(R.color.green), getColor(R.color.yellow))
                .connect(pagerView)
        }
    }

    private fun initAdapter(): PagerAdapter {
        val pages = listOf(
            SimpleFragment(R.layout.page1),
            SimpleFragment(R.layout.page2),
            SimpleFragment(R.layout.page3)
        )

        return object: FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR) {
            override fun getCount(): Int = pages.size
            override fun getItem(position: Int): Fragment = pages[position]
        }
    }

}