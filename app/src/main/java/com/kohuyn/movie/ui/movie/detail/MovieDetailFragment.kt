package com.kohuyn.movie.ui.movie.detail

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.kohuyn.movie.R
import com.kohuyn.movie.databinding.FragmentMovieDetailBinding
import com.kohuyn.movie.model.MovieDetail
import com.kohuyn.movie.ui.alert.MessageDialog
import com.kohuyn.movie.ui.login.LoginDialog
import com.kohuyn.movie.ui.movie.adapter.MovieCastAdapter
import com.kohuyn.movie.ui.movie.adapter.MovieRecommendationAdapter
import com.kohuyn.movie.utils.StorageCache
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class MovieDetailFragment : Fragment() {
    companion object {
        private const val ARG_MOVIE_ID = "ARG_MOVIE_ID"
        fun navigateToMovieDetail(navController: NavController, movieId: Int) {
            navController.navigate(R.id.fragment_movie_detail, bundleOf(ARG_MOVIE_ID to movieId))
        }
    }

    private lateinit var binding: FragmentMovieDetailBinding

    private val onOffsetChangeListener = AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
        lifecycleScope.launchWhenStarted {
            binding.toolbar.navigationIcon?.setTint(if (verticalOffset.absoluteValue != 0) Color.BLACK else Color.WHITE)
            if (verticalOffset.absoluteValue != 0) {
                binding.fabFavorite.extend()
            } else {
                binding.fabFavorite.shrink()
            }

        }
    }

    private val movieId by lazy {
        arguments?.getInt(ARG_MOVIE_ID) ?: -1
    }

    private val vm by viewModels<MovieDetailViewModel>()

    private val recommendationAdapter by lazy { MovieRecommendationAdapter() }
    private val seriesCastAdapter by lazy { MovieCastAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        binding.appBarDetail.addOnOffsetChangedListener(onOffsetChangeListener)
        super.onStart()
    }

    override fun onStop() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        binding.appBarDetail.removeOnOffsetChangedListener(onOffsetChangeListener)
        super.onStop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.getMovie(movieId)
        initRcv()
        initListener()
        observeViewModel()
    }

    private fun initRcv() {
        //recommendation
        binding.rcvRecommendation.apply {
            adapter = recommendationAdapter
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        //series cast
        binding.rcvSeriesCast.apply {
            adapter = seriesCastAdapter
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun initListener() {
        recommendationAdapter.onItemClick = { movieId ->
            navigateToMovieDetail(findNavController(), movieId)
        }
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.fabFavorite.setOnClickListener {
            val favoriteClick = {
                vm.movieDetail.value?.isFavorite?.not()?.let { isFavorite ->
                    vm.favorite(movieId, isFavorite)
                } ?: kotlin.run {
                    Toast.makeText(
                        requireContext(),
                        "Some error,Pls retry",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            if (StorageCache.token.isNullOrBlank()) {
                LoginDialog()
                    .show(childFragmentManager)
                    .setOnLoginSuccess {
                        favoriteClick()
                    }
            } else {
                favoriteClick()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            vm.movieDetail
                .flowWithLifecycle(lifecycle)
                .mapNotNull { it }
                .collect { movie ->
                    bindMovie(movie)
                }
        }
        lifecycleScope.launch {
            vm.loading
                .flowWithLifecycle(lifecycle)
                .collect { isLoading ->
                    setLoading(isLoading)
                }
        }
        lifecycleScope.launch {
            vm.loadingSeriesCast
                .flowWithLifecycle(lifecycle)
                .collect { isLoading ->
                    setLoadingSeriesCast(isLoading)
                }
        }
        lifecycleScope.launch {
            vm.loadingMovieRecommendation
                .flowWithLifecycle(lifecycle)
                .collect { isLoading ->
                    setLoadingRecommendation(isLoading)
                }
        }
        lifecycleScope.launch {
            vm.loadingFavorite
                .flowWithLifecycle(lifecycle)
                .collect { isLoading ->
                    setLoadingFavorite(isLoading)
                }
        }
        lifecycleScope.launch {
            vm.messages
                .flowWithLifecycle(lifecycle)
                .collect { messages ->
                    if (messages.isNotEmpty()) {
                        val messageShow = messages.first()
                        MessageDialog.Builder()
                            .setMessage(messageShow.message)
                            .setButtonNegative { dialog -> dialog.dismiss() }
                            .setButtonPositive(getString(R.string.retry)) { dialog ->
                                dialog.dismiss()
                                vm.getMovie(movieId)
                            }
                            .setOnDismissListener { vm.setMessageShown(messageShow.message) }
                            .setCancelable(false)
                            .build(childFragmentManager)
                    }
                }
        }
    }

    private fun bindMovie(movie: MovieDetail) {
        Glide.with(this)
            .load(movie.backdropPath)
            .centerCrop()
            .into(binding.ivThumbnail)

        Glide.with(this)
            .load(movie.posterPath)
            .into(binding.ivPoster)

        binding.collapsingToolbar.title = movie.title
        binding.tvStatusLanguage.text = getString(R.string.status_value, movie.status)
        binding.tvSpokenLanguage.text =
            getString(R.string.spoken_language_value, movie.spokenLanguage)
        binding.tvReleaseDate.text = getString(R.string.release_date_value, movie.releaseDate)
        binding.tvGenres.text = getString(R.string.genres_value, movie.genres)
        binding.tvRating.text = getString(R.string.rating_value, movie.voteCount?.toString())
        binding.ratingBar.rating = movie.rating
        binding.tvOverview.text = movie.overview
        //series cast
        movie.seriesCast.isNullOrEmpty().let { isEmpty ->
            binding.tvSeriesCastLabel.isGone = isEmpty
            binding.rcvSeriesCast.isGone = isEmpty
        }
        seriesCastAdapter.items = movie.seriesCast
        //recommendation
        movie.movieRecommendations.isNullOrEmpty().let { isEmpty ->
            binding.tvRecommendationLabel.isGone = isEmpty
            binding.rcvRecommendation.isGone = isEmpty
        }
        recommendationAdapter.items = movie.movieRecommendations
        //favorite
        binding.fabFavorite.icon = ResourcesCompat.getDrawable(
            resources,
            if (movie.isFavorite) R.drawable.ic_favorited else R.drawable.ic_favorite,
            null
        )
        binding.fabFavorite.text =
            getString(if (movie.isFavorite) R.string.un_favorite else R.string.favorite)
    }

    private fun setLoading(isLoading: Boolean) {
        binding.clDetail.isGone = isLoading
        if (isLoading) {
            binding.loadingProgress.show()
        } else {
            binding.loadingProgress.hide()
        }
    }

    private fun setLoadingRecommendation(isLoading: Boolean) {
        binding.rcvRecommendation.isGone = isLoading
        if (isLoading) {
            binding.loadingRecommendationProgress.show()
        } else {
            binding.loadingRecommendationProgress.hide()
        }
    }

    private fun setLoadingSeriesCast(isLoading: Boolean) {
        binding.rcvSeriesCast.isGone = isLoading
        if (isLoading) {
            binding.loadingSeriesCastProgress.show()
        } else {
            binding.loadingSeriesCastProgress.hide()
        }
    }

    private fun setLoadingFavorite(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingFavoriteProgress.show()
        } else {
            binding.loadingFavoriteProgress.hide()
        }
        val colorBackground = ContextCompat.getColor(
            requireContext(),
            if (isLoading) android.R.color.darker_gray else R.color.secondary
        )
        val colorTextIcon = ContextCompat.getColor(
            requireContext(),
            if (isLoading) android.R.color.darker_gray else R.color.white
        )
        binding.fabFavorite.backgroundTintList = ColorStateList.valueOf(colorBackground)
        binding.fabFavorite.iconTint = ColorStateList.valueOf(colorTextIcon)
        binding.fabFavorite.setTextColor(colorTextIcon)
        binding.fabFavorite.isEnabled = isLoading.not()

    }
}