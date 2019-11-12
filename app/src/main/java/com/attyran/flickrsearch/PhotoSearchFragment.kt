package com.attyran.flickrsearch

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.KeyEvent
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.attyran.flickrsearch.di.PhotoSearchApplication
import com.attyran.flickrsearch.di.PhotoSearchViewModelFactory
import com.attyran.flickrsearch.network.Photo
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_photo_search.*
import javax.inject.Inject

class PhotoSearchFragment : androidx.fragment.app.Fragment() {

    @Inject
    lateinit var viewModelFactory: PhotoSearchViewModelFactory
    private lateinit var viewModel: PhotoSearchViewModel

    private var compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        PhotoSearchApplication.appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PhotoSearchViewModel::class.java)

        return inflater.inflate(R.layout.fragment_photo_search, container, false)
    }

    override fun onStart() {
        super.onStart()

        setupRecyclerView()
        viewModel.observePhotos().observe(this, Observer {
            it?.let {
                val adapter = search_results_rv.adapter as FlickrAdapter
                adapter.submitList(it.photos.photo)
            }
        })

        val itemInputNameObservable = RxTextView.textChanges(tag_field)
                .map { inputText: CharSequence -> inputText.isEmpty() }
                .distinctUntilChanged()
        compositeDisposable.add(setupTextInputObserver(itemInputNameObservable))
        tag_field.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                tag_field.hideKeyboard()
                if (!tag_field.text.isNullOrEmpty())
                    viewModel.search(tag_field.text.toString())
                return@OnKeyListener true
            }
            false
        })

        search_button.setOnClickListener {
            tag_field.hideKeyboard()
            viewModel.search(tag_field.text.toString())
        }
    }

    private fun setupTextInputObserver(itemInputNameObservable: Observable<Boolean>): Disposable {
        return itemInputNameObservable.subscribe { inputIsEmpty: Boolean ->
            search_button.isEnabled = !inputIsEmpty
        }
    }

    private fun setupRecyclerView() {
        search_results_rv.layoutManager = GridLayoutManager(context, 2)
        search_results_rv.adapter = FlickrAdapter(object : FlickrAdapter.FlickrAdapterViewHolder.Interactor {
            override fun onItemSelected(photo: Photo, image: ImageView) {
                // todo
            }
        })
    }
}