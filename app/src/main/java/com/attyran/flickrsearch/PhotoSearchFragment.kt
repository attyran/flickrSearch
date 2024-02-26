package com.attyran.flickrsearch

import android.os.Bundle
import android.view.KeyEvent
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.attyran.flickrsearch.databinding.FragmentPhotoSearchBinding
import com.attyran.flickrsearch.di.PhotoSearchApplication
import com.attyran.flickrsearch.di.PhotoSearchViewModelFactory
import com.attyran.flickrsearch.network.Photo
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject
import androidx.lifecycle.lifecycleScope
import com.jakewharton.rxbinding2.widget.RxTextView
import kotlinx.coroutines.launch

class PhotoSearchFragment : Fragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        setupRecyclerView()
        observePhotoSearchResponse()
        setupSearchField()
        setupSearchButton()
    }

    private fun observePhotoSearchResponse() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.photoState.collect { response ->
                val adapter = binding.searchResultsRv.adapter as FlickrAdapter
                adapter.submitList(response.photos.photo)
            }
        }
    }

    private fun setupSearchField() {
        val itemInputNameObservable = RxTextView.textChanges(binding.tagField)
            .map { inputText: CharSequence -> inputText.isEmpty() }
            .distinctUntilChanged()
        compositeDisposable.add(setupTextInputObserver(itemInputNameObservable))
        binding.tagField.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                binding.tagField.hideKeyboard()
                if (!binding.tagField.text.isNullOrEmpty())
                    viewModel.search(binding.tagField.text.toString())
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun setupSearchButton() {
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