package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// iOS Universal Colors
val IosBlue = Color(0xFF007AFF)
val IosGreen = Color(0xFF34C759)
val IosNotesYellow = Color(0xFFFFCC00)
val IosGrayBg = Color(0xFFF2F2F7)
val IosDarkGrayBg = Color(0xFF1C1C1E)

@Composable
fun SimulatorAppContainer(
    appName: String,
    onBack: () -> Unit,
    primaryColor: Color = Color.White,
    isDark: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color.Black else Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Elegant top app header with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) IosDarkGrayBg else IosGrayBg)
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = if (isDark) Color.White else Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = appName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Divider(color = if (isDark) Color(0xFF38383A) else Color(0xFFD1D1D6), thickness = 0.5.dp)

            Box(
                modifier = Modifier
                    .fillSomeWeight()
                    .weight(1f)
            ) {
                content()
            }
        }
    }
}

// Helper extension to keep Compose happy
private fun Modifier.fillSomeWeight() = this.fillMaxSize()

// ==========================================
// 1. APP STORE VIEW
// ==========================================
@Composable
fun AppStoreView(viewModel: IPhoneSimulatorViewModel, onBack: () -> Unit) {
    val apps by viewModel.appsList.collectAsState()
    val downloadableApps = apps.filter { it.isAppStoreOffer }
    var searchQuery by remember { mutableStateOf("") }

    SimulatorAppContainer(appName = "App Store", onBack = onBack, isDark = true) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "HOJE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Text(
                    text = "Principais Apps",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                // Large Featured App Banner
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "NOVIDADE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF007AFF)
                        )
                        Text(
                            text = "A era dos Simuladores",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Com o iOS 15 você gerencia seus aplicativos favoritos em tempo real.",
                            fontSize = 14.sp,
                            color = Color.LightGray,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                // App Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar Apps...", color = Color.Gray) },
                    textColorText = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1C1C1E),
                        unfocusedContainerColor = Color(0xFF1C1C1E),
                        focusedBorderColor = IosBlue,
                        unfocusedBorderColor = Color(0xFF3A3A3C)
                    )
                )

                Text(
                    text = "Novos lançamentos recomendados",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            val filteredList = downloadableApps.filter {
                searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true)
            }

            items(filteredList) { app ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App Custom Styled Icon
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(app.iconBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(app.iconEmoji, fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${app.category} • ★ ${app.rating}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = app.description,
                            fontSize = 11.sp,
                            color = Color.LightGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Install / Get Dynamic Button
                    Box(contentAlignment = Alignment.Center) {
                        if (app.isDownloading) {
                            CircularProgressIndicator(
                                progress = app.downloadProgress,
                                modifier = Modifier.size(36.dp),
                                color = IosBlue,
                                strokeWidth = 3.dp
                            )
                            Text(
                                text = "${(app.downloadProgress * 100).toInt()}%",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        } else {
                            Button(
                                onClick = {
                                    if (!app.isInstalled) {
                                        viewModel.installApp(app.id)
                                    } else {
                                        viewModel.openApp(app.id)
                                    }
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (app.isInstalled) Color(0xFF3A3A3C) else Color(
                                        0xFF0A84FF
                                    )
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(
                                    text = if (app.isInstalled) "ABRIR" else "OBTER",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (app.isInstalled) IosBlue else Color.White
                                )
                            }
                        }
                    }
                }
                Divider(color = Color(0xFF2C2C2E), thickness = 0.5.dp)
            }
        }
    }
}

// Handle Custom Text styling to prevent compile issues on generic material/outlined text
@Composable
private fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: @Composable () -> Unit,
    textColorText: Color,
    modifier: Modifier,
    shape: androidx.compose.ui.graphics.Shape,
    colors: TextFieldColors
) {
    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier,
        shape = shape,
        colors = colors,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(color = textColorText)
    )
}

