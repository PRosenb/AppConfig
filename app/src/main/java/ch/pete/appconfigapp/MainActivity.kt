package ch.pete.appconfigapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ch.pete.appconfigapp.configdetail.ConfigDetailFragment
import timber.log.Timber


class MainActivity : AppCompatActivity(), MainView {
    private lateinit var viewModel: AppConfigViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(AppConfigViewModel::class.java)
        viewModel.mainView = this
//        viewModel.initViewModel()
    }

    override fun showDetails(configId: Long) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val fragment = ConfigDetailFragment()

        fragment.arguments = Bundle().apply {
            putLong(ConfigDetailFragment.ARG_CONFIG_ENTRY_ID, configId)
        }
        fragmentTransaction
            .replace(
                R.id.fragmentContainer,
                fragment
            )

        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
