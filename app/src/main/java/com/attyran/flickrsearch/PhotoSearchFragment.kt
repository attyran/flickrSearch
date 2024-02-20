package com.attyran.flickrsearch

import android.os.Bundle
import android.view.KeyEvent
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.attyran.flickrsearch.databinding.FragmentPhotoSearchBinding
import com.attyran.flickrsearch.di.PhotoSearchApplication
import com.attyran.flickrsearch.di.PhotoSearchViewModelFactory
import com.attyran.flickrsearch.network.Photo
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class PhotoSearchFragment : androidx.fragment.app.Fragment() {

    @Inject
    lateinit var viewModelFactory: PhotoSearchViewModelFactory
    private lateinit var viewModel: PhotoSearchViewModel

    private var compositeDisposable = CompositeDisposable()

    private var _binding: FragmentPhotoSearchBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentPhotoSearchBinding.inflate(inflater, container, false)
        val view = binding.root
        PhotoSearchApplication.appComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[PhotoSearchViewModel::class.java]

        return view
    }

    override fun onStart() {
        super.onStart()

        setupRecyclerView()
        viewModel.observePhotos().observe(this) {
            it?.let {
                val adapter = binding.searchResultsRv.adapter as FlickrAdapter
                adapter.submitList(it.photos.photo)
            }
        }

        val itemInputNameObservable = RxTextView.textChanges(binding.tagField)
                .map { inputText: CharSequence -> inputText.isEmpty() }
                .distinctUntilChanged()
        compositeDisposable.add(setupTextInputObserver(itemInputNameObservable))
        binding.tagField.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                binding.tagField.hideKeyboard()
                if (!binding.tagField.text.isNullOrEmpty())
                    viewModel.search(binding.tagField.text.toString())
                return@OnKeyListener true
            }
            false
        })

        binding.searchButton.setOnClickListener {
            binding.tagField.hideKeyboard()
            viewModel.search(binding.tagField.text.toString())
        }
    }

    private fun setupTextInputObserver(itemInputNameObservable: Observable<Boolean>): Disposable {
        return itemInputNameObservable.subscribe { inputIsEmpty: Boolean ->
            binding.searchButton.isEnabled = !inputIsEmpty
        }
    }

    private fun setupRecyclerView() {
        binding.searchResultsRv.layoutManager = GridLayoutManager(context, 2)
        binding.searchResultsRv.adapter = FlickrAdapter(object : FlickrAdapter.FlickrAdapterViewHolder.Interactor {
            override fun onItemSelected(photo: Photo, image: ImageView) {
                // todo
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}