// ==========================================
// 2. SAFARI VIEW
// ==========================================
@Composable
fun SafariView(viewModel: IPhoneSimulatorViewModel, onBack: () -> Unit) {
    val searchInput by viewModel.safariSearchInput.collectAsState()
    val currentUrl by viewModel.safariUrl.collectAsState()
    val webPageType by viewModel.safariWebPageType.collectAsState()
    val contextKeyboard = LocalSoftwareKeyboardController.current

    SimulatorAppContainer(appName = "Safari", onBack = onBack, isDark = false) {
        Column(modifier = Modifier.fillMaxSize()) {
            // WEB PAGE CANVAS (SIMULATION)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                when (webPageType) {
                    "google" -> {
                        // GOOGLE HOME SCREEN
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Google",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            OutlinedTextField(
                                value = searchInput,
                                onValueChange = { viewModel.updateSafariSearchInput(it) },
                                placeholder = { Text("Pesquisar na Web...") },
                                textColorText = Color.Black,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF1F3F4),
                                    unfocusedContainerColor = Color(0xFFF1F3F4),
                                    focusedBorderColor = Color.LightGray,
                                    unfocusedBorderColor = Color.LightGray
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    viewModel.searchSafari(searchInput)
                                    contextKeyboard?.hide()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
                            ) {
                                Text("Pesquisar", color = Color.White)
                            }
                        }
                    }

                    "apple" -> {
                        // APPLE MOCK PAGE
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black)
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(" apple.com", color = Color.White, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "iPhone 13",
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            "Supercoloridamente incrível.",
                                            fontSize = 16.sp,
                                            color = Color.LightGray,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            "A15 Bionic • Novo sistema de câmera dupla",
                                            fontSize = 11.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "Destaques do Chip A15 Bionic",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        "Com desempenho gráfico até 50% superior a qualquer outro processador de smartphone, o iPhone 13 roda jogos avançados com extrema fluidez.",
                                        fontSize = 13.sp,
                                        color = Color.DarkGray,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                androidx.compose.ui.graphics.Brush.linearGradient(
                                                    listOf(
                                                        Color(0xFFE55D87),
                                                        Color(0xFF5FC3E4)
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "O ápice da inovação iOS",
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "wikipedia" -> {
                        // WIKIPEDIA MOCK PAGE
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Wikipedia",
                                        fontSize = 22.sp,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("A enciclopédia livre", fontSize = 11.sp, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "iPhone 13",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "O iPhone 13 é um smartphone lançado pela Apple Inc. Projetado e fabricado na Califórnia, foi revelado em 14 de setembro de 2021 ao lado de seus irmãos iPhone 13 Mini, Pro e Pro Max.",
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Especificações Principais:",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    "• Processador: Apple A15 Bionic (5nm)\n" +
                                            "• Tela: Super Retina XDR OLED com HDR10\n" +
                                            "• Sistema Operacional Original: iOS 15\n" +
                                            "• Notch: 20% menor que os antecessores",
                                    fontSize = 13.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    "news" -> {
                        // G1 NEWS PORTAL MOCK
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFC4170C))
                                        .padding(16.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text("g1.globo.com - Notícias", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                }
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("URGENTE • PRINCIPAIS NOTÍCIAS DE HOJE", color = Color(0xFFC4170C), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Lançado o novo simulador virtual de iPhone 13 no Android",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        "Inovação tecnológica permite simular aplicativos completos de iOS 15 como Notas, Music Beats e App Store em tempo real através de Jetpack Compose.",
                                        fontSize = 14.sp,
                                        color = Color.DarkGray,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )

                                    Divider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 12.dp))

                                    Text(
                                        "Clima em São Paulo sobe com sol brilhando em todo estado",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        // GENERAL GOOGLE SEARCH RESULTS
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            item {
                                Text("Resultados para: \"$searchInput\"", fontSize = 14.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(16.dp))

                                // Result 1
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("https://apple.com/br/iphone-13", fontSize = 10.sp, color = Color.DarkGray)
                                        Text("Comprar iPhone 13 - Apple (BR)", fontSize = 16.sp, color = IosBlue, fontWeight = FontWeight.Bold)
                                        Text("Descubra o design, bateria gigante, câmeras que gravam no modo cinema e o poderoso processador A15.", fontSize = 12.sp, color = Color.Black)
                                    }
                                }

                                // Result 2
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("https://pt.wikipedia.org > wiki", fontSize = 10.sp, color = Color.DarkGray)
                                        Text("iPhone 13 – Wikipédia, a enciclopédia livre", fontSize = 16.sp, color = IosBlue, fontWeight = FontWeight.Bold)
                                        Text("Consulte as fichas técnicas, datas históricas, variações, cores de lançamento do iPhone 13.", fontSize = 12.sp, color = Color.Black)
                                    }
                                }

                                // Alternative Notice
                                Text(
                                    "Dica: Tente pesquisar por 'apple', 'wikipedia' ou 'g1' para abrir as respectivas páginas diretamente na barra de navegação!",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 24.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ADDRESS BAR (iOS 15 introduced bottom address bar!)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFEFF4))
            ) {
                Divider(color = Color.LightGray, thickness = 0.5.dp)

                // Typing inputs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.OutlinedTextField(
                        value = searchInput,
                        onValueChange = { viewModel.updateSafariSearchInput(it) },
                        leadingIcon = { Icon(Icons.Default.Search, "Buscar", tint = Color.Gray, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchInput.isNotEmpty()) {
                                Icon(
                                    Icons.Default.Clear,
                                    "Limpar",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { viewModel.updateSafariSearchInput("") }
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            viewModel.searchSafari(searchInput)
                            contextKeyboard?.hide()
                        }),
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = Color.Black),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.LightGray,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Ir",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = IosBlue,
                        modifier = Modifier.clickable {
                            viewModel.searchSafari(searchInput)
                            contextKeyboard?.hide()
                        }
                    )
                }

                // Interactive Icons at the very bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp, top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = IosBlue,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { viewModel.navigateSafariHome() }
                    )
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = IosBlue,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { viewModel.navigateSafariHome() }
                    )
                    Text(
                        text = currentUrl.take(20) + (if (currentUrl.length > 20) "..." else ""),
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Recarregar",
                        tint = IosBlue,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { viewModel.searchSafari(searchInput) }
                    )
                }
            }
        }
    }
}

// ==========================================
// 3. APPLE NOTES VIEW (NOTAS)
// ==========================================
@Composable
fun NotesView(viewModel: IPhoneSimulatorViewModel, onBack: () -> Unit) {
    val notes by viewModel.notes.collectAsState()
    val editingNote by viewModel.editingNote.collectAsState()
    var searchNoteQuery by remember { mutableStateOf("") }

    SimulatorAppContainer(appName = "Notas", onBack = onBack, isDark = false) {
        if (editingNote != null) {
            val currNote = editingNote!!
            // Editing/Creating note screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Editando Nota",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                    Row {
                        Button(
                            onClick = { viewModel.saveEditingNote() },
                            colors = ButtonDefaults.buttonColors(containerColor = IosNotesYellow),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Salvar", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { viewModel.deleteNote(currNote.id) }) {
                            Icon(Icons.Default.Delete, "Excluir", tint = Color.Red)
                        }
                    }
                }

                androidx.compose.material3.TextField(
                    value = currNote.title,
                    onValueChange = { viewModel.updateEditingNote(it, currNote.content) },
                    placeholder = { Text("Título da Nota", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Divider(color = Color.LightGray, thickness = 0.5.dp)

                androidx.compose.material3.TextField(
                    value = currNote.content,
                    onValueChange = { viewModel.updateEditingNote(currNote.title, it) },
                    placeholder = { Text("Comece a escrever...") },
                    textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, color = Color.DarkGray),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        } else {
            // Main notes list view
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(IosGrayBg)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchNoteQuery,
                    onValueChange = { searchNoteQuery = it },
                    placeholder = { Text("Buscar notas...") },
                    textColorText = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE5E5EA),
                        unfocusedContainerColor = Color(0xFFE5E5EA),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pastas / iCloud",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    IconButton(onClick = { viewModel.startNewNote() }) {
                        Icon(Icons.Default.NoteAdd, "Nova Nota", tint = IosNotesYellow)
                    }
                }

                val filteredNotes = notes.filter {
                    searchNoteQuery.isEmpty() || it.title.contains(searchNoteQuery, ignoreCase = true) || it.content.contains(searchNoteQuery, ignoreCase = true)
                }

                if (filteredNotes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Note, "Sem Notas", tint = Color.LightGray, modifier = Modifier.size(48.dp))
                            Text("Nenhuma Nota Encontrada", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        LazyColumn {
                            itemsIndexed(filteredNotes) { idx, item ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.selectNote(item) }
                                        .padding(14.dp)
                                ) {
                                    Text(
                                        text = item.title,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Row(
                                        modifier = Modifier.padding(top = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.time,
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = item.content.take(40) + (if (item.content.length > 40) "..." else ""),
                                            fontSize = 13.sp,
                                            color = Color.DarkGray,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                if (idx < filteredNotes.size - 1) {
                                    Divider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 12.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. CALCULATOR VIEW (CALCULADORA)
// ==========================================
@Composable
fun CalculatorView(viewModel: IPhoneSimulatorViewModel, onBack: () -> Unit) {
    val displayStr by viewModel.calcDisplay.collectAsState()

    val buttons = listOf(
        listOf("C", "+/-", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "=")
    )

    SimulatorAppContainer(appName = "Calculadora", onBack = onBack, isDark = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            // DISPLAY
            Text(
                text = displayStr,
                fontSize = 55.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 60.sp,
                modifier = Modifier
                    .fillSomeWeight()
                    .padding(vertical = 12.dp)
            )

            // PADS
            buttons.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.forEach { char ->
                        val isNum = char.toIntOrNull() != null || char == "."
                        val isOp = char == "+" || char == "-" || char == "×" || char == "÷" || char == "="
                        val isSpecial = char == "C" || char == "+/-" || char == "%"

                        val bgColor = when {
                            isOp -> Color(0xFFFF9500)
                            isSpecial -> Color(0xFFA5A5A5)
                            else -> Color(0xFF333333)
                        }

                        val textColor = if (isSpecial) Color.Black else Color.White
                        val btnWidth = if (char == "0") 160.dp else 74.dp

                        Box(
                            modifier = Modifier
                                .size(width = btnWidth, height = 74.dp)
                                .clip(RoundedCornerShape(37.dp))
                                .background(bgColor)
                                .clickable { viewModel.onCalcButtonPress(char) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ==========================================
// 5. WEATHER VIEW (TEMPO)
// ==========================================
@Composable
fun WeatherView(viewModel: IPhoneSimulatorViewModel, onBack: () -> Unit) {
    val selIndex by viewModel.selectedWeatherIndex.collectAsState()
    val weather = viewModel.weatherData[selIndex]

    SimulatorAppContainer(appName = "Tempo", onBack = onBack, isDark = false) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(Color(0xFF5AC8FA), Color(0xFF007AFF))
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Select Cities Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                itemsIndexed(viewModel.weatherData) { idx, cityInfo ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (idx == selIndex) Color.White.copy(0.3f) else Color.Transparent)
                            .clickable { viewModel.selectWeatherCity(idx) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cityInfo.city,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = weather.city,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "${weather.temp}°",
                fontSize = 72.sp,
                fontWeight = FontWeight.ExtraLight,
                color = Color.White
            )

            Text(
                text = "${weather.condition} ${weather.emoji}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Forecast card container
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PREVISÃO PARA 5 DIAS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    weather.forecast.forEach { (day, temp) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = day,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.width(60.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "Sun",
                                tint = Color(0xFFFFCC00),
                                modifier = Modifier.size(20.dp)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${temp - 3}°",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(0.6f)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "${temp}°",
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. PHOTOS & MOCK CAMERA VIEW
// ==========================================
@Composable
fun PhotosView(viewModel: IPhoneSimulatorViewModel, onBack: () -> Unit) {
    val photos by viewModel.photosList.collectAsState()
    val isFrontCamera by viewModel.cameraFacingFront.collectAsState()
    var selectedPhotoDetail by remember { mutableStateOf<PhotoItem?>(null) }
    var activeTab by remember { mutableStateOf("gallery") } // gallery or camera

    SimulatorAppContainer(
        appName = if (activeTab == "camera") "Câmera Simulada" else "Fotos",
        onBack = onBack,
        isDark = true
    ) {
        if (selectedPhotoDetail != null) {
            val p = selectedPhotoDetail!!
            // Full Screen Photo View with option to close
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = p.location,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { selectedPhotoDetail = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                        ) {
                            Text("Fechar", color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(p.emoji, fontSize = 90.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tirada em: ${p.time}",
                        color = Color.LightGray,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // TABS HEADER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IosDarkGrayBg)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeTab = "gallery" }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Galeria (${photos.size})",
                            color = if (activeTab == "gallery") IosBlue else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { activeTab = "camera" }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Câmara 📷",
                            color = if (activeTab == "camera") IosBlue else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (activeTab == "camera") {
                    // MOCK CAMERA VIEWPORT
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Visor do iPhone 13",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        // VIEWPORT CONTAINER
                        Box(
                            modifier = Modifier
                                .size(220.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF2C2C2E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isFrontCamera) "🤳" else "🏞️",
                                    fontSize = 72.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (isFrontCamera) "Câmera Frontal Activa" else "Câmera Traseira Activa",
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // SHUTTER KEY CONTROLS
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.toggleCameraFacing() },
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color.DarkGray, CircleShape)
                            ) {
                                Icon(Icons.Default.FlipCameraAndroid, "Rotacionar", tint = Color.White)
                            }

                            // Capture key
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .clickable {
                                        viewModel.takePhotoSimulated()
                                        activeTab = "gallery"
                                    }
                                    .padding(4.dp)
                                    .background(Color.Black, CircleShape)
                                    .padding(4.dp)
                                    .background(Color.White, CircleShape)
                            )

                            // Thumbnail preview of last photo
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.DarkGray)
                                    .clickable { activeTab = "gallery" },
                                contentAlignment = Alignment.Center
                            ) {
                                if (photos.isNotEmpty()) {
                                    Text(photos.first().emoji, fontSize = 24.sp)
                                }
                            }
                        }
                    }
                } else {
                    // GRID OF PHOTOS
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(photos) { photo ->
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1C1C1E))
                                    .clickable { selectedPhotoDetail = photo },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(photo.emoji, fontSize = 42.sp)
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .background(Color.Black.copy(0.6f))
                                        .padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = photo.location,
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. SYSTEM SETTINGS VIEW (AJUSTES)
// ==========================================
@Composable
fun SettingsView(viewModel: IPhoneSimulatorViewModel, onBack: () -> Unit) {
    val userName by viewModel.userName.collectAsState()
    val curWallIdx by viewModel.currentWallpaperIndex.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isAssistiveTouch by viewModel.isAssistiveTouchEnabled.collectAsState()

    var editingNameState by remember { mutableStateOf(false) }
    var tempNameInput by remember { mutableStateOf(userName) }

    SimulatorAppContainer(appName = "Ajustes", onBack = onBack, isDark = isDarkMode) {
        val uiBg = if (isDarkMode) Color.Black else IosGrayBg
        val itemBg = if (isDarkMode) Color(0xFF1C1C1E) else Color.White
        val txtColor = if (isDarkMode) Color.White else Color.Black

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(uiBg)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Ajustes",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = txtColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // APPLE ID CARD
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = itemBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(IosBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(userName.take(1).uppercase(), fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            if (editingNameState) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    androidx.compose.material3.OutlinedTextField(
                                        value = tempNameInput,
                                        onValueChange = { tempNameInput = it },
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = txtColor),
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        singleLine = true
                                    )
                                    IconButton(onClick = {
                                        viewModel.updateUserName(tempNameInput)
                                        editingNameState = false
                                    }) {
                                        Icon(Icons.Default.Check, "Done", tint = IosGreen)
                                    }
                                }
                            } else {
                                Text(
                                    text = userName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = txtColor
                                )
                                Text(
                                    text = "Apple ID, iCloud, Mídia e Compras",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        if (!editingNameState) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = IosBlue,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable {
                                        tempNameInput = userName
                                        editingNameState = true
                                    }
                            )
                        }
                    }
                }

                Text(
                    "TELA & VISUAL",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // THEME & DISPLAY CARD
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = itemBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DarkMode, "Dark", tint = IosBlue)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Aparência Escura", color = txtColor, fontWeight = FontWeight.SemiBold)
                            }
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { viewModel.toggleDarkMode() },
                                colors = SwitchDefaults.colors(checkedThumbColor = IosGreen)
                            )
                        }

                        Divider(color = Color.LightGray.copy(0.3f), modifier = Modifier.padding(vertical = 12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TouchApp, "AssistiveTouch", tint = IosNotesYellow)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("AssistiveTouch", color = txtColor, fontWeight = FontWeight.SemiBold)
                            }
                            Switch(
                                checked = isAssistiveTouch,
                                onCheckedChange = { viewModel.toggleAssistiveTouch() },
                                colors = SwitchDefaults.colors(checkedThumbColor = IosGreen)
                            )
                        }
                    }
                }

                Text(
                    "SELEÇÃO DE WALLPAPER",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // WALLPAPER THUMBNAILS CAROUSEL
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    viewModel.wallpapers.forEachIndexed { index, wp ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { viewModel.setWallpaperIndex(index) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 64.dp, height = 90.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(wp.gradient)
                                    .background(
                                        if (index == curWallIdx) Color.White.copy(0.2f) else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (index == curWallIdx) {
                                    Icon(Icons.Default.CheckCircle, "Active", tint = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = wp.name.take(10),
                                fontSize = 9.sp,
                                color = txtColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Text(
                    "SOBRE O IPHONE 13",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // DEVICE INFO CARD
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = itemBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        DeviceInfoRow("Nome do Dispositivo", "iPhone 13", txtColor)
                        DeviceInfoRow("Versão do iOS", "15.4", txtColor)
                        DeviceInfoRow("Modelo", "MLPF3BR/A", txtColor)
                        DeviceInfoRow("Capacidade útil", "256 GB", txtColor)
                        DeviceInfoRow("Estatísticas da Bateria", "Saúde 100%", txtColor)
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceInfoRow(label: String, valText: String, textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(valText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}

// ==========================================
// 8. TELEFONE VIEW (PHONE KEYPAD)
// ==========================================
@Composable
fun PhoneView(viewModel: IPhoneSimulatorViewModel, onBack: () -> Unit) {
    val dialText by viewModel.phoneDialText.collectAsState()
    val activeCallName by viewModel.phoneActiveCallName.collectAsState()
    val callDuration by viewModel.phoneCallDuration.collectAsState()

    SimulatorAppContainer(appName = "Telefone", onBack = onBack, isDark = true) {
        if (activeCallName != null) {
            // CALL SCREEN ACTIVE
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1C1C1E))
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color.DarkGray, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(activeCallName!!.take(1).uppercase(), fontSize = 36.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = activeCallName!!,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = String.format("%02d:%02d", callDuration / 60, callDuration % 60),
                        fontSize = 16.sp,
                        color = Color.LightGray
                    )
                }

                // Grid actions during call simulation
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(140.dp)
                ) {
                    val listKeys = listOf(
                        Icons.Default.MicOff to "Mudo",
                        Icons.Default.Dialpad to "Teclado",
                        Icons.Default.VolumeUp to "Alto-Falante",
                        Icons.Default.Add to "Add Chamada",
                        Icons.Default.VideoCall to "FaceTime",
                        Icons.Default.ContactPage to "Contatos"
                    )
                    items(listKeys) { (icon, name) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(Color.White.copy(0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, name, tint = Color.White)
                            }
                            Text(name, fontSize = 9.sp, color = Color.LightGray, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }

                // End call button
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .clickable { viewModel.endCall() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CallEnd, "Sair", tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        } else {
            // PHONE DIALPAD KEYBOARD
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dialText,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        if (dialText.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearPhoneKey() }) {
                                Icon(Icons.Default.Delete, "clear", tint = Color.Gray)
                            }
                        }
                    }
                    Text("Adicionar Número", fontSize = 12.sp, color = IosBlue, modifier = Modifier.clickable {
                        viewModel.startCall(dialText)
                    })
                }

                val phoneKeys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("*", "0", "#")
                )

                Column {
                    phoneKeys.forEach { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { digit ->
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF232323))
                                        .clickable { viewModel.pressPhoneKey(digit) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = digit,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Green dial button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(IosGreen)
                                .clickable {
                                    if (dialText.isNotEmpty()) {
                                        viewModel.startCall(dialText)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Call, "Dial", tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ==========================================
// 9. MESSAGES VIEW (IMESSAGE MOCK CHATS)
// ==========================================
@Composable
fun MessagesView(viewModel: IPhoneSimulatorViewModel, onBack: () -> Unit) {
    val chatsList by viewModel.messagesChatsList.collectAsState()
    val activeIdx by viewModel.activeContactIndex.collectAsState()
    val typedText by viewModel.typedMessage.collectAsState()

    SimulatorAppContainer(appName = "Mensagens", onBack = onBack, isDark = false) {
        if (activeIdx != null) {
            val contact = chatsList[activeIdx!!]
            // Conversation Screen with Chat bubbles
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // Sender details bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IosGrayBg)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.selectChatContact(null) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "voltar", tint = IosBlue)
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(IosBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(contact.emoji, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(contact.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(contact.statusText, fontSize = 10.sp, color = Color.Gray)
                    }
                }

                // Chat Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(contact.activeMessages) { msg ->
                        val alignLeft = !msg.isUser
                        val cardColor = if (msg.isUser) IosBlue else Color(0xFFE5E5EA)
                        val txtColor = if (msg.isUser) Color.White else Color.Black

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = if (alignLeft) Arrangement.Start else Arrangement.End
                        ) {
                            Column(
                                horizontalAlignment = if (alignLeft) Alignment.Start else Alignment.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomStart = if (alignLeft) 0.dp else 16.dp,
                                                bottomEnd = if (alignLeft) 16.dp else 0.dp
                                            )
                                        )
                                        .background(cardColor)
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = msg.text,
                                        fontSize = 14.sp,
                                        color = txtColor
                                    )
                                }
                                Text(
                                    text = msg.timestamp,
                                    fontSize = 9.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Keyboard Typing input box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IosGrayBg)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.OutlinedTextField(
                        value = typedText,
                        onValueChange = { viewModel.updateTypedMessage(it) },
                        placeholder = { Text("Mensagem iMessage") },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedBorderColor = Color.LightGray,
                                        unfocusedBorderColor = Color.LightGray
                                    ),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = Color.Black)
                                )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { viewModel.sendChatMessage() },
                        modifier = Modifier
                            .size(36.dp)
                            .background(IosBlue, CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowUpward, "send", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        } else {
            // LIST OF CONVERSATIONS
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    item {
                        Text(
                            text = "iMessage",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    itemsIndexed(chatsList) { idx, chat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectChatContact(idx) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(Color(0xFFE5E5EA), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(chat.emoji, fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = chat.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                val lastMsg = chat.activeMessages.lastOrNull()?.text ?: ""
                                Text(
                                    text = lastMsg,
                                    fontSize = 13.sp,
                                    color = Color.DarkGray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Icon(Icons.Default.ChevronRight, "ir", tint = Color.LightGray)
                        }
                        if (idx < chatsList.size - 1) {
                            Divider(color = Color.LightGray.copy(0.4f), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 10. MUSIC PLAYER VIEW (APPLE MUSIC CHILL)
// ==========================================
@Composable
fun MusicView(viewModel: IPhoneSimulatorViewModel, onBack: () -> Unit) {
    val listSongs = viewModel.playlist
    val currIdx by viewModel.currentSongIndex.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentSong = listSongs[currIdx]
    val progressSec by viewModel.songProgress.collectAsState()

    SimulatorAppContainer(appName = "Música", onBack = onBack, isDark = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(Color(0xFFFF2D55), Color(0xFF1C1B1F))
                    )
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = " MUSIC CHILL OUT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(0.8f)
            )

            // Large album spinning visual frame icon
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💿", fontSize = 80.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(currentSong.album, fontSize = 11.sp, color = Color.White.copy(0.7f))
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentSong.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = currentSong.artist,
                    fontSize = 15.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )
            }

            // Timeline slider
            Column(modifier = Modifier.fillMaxWidth()) {
                val remSeconds = currentSong.durationSeconds - progressSec
                Slider(
                    value = progressSec.toFloat(),
                    onValueChange = { viewModel.setSongProgressSeconds(it.toInt()) },
                    valueRange = 0f..currentSong.durationSeconds.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        String.format("%02d:%02d", progressSec / 60, progressSec % 60),
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                    Text(
                        String.format("-%02d:%02d", remSeconds / 60, remSeconds % 60),
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }
            }

            // Controller Playback keys
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.prevSong() }) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        "anterior",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.togglePlayPauseSong() },
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        "playPause",
                        tint = Color.Red,
                        modifier = Modifier.size(36.dp)
                    )
                }

                IconButton(onClick = { viewModel.nextSong() }) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        "proxima",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Lyrics block preview
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(0.4f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val lyrIndex = (progressSec / 10) % currentSong.lyrics.size
                    Text(
                        "Letras em Sincronia 🎧",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(0.6f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        currentSong.lyrics[lyrIndex],
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
