package com.japanesehelper.presentation.screens.homeScreen.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.japanesehelper.R
import com.japanesehelper.presentation.theme.JapaneseHelperTheme
import com.japanesehelper.presentation.viewmodel.screendata.PictureError
import com.japanesehelper.presentation.viewmodel.screendata.PictureLimitExceeded
import com.japanesehelper.presentation.viewmodel.screendata.PictureLoading
import com.japanesehelper.presentation.viewmodel.screendata.PictureState
import com.japanesehelper.presentation.viewmodel.screendata.PictureSuccess

private const val PIC_ASPECT_RATIO = 16f / 9f

private const val IMAGE_WIDTH_FRACTION = 0.7f

@Composable
fun ImageWithProgress(
    data: PictureState,
    modifier: Modifier = Modifier
) {
    when (data) {
        is PictureError -> {
            PictureFrame(modifier) {
                Image(
                    painter = painterResource(R.drawable.image_placeholder),
                    contentDescription = null
                )
            }
        }

        is PictureSuccess -> {
            PictureFrame(modifier) {
                var isLoading by remember { mutableStateOf(false) }

                if (isLoading) AppProgressIndicator()

                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(data.imageBytes)
                        .error(R.drawable.image_placeholder)
                        .build(),
                    onState = { state -> isLoading = state is AsyncImagePainter.State.Loading }
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

        is PictureLoading -> PictureFrame(modifier) { AppProgressIndicator() }

        is PictureLimitExceeded -> Unit
    }
}

@Composable
private fun PictureFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth(IMAGE_WIDTH_FRACTION)
            .aspectRatio(PIC_ASPECT_RATIO),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ImageWithProgressPreview() {
    JapaneseHelperTheme {
        ImageWithProgress(
            modifier = Modifier.background(Color.LightGray),
            data = PictureLoading()
        )
    }
}
