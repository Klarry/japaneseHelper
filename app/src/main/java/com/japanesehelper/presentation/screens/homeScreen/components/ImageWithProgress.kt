package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.japanesehelper.R
import com.japanesehelper.presentation.theme.JapaneseHelperTheme
import com.japanesehelper.presentation.viewmodel.screendata.PictureError
import com.japanesehelper.presentation.viewmodel.screendata.PictureLimitExceeded
import com.japanesehelper.presentation.viewmodel.screendata.PictureLoading
import com.japanesehelper.presentation.viewmodel.screendata.PictureScreenData
import com.japanesehelper.presentation.viewmodel.screendata.PictureSuccess

/**
 * Displays an image with a loading indicator or an error message
 * depending on the provided [data] state.
 *
 * Uses [PictureScreenData] to determine the UI state:
 * - If the data is loading — shows a progress indicator.
 * - If loading is successful — displays the image.
 * - If an error occurs — shows an image placeholder.
 *
 * @param data The current screen state containing image data (success, error, or loading).
 * @param modifier Modifier for customizing the appearance and layout.
 */
@Composable
fun ImageWithProgress(
    data: PictureScreenData,
    modifier: Modifier = Modifier
) {

    when (data) {
        is PictureError -> {
            Box(
                modifier = modifier
                    .aspectRatio(16f / 9f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.image_placeholder),
                    contentDescription = null,
                )
            }
        }

        is PictureSuccess -> {
            Box(
                modifier = modifier
                    .aspectRatio(16f / 9f),
                contentAlignment = Alignment.Center
            ) {

                var isLoading by remember { mutableStateOf(false) }

                if (isLoading) { CircularProgressIndicator() }

                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(data.url)
                        .error(R.drawable.image_placeholder)
                        .build(),
                    onState = { state ->
                        isLoading = when (state) {
                            is AsyncImagePainter.State.Loading -> true
                            is AsyncImagePainter.State.Success -> false
                            is AsyncImagePainter.State.Error -> false
                            else -> false
                        }
                    }
                )
                Crossfade(targetState = painter, modifier = Modifier.fillMaxSize()) { currentPainter ->
                    Image(
                        painter = currentPainter,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        is PictureLoading -> {
            Box(
                modifier = modifier
                    .aspectRatio(16f / 9f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is PictureLimitExceeded -> Unit
    }
}

@Composable
fun CircularProgressIndicator() {
    CircularProgressIndicator(
        modifier = Modifier
            .size(24.dp)
            .fillMaxWidth(),
        strokeWidth = 2.dp,
    )
}

@Preview(
    name = "ImageWithProgress Preview",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun ImageWithProgressPreview() {
    JapaneseHelperTheme {
        ImageWithProgress(
            modifier = Modifier.background(Color.LightGray),
            data = PictureSuccess(
                url = "https://picsum.photos/200/300"
            )
        )
    }
}