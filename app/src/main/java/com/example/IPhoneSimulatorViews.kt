package com.example

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun IPhone13Chassis(
    viewModel: IPhoneSimulatorViewModel,
    modifier: Modifier = Modifier
) {
    val isPowerOn by viewModel.isPoweredOn.collectAsState()
    val isLocked by viewModel.isLocked.collectAsState()
    val currentWallpaperIndex by viewModel.currentWallpaperIndex.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val chosenWallpaper = viewModel.wallpapers[currentWallpaperIndex]

    // Screen dimension states
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDarkMode) Color(0xFF1C1B1F) else Color(0xFFF2F2F7)),
        contentAlignment = Alignment.Center
    ) {
        // Simulated graphite metallic outer frame for iPhone 13
        Box(
            modifier = Modifier
                .width(360.dp)
                .height(720.dp)
                .shadow(elevation = 24.dp, shape = RoundedCornerShape(40.dp))
                .clip(RoundedCornerShape(40.dp))
                .background(Color(0xFF2C2C2E)) // Graphite frame
                .padding(4.dp) // Bezel width representation
                .background(Color.Black) // Outer screen border bezel
                .padding(8.dp) // inner padding for realistic screen
        ) {
            // INNER ACTIVE SCREEN CONTAINER
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.Black)
            ) {
                if (isPowerOn) {
                    // WALLPAPER APPLIED
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(chosenWallpaper.gradient)
                    ) {
                        // System Screens Hierarchy
                        when {
                            isLocked -> {
                                LockScreen(viewModel)
                            }
                            else -> {
                                MainIosInterface(viewModel)
                            }
                        }

                        // Top CAMERA NOTCH (iPhone 13 Notch: 20% smaller than previous ones)
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .width(135.dp)
                                .height(26.dp)
                                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                                .background(Color.Black)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Microphone/Speaker line
                                Box(
                                    modifier = Modifier
                                        .width(42.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color.DarkGray)
                                )
                                // Camera lens reflection circle
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0F1E36))
                                )
                            }
                        }

                        // Floating ASSISTIVE TOUCH (iOS Core style)
                        AssistiveTouchFloatingButton(viewModel)
                    }
                } else {
                    // BLACK SLEEP SCREEN (Screen Off)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Toque o botão lateral para ligar ⚡",
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            }
        }

        // PHYSICAL POWER BUTTON AT THE SIDE edge of the chassis (Right hand side layout)
        Box(
            modifier = Modifier
                .offset(x = 182.dp, y = (-80).dp)
                .width(6.dp)
                .height(55.dp)
                .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                .background(Color(0xFF4E4E52))
                .clickable { viewModel.togglePower() }
        )
    }
}

@Composable
fun MainIosInterface(viewModel: IPhoneSimulatorViewModel) {
    val currentOpenApp by viewModel.currentOpenApp.collectAsState()

    // Slide/Fade Entry-Exit transitions for iOS Apps
    Box(modifier = Modifier.fillMaxSize()) {
        if (currentOpenApp == null) {
            HomeScreen(viewModel)
        } else {
            AnimatedContent(
                targetState = currentOpenApp,
                transitionSpec = {
                    (fadeIn() + slideInVertically { it }).togetherWith(fadeOut() + slideOutVertically { it })
                },
                label = "AppTransition"
            ) { appId ->
                when (appId) {
                    "store" -> AppStoreView(viewModel, onBack = { viewModel.openApp(null) })
                    "safari" -> SafariView(viewModel, onBack = { viewModel.openApp(null) })
                    "notes" -> NotesView(viewModel, onBack = { viewModel.openApp(null) })
                    "calc" -> CalculatorView(viewModel, onBack = { viewModel.openApp(null) })
                    "weather" -> WeatherView(viewModel, onBack = { viewModel.openApp(null) })
                    "photos" -> PhotosView(viewModel, onBack = { viewModel.openApp(null) })
                    "settings" -> SettingsView(viewModel, onBack = { viewModel.openApp(null) })
                    "phone" -> PhoneView(viewModel, onBack = { viewModel.openApp(null) })
                    "messages" -> MessagesView(viewModel, onBack = { viewModel.openApp(null) })
                    "music" -> MusicView(viewModel, onBack = { viewModel.openApp(null) })

                    // Downloadable Apps once launched from home screen
                    "whatsapp" -> MessagesView(viewModel, onBack = { viewModel.openApp(null) })
                    "instagram" -> PhotosView(viewModel, onBack = { viewModel.openApp(null) })
                    "youtube" -> SafariView(viewModel, onBack = { viewModel.openApp(null) })
                    "spotify" -> MusicView(viewModel, onBack = { viewModel.openApp(null) })
                    "tiktok" -> PhotosView(viewModel, onBack = { viewModel.openApp(null) })
                    "netflix" -> SafariView(viewModel, onBack = { viewModel.openApp(null) })
                }
            }
        }
    }
}

