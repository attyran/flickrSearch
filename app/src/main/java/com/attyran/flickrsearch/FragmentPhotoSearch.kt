package com.attyran.flickrsearch

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.KeyEvent
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_photo_search.*
import javax.inject.Inject

class FragmentPhotoSearch : Fragment() {

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

        val itemInputNameObservable = RxTextView.textChanges(tag_field)
                .map { inputText: CharSequence -> inputText.isEmpty() }
                .distinctUntilChanged()
        compositeDisposable.add(setupTextInputObserver(itemInputNameObservable))
        tag_field.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                tag_field.hideKeyboard()
                if (!tag_field.text.isNullOrEmpty())
                    searchTag(tag_field.text.toString())
                return@OnKeyListener true
            }
            false
        })

        search_button.setOnClickListener {
            tag_field.hideKeyboard()
            searchTag(tag_field.text.toString())
        }
    }

    private fun setupTextInputObserver(itemInputNameObservable: Observable<Boolean>): Disposable {
        return itemInputNameObservable.subscribe { inputIsEmpty: Boolean ->
            search_button.isEnabled = !inputIsEmpty
        }
    }

    private fun setupRecyclerView(list: List<Photo>) {
        search_results_rv.layoutManager = GridLayoutManager(context, 2)
        search_results_rv.adapter = FlickrAdapter(list)
    }

    private fun searchTag(tag: String) {
        viewModel.search(tag, object : PhotoSearchClient.PhotoSearchClientCallback {
            override fun onSuccess(response: PhotoSearchResponse) {
                setupRecyclerView(response.photos.photo)
            }

            override fun onError(errorMessage: Throwable) {
            }
        })
    }
}