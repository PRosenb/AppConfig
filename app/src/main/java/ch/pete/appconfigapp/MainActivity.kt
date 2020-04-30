package ch.pete.appconfigapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
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

        val adapter = ConfigEntryAdapter(
            onExecuteClickListener = {
                viewModel.onExecuteClicked(it)
            },
            onItemClickListener = {
                viewModel.onConfigEntryClicked(it)
            }
        )
        viewModel.configEntries.observe(this, Observer {
            adapter.submitList(it)
        })

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            val dividerItemDecoration = DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
            addItemDecoration(dividerItemDecoration)
            this.adapter = adapter
        }
    }
}