@Composable
fun IosStatusBar(
    isLightMode: Boolean,
    modifier: Modifier = Modifier
) {
    var currentTimeStr by remember { mutableStateOf("09:41") }

    // Tick real time ticking
    LaunchedEffect(Unit) {
        while (true) {
            val dateForm = SimpleDateFormat("HH:mm", Locale.getDefault())
            currentTimeStr = dateForm.format(Date())
            delay(1000)
        }
    }

    val contentColor = if (isLightMode) Color.White else Color.Black

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TIME on Left
        Text(
            text = currentTimeStr,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )

        // SYMBOLS on Right (Signal, Wifi, Battery)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Default.SignalCellularAlt, "Signal", tint = contentColor, modifier = Modifier.size(14.dp))
            Icon(Icons.Default.Wifi, "WiFi", tint = contentColor, modifier = Modifier.size(14.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(Icons.Default.BatteryFull, "Battery", tint = contentColor, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun LockScreen(viewModel: IPhoneSimulatorViewModel) {
    var hourStr by remember { mutableStateOf("20:41") }
    var dateStr by remember { mutableStateOf("Segunda, 25 de Maio") }

    LaunchedEffect(Unit) {
        while (true) {
            val hForm = SimpleDateFormat("HH:mm", Locale.getDefault())
            val calendar = Calendar.getInstance()
            hourStr = hForm.format(calendar.time)

            val dForm = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("pt", "BR"))
            dateStr = dForm.format(calendar.time).replaceFirstChar { it.uppercase() }
            delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        IosStatusBar(isLightMode = true)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "🔒 Locked",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = dateStr,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(primaryAlpha())
                )
                Text(
                    text = hourStr,
                    fontSize = 62.sp,
                    fontWeight = FontWeight.ExtraLight,
                    color = Color.White,
                    lineHeight = 65.sp
                )
            }

            // Lock Screen Notification Card Box
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💬", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("iMessage", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(0.7f))
                        }
                        Text("Mãe • Agora mesmo", fontSize = 9.sp, color = Color.White.copy(0.5f))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Filha, já testou todos os aplicativos do iPhone 13?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            // Click unlock prompt or button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = { viewModel.setLocked(false) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.25f)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .testTag("unlock_button")
                ) {
                    Text(
                        text = "Clique aqui para desbloquear 🔓",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                // Simulated camera/flashlight shortcuts at bottom
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FlashlightOn, "Flash", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, "Camera", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

private fun primaryAlpha() = 0.9f

@Composable
fun HomeScreen(viewModel: IPhoneSimulatorViewModel) {
    val apps by viewModel.appsList.collectAsState()
    val installedApps = apps.filter { it.isInstalled && !it.isAppStoreOffer }
    val offerApps = apps.filter { it.isInstalled && it.isAppStoreOffer }

    // Combine standard internal apps and installed App Store ones
    val desktopGrid = installedApps + offerApps

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        IosStatusBar(isLightMode = true)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 36.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // DESKTOP GRID OF APPS
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(desktopGrid) { app ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { viewModel.openApp(app.id) }
                            .padding(vertical = 4.dp)
                    ) {
                        // Icon Rounded frame
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(app.iconBgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(app.iconEmoji, fontSize = 26.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = app.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // PAGINATION SPOTS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(0.4f))
                )
            }

            // BOTTOM DOCK (Holds exactly 4 vital apps icons)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.24f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val dockApps = listOf(
                        Triple("phone", "Telefone", "📞"),
                        Triple("safari", "Safari", "🧭"),
                        Triple("messages", "iMessage", "💬"),
                        Triple("music", "Música", "🎵")
                    )

                    dockApps.forEach { (id, name, emoji) ->
                        val appThemeClr = when (id) {
                            "phone" -> Color(0xFF4CD964)
                            "safari" -> Color(0xFF34A853)
                            "messages" -> Color(0xFF4CD964)
                            else -> Color(0xFFFF2D55)
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { viewModel.openApp(id) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(appThemeClr),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 26.sp)
                            }
                        }
                    }
                }
            }

            // REAL HOME INDICATOR gesture bar representation at bottom center
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp, bottom = 4.dp)
                    .width(120.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White)
            )
        }
    }
}

// ==========================================
// INTERACTIVE ASSISTIVETOUCH ORB OVERLAY
// ==========================================
@Composable
fun AssistiveTouchFloatingButton(viewModel: IPhoneSimulatorViewModel) {
    val enabled by viewModel.isAssistiveTouchEnabled.collectAsState()
    if (!enabled) return

    val touchX by viewModel.assistiveTouchX.collectAsState()
    val touchY by viewModel.assistiveTouchY.collectAsState()
    var isMenuOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Floating Draggable Grey Button Orb
        Box(
            modifier = Modifier
                .offset { IntOffset(touchX.roundToInt(), touchY.roundToInt()) }
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.White.copy(0.12f))
                .padding(2.dp)
                .background(Color.Black.copy(0.4f), CircleShape)
                .padding(4.dp)
                .background(Color.White.copy(0.2f), CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newX = (touchX + dragAmount.x).coerceIn(10f, 600f)
                        val newY = (touchY + dragAmount.y).coerceIn(44f, 1300f)
                        viewModel.updateAssistiveTouchPos(newX, newY)
                    }
                }
                .clickable { isMenuOpen = !isMenuOpen }
        )

        // Floating Overlay Assistive Menu when clicked
        AnimatedVisibility(
            visible = isMenuOpen,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(0.85f)),
                modifier = Modifier
                    .size(190.dp)
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "AssistiveTouch",
                        color = Color.LightGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // Option 1: Lock Device
                        AssistiveItem(icon = Icons.Default.Lock, name = "Bloquear") {
                            viewModel.setLocked(true)
                            isMenuOpen = false
                        }
                        // Option 2: Go Home
                        AssistiveItem(icon = Icons.Default.Home, name = "Início") {
                            viewModel.openApp(null)
                            viewModel.setLocked(false)
                            isMenuOpen = false
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // Option 3: Settings
                        AssistiveItem(icon = Icons.Default.Settings, name = "Ajustes") {
                            viewModel.openApp("settings")
                            viewModel.setLocked(false)
                            isMenuOpen = false
                        }
                        // Option 4: Quick Close
                        AssistiveItem(icon = Icons.Default.Close, name = "Fechar") {
                            isMenuOpen = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AssistiveItem(icon: ImageVector, name: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color.White.copy(0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, name, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Text(name, fontSize = 9.sp, color = Color.LightGray, modifier = Modifier.padding(top = 2.dp))
    }
}
