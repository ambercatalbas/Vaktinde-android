package com.ambercatalbas.vaktinde.feature.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ambercatalbas.vaktinde.core.ui.R
import com.ambercatalbas.vaktinde.core.ui.theme.Gold
import com.ambercatalbas.vaktinde.core.ui.theme.GoldDeep
import com.ambercatalbas.vaktinde.core.ui.theme.GoldSoft
import kotlinx.coroutines.launch
import kotlin.random.Random

private val BgDark = Color(0xFF060D17)
private val TextLight = Color(0xFFEEF4FB)
private val TextDim = Color(0xFFDCE8F6)

data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val description: String,
)

@Composable
private fun pages() = listOf(
    OnboardingPage(
        icon = Icons.Default.Nightlight,
        title = stringResource(R.string.onboarding_welcome_title),
        subtitle = stringResource(R.string.onboarding_welcome_subtitle),
        description = stringResource(R.string.onboarding_welcome_desc),
    ),
    OnboardingPage(
        icon = Icons.Default.LocationOn,
        title = stringResource(R.string.onboarding_location_title),
        subtitle = stringResource(R.string.onboarding_location_subtitle),
        description = stringResource(R.string.onboarding_location_desc),
    ),
    OnboardingPage(
        icon = Icons.Default.Notifications,
        title = stringResource(R.string.onboarding_notif_title),
        subtitle = stringResource(R.string.onboarding_notif_subtitle),
        description = stringResource(R.string.onboarding_notif_desc),
    ),
)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
) {
    val pages = pages()
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
    ) {
        // Star field background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val random = Random(42)
            repeat(30) {
                val x = random.nextFloat() * size.width
                val y = random.nextFloat() * size.height
                val opacity = 0.15f + random.nextFloat() * 0.35f
                val radius = 0.5f + random.nextFloat() * 1f
                drawCircle(
                    color = Color.White.copy(alpha = opacity),
                    radius = radius.dp.toPx(),
                    center = Offset(x, y),
                    style = Fill,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
        ) {
            // Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                if (pagerState.currentPage < pages.size - 1) {
                    TextButton(onClick = onComplete) {
                        Text(
                            text = stringResource(R.string.skip),
                            color = TextDim.copy(alpha = 0.6f),
                            fontSize = 15.sp,
                        )
                    }
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                OnboardingPageContent(pages[page])
            }

            // Page indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                pages.forEachIndexed { index, _ ->
                    val isActive = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isActive) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) Gold else Color.White.copy(alpha = 0.2f)
                            ),
                    )
                }
            }

            // Button
            val isLastPage = pagerState.currentPage == pages.size - 1
            Button(
                onClick = {
                    if (isLastPage) {
                        onComplete()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .padding(bottom = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = Color(0xFF1A1000),
                ),
            ) {
                Text(
                    text = if (isLastPage) stringResource(R.string.start) else stringResource(R.string.continue_text),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Icon with glow
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Gold.copy(alpha = 0.2f), Color.Transparent),
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(52.dp),
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = page.title,
            color = TextLight,
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = page.subtitle,
            color = GoldSoft,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = page.description,
            color = TextDim.copy(alpha = 0.65f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
        )
    }
}
