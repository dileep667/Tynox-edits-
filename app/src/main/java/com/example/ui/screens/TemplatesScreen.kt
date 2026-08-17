package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AspectRatio
import com.example.data.model.ProjectFps
import com.example.data.model.ProjectResolution
import com.example.ui.theme.*
import com.example.viewmodel.HomeViewModel

data class CreatorTemplate(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val durationText: String,
    val imageRes: Int,
    val aspectRatio: AspectRatio,
    val resolution: ProjectResolution,
    val fps: ProjectFps
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    homeViewModel: HomeViewModel,
    onNavigateBack: () -> Unit,
    onOpenEditor: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val templates = listOf(
        CreatorTemplate(
            id = "template_viral_beast",
            title = "Viral Shorts & Reels Master",
            description = "High energy pacing with bouncy karaoke captions, sound effects, and neon beat drops.",
            category = "Trending Shorts",
            durationText = "12s • 9:16",
            imageRes = R.drawable.sample_cyberpunk,
            aspectRatio = AspectRatio.RATIO_9_16,
            resolution = ProjectResolution.RES_1080P,
            fps = ProjectFps.FPS_60
        ),
        CreatorTemplate(
            id = "template_podcast_caps",
            title = "Studio Podcast Auto-Captions",
            description = "Modern pill-badge subtitles with word-by-word highlight and crystal clear voice preset.",
            category = "Podcasts & Talks",
            durationText = "15s • 9:16",
            imageRes = R.drawable.sample_podcast_host,
            aspectRatio = AspectRatio.RATIO_9_16,
            resolution = ProjectResolution.RES_1080P,
            fps = ProjectFps.FPS_30
        ),
        CreatorTemplate(
            id = "template_travel_cinematic",
            title = "Cinematic Travel & Vlog",
            description = "Teal & orange color grade, ambient drone soundscapes, and smooth zoom-in transitions.",
            category = "Cinematic Travel",
            durationText = "10s • 16:9",
            imageRes = R.drawable.sample_reels_travel,
            aspectRatio = AspectRatio.RATIO_16_9,
            resolution = ProjectResolution.RES_4K,
            fps = ProjectFps.FPS_60
        ),
        CreatorTemplate(
            id = "template_cyberpunk_neon",
            title = "Cyberpunk 2077 Night Reel",
            description = "Neon glitch text animations, scanline effects, and heavy synthwave audio track.",
            category = "Sci-Fi & Cyber",
            durationText = "11s • 9:16",
            imageRes = R.drawable.sample_cyberpunk,
            aspectRatio = AspectRatio.RATIO_9_16,
            resolution = ProjectResolution.RES_4K,
            fps = ProjectFps.FPS_60
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = TyoxBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Creator Templates",
                        color = TyoxTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("btn_templates_back")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TyoxTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TyoxSurface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pick a template to instantly start editing with pre-timed tracks, captions, and effects.",
                    color = TyoxTextSecondary,
                    fontSize = 12.sp
                )
            }

            items(templates, key = { it.id }) { template ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = TyoxSurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TyoxBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Thumbnail with play badge
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        ) {
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = template.imageRes),
                                contentDescription = template.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Category Badge
                            Box(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .align(Alignment.TopStart)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(TyoxObsidianDark.copy(alpha = 0.85f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(template.category, color = TyoxCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            // Duration
                            Box(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.8f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(template.durationText, color = Color.White, fontSize = 10.sp)
                            }
                        }

                        // Content & CTA
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(template.title, color = TyoxTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(template.description, color = TyoxTextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    homeViewModel.createNewProject(
                                        name = template.title,
                                        aspectRatio = template.aspectRatio,
                                        resolution = template.resolution,
                                        fps = template.fps
                                    ) { newId ->
                                        onOpenEditor(newId)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TyoxCyan, contentColor = Color.Black),
                                modifier = Modifier.fillMaxWidth().testTag("btn_use_${template.id}")
                            ) {
                                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Use Template", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
