package com.example

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IPhoneSimulatorViewModel : ViewModel() {

    // Wallpapers
    val wallpapers = listOf(
        WallpaperItem(
            0, "Aurora Clássica",
            Brush.linearGradient(
                listOf(Color(0xFFE55D87), Color(0xFF5FC3E4))
            ), false
        ),
        WallpaperItem(
            1, "Meia Noite Espacial",
            Brush.radialGradient(
                listOf(Color(0xFF1F1C2C), Color(0xFF928DAB))
            ), true
        ),
        WallpaperItem(
            2, "Neon Cyberpunk",
            Brush.linearGradient(
                listOf(Color(0xFF8A2387), Color(0xFFE94057), Color(0xFFF27121))
            ), true
        ),
        WallpaperItem(
            3, "Floresta Esmeralda",
            Brush.sweepGradient(
                listOf(Color(0xFF11998e), Color(0xFF38ef7d))
            ), false
        )
    )

    // Base System State
    private val _isLocked = MutableStateFlow(true)
    val isLocked = _isLocked.asStateFlow()

    private val _isPoweredOn = MutableStateFlow(true)
    val isPoweredOn = _isPoweredOn.asStateFlow()

    private val _currentOpenApp = MutableStateFlow<String?>(null) // null = Home Screen
    val currentOpenApp = _currentOpenApp.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _currentWallpaperIndex = MutableStateFlow(0)
    val currentWallpaperIndex = _currentWallpaperIndex.asStateFlow()

    private val _isAssistiveTouchEnabled = MutableStateFlow(true)
    val isAssistiveTouchEnabled = _isAssistiveTouchEnabled.asStateFlow()

    // AssistiveTouch coordinates relative standard screen dimensions
    private val _assistiveTouchX = MutableStateFlow(300f)
    val assistiveTouchX = _assistiveTouchX.asStateFlow()

    private val _assistiveTouchY = MutableStateFlow(500f)
    val assistiveTouchY = _assistiveTouchY.asStateFlow()

    // Username setup
    private val _userName = MutableStateFlow("Giovanna")
    val userName = _userName.asStateFlow()

    fun updateUserName(name: String) {
        _userName.value = name
    }

    fun setLocked(locked: Boolean) {
        _isLocked.value = locked
    }

    fun togglePower() {
        _isPoweredOn.value = !_isPoweredOn.value
        if (!_isPoweredOn.value) {
            _isLocked.value = true
            _currentOpenApp.value = null
        }
    }

    fun openApp(appId: String?) {
        _currentOpenApp.value = appId
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setWallpaperIndex(index: Int) {
        if (index in wallpapers.indices) {
            _currentWallpaperIndex.value = index
        }
    }

    fun toggleAssistiveTouch() {
        _isAssistiveTouchEnabled.value = !_isAssistiveTouchEnabled.value
    }

    fun updateAssistiveTouchPos(x: Float, y: Float) {
        _assistiveTouchX.value = x
        _assistiveTouchY.value = y
    }

    // Apps Grid & App Store Offer
    private val _appsList = MutableStateFlow<List<AppItem>>(emptyList())
    val appsList = _appsList.asStateFlow()

    init {
        // Initial Built-in apps and App Store offers
        _appsList.value = listOf(
            AppItem("store", "App Store", "🗺️", Color(0xFF007AFF)),
            AppItem("safari", "Safari", "🧭", Color(0xFF34A853)),
            AppItem("notes", "Notas", "📝", Color(0xFFFFCC00)),
            AppItem("calc", "Calculadora", "🧮", Color(0xFFFF9500)),
            AppItem("weather", "Tempo", "☀️", Color(0xFF5AC8FA)),
            AppItem("photos", "Fotos", "🎨", Color(0xFFFF2D55)),
            AppItem("settings", "Ajustes", "⚙️", Color(0xFF8E8E93)),
            AppItem("phone", "Telefone", "📞", Color(0xFF4CD964)),
            AppItem("messages", "Mensagens", "💬", Color(0xFF4CD964)),
            AppItem("music", "Música", "🎵", Color(0xFFFF2D55)),

            // App Store downloadable Apps (Initially Not Installed)
            AppItem("whatsapp", "WhatsApp", "🟢", Color(0xFF25D366), isAppStoreOffer = true, category = "Redes Sociais", description = "Mensagens e Chamadas Simples, Seguras e Gratuitas.", rating = 4.7, isInstalled = false),
            AppItem("instagram", "Instagram", "📸", Color(0xFFE1306C), isAppStoreOffer = true, category = "Redes Sociais", description = "Aproximando você das pessoas e das coisas que você ama.", rating = 4.7, isInstalled = false),
            AppItem("youtube", "YouTube", "📺", Color(0xFFFF0000), isAppStoreOffer = true, category = "Entretenimento", description = "Assista a vídeos, canais e playlists em alta qualidade.", rating = 4.8, isInstalled = false),
            AppItem("spotify", "Spotify", "🎧", Color(0xFF1DB954), isAppStoreOffer = true, category = "Música/Áudio", description = "Ouça suas músicas de graça e descubra bilhões de faixas.", rating = 4.8, isInstalled = false),
            AppItem("tiktok", "TikTok", "💃", Color(0xFF000000), isAppStoreOffer = true, category = "Redes Sociais", description = "Dê asas à sua criatividade com vídeos curtos divertidos.", rating = 4.6, isInstalled = false),
            AppItem("netflix", "Netflix", "🍿", Color(0xFFE50914), isAppStoreOffer = true, category = "Entretenimento", description = "Assista a séries de TV, filmes famosos e originais.", rating = 4.4, isInstalled = false)
        )
    }

    fun installApp(appId: String) {
        _appsList.value = _appsList.value.map { app ->
            if (app.id == appId) {
                if (!app.isInstalled && !app.isDownloading) {
                    // Trigger download background simulation
                    viewModelScope.launch {
                        _appsList.value = _appsList.value.map {
                            if (it.id == appId) it.copy(isDownloading = true, downloadProgress = 0.05f) else it
                        }
                        for (i in 1..20) {
                            delay(150)
                            _appsList.value = _appsList.value.map {
                                if (it.id == appId) it.copy(downloadProgress = i * 0.05f) else it
                            }
                        }
                        _appsList.value = _appsList.value.map {
                            if (it.id == appId) it.copy(isInstalled = true, isDownloading = false, downloadProgress = 1f) else it
                        }
                    }
                }
                app
            } else app
        }
    }

    fun uninstallApp(appId: String) {
        _appsList.value = _appsList.value.map { app ->
            if (app.id == appId && app.isAppStoreOffer) {
                app.copy(isInstalled = false, isDownloading = false, downloadProgress = 0f)
            } else app
        }
    }

    // --- APP 1: SAFARI ---
    private val _safariUrl = MutableStateFlow("google.com.br")
    val safariUrl = _safariUrl.asStateFlow()

    private val _safariSearchInput = MutableStateFlow("")
    val safariSearchInput = _safariSearchInput.asStateFlow()

    private val _safariWebPageType = MutableStateFlow("google") // google, apple, wikipedia, news, search_results
    val safariWebPageType = _safariWebPageType.asStateFlow()

    fun updateSafariSearchInput(query: String) {
        _safariSearchInput.value = query
    }

    fun searchSafari(query: String) {
        val cleanQuery = query.lowercase().trim()
        _safariSearchInput.value = query
        if (cleanQuery.contains("apple")) {
            _safariUrl.value = "apple.com"
            _safariWebPageType.value = "apple"
        } else if (cleanQuery.contains("wikipedia") || cleanQuery.contains("wiki")) {
            _safariUrl.value = "wikipedia.org/wiki/iPhone_13"
            _safariWebPageType.value = "wikipedia"
        } else if (cleanQuery.contains("g1") || cleanQuery.contains("noticia") || cleanQuery.contains("news")) {
            _safariUrl.value = "g1.globo.com"
            _safariWebPageType.value = "news"
        } else if (cleanQuery.startsWith("www.") || cleanQuery.contains(".com")) {
            _safariUrl.value = cleanQuery
            _safariWebPageType.value = "search_results"
        } else {
            _safariUrl.value = "google.com.br/search?q=${cleanQuery}"
            _safariWebPageType.value = "search_results"
        }
    }

    fun navigateSafariHome() {
        _safariUrl.value = "google.com.br"
        _safariSearchInput.value = ""
        _safariWebPageType.value = "google"
    }

    // --- APP 2: NOTES (NOTAS) ---
    private val _notes = MutableStateFlow<List<NoteItem>>(
        listOf(
            NoteItem(1, "Lista de Compras 🛒", "• Maçã\n• Banana 🍌\n• Leite sem lactose\n• Café expresso", "10:30"),
            NoteItem(2, "Ideias para Projetos 💡", "1. Fazer um app mock de iOS 15 para Android.\n2. Estudar Jetpack Compose e animações fluidas.\n3. Criar uma interface inovadora e limpa.", "Ontem"),
            NoteItem(3, "Lembrete Reunião", "Alinhamento importante às 15:00 de amanhã. Trazer os protótipos visuais.", "24 mai")
        )
    )
    val notes = _notes.asStateFlow()

    private val _editingNote = MutableStateFlow<NoteItem?>(null)
    val editingNote = _editingNote.asStateFlow()

    fun startNewNote() {
        val newNote = NoteItem(
            id = (0..10000).random(),
            title = "",
            content = "",
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        )
        _editingNote.value = newNote
    }

    fun selectNote(note: NoteItem) {
        _editingNote.value = note
    }

    fun updateEditingNote(title: String, content: String) {
        _editingNote.value = _editingNote.value?.copy(title = title, content = content)
    }

    fun saveEditingNote() {
        val note = _editingNote.value ?: return
        if (note.title.isBlank() && note.content.isBlank()) {
            _editingNote.value = null
            return
        }
        val validatedNote = note.copy(
            title = if (note.title.isBlank()) "Nota Sem Título" else note.title,
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        )

        val exists = _notes.value.any { it.id == validatedNote.id }
        if (exists) {
            _notes.value = _notes.value.map { if (it.id == validatedNote.id) validatedNote else it }
        } else {
            _notes.value = listOf(validatedNote) + _notes.value
        }
        _editingNote.value = null
    }

    fun deleteNote(id: Int) {
        _notes.value = _notes.value.filter { it.id != id }
        if (_editingNote.value?.id == id) {
            _editingNote.value = null
        }
    }

    // --- APP 3: CALCULATOR ---
    private val _calcDisplay = MutableStateFlow("0")
    val calcDisplay = _calcDisplay.asStateFlow()

    private var calcLeftVal: Double? = null
    private var calcOperator: String? = null
    private var isCalcResetOnInput = false

    fun onCalcButtonPress(btn: String) {
        val current = _calcDisplay.value
        when (btn) {
            "C" -> {
                _calcDisplay.value = "0"
                calcLeftVal = null
                calcOperator = null
                isCalcResetOnInput = false
            }
            "+/-" -> {
                if (current != "0") {
                    _calcDisplay.value = if (current.startsWith("-")) current.substring(1) else "-$current"
                }
            }
            "%" -> {
                val num = current.toDoubleOrNull() ?: 0.0
                _calcDisplay.value = (num / 100.0).toString()
            }
            "+", "-", "×", "÷" -> {
                calcLeftVal = current.toDoubleOrNull()
                calcOperator = btn
                isCalcResetOnInput = true
            }
            "=" -> {
                val left = calcLeftVal
                val op = calcOperator
                val right = current.toDoubleOrNull()
                if (left != null && op != null && right != null) {
                    val result = when (op) {
                        "+" -> left + right
                        "-" -> left - right
                        "×" -> left * right
                        "÷" -> if (right != 0.0) left / right else Double.NaN
                        else -> 0.0
                    }
                    _calcDisplay.value = formatCalcResult(result)
                    calcLeftVal = null
                    calcOperator = null
                    isCalcResetOnInput = true
                }
            }
            "." -> {
                if (isCalcResetOnInput || current == "0") {
                    _calcDisplay.value = "0."
                    isCalcResetOnInput = false
                } else if (!current.contains(".")) {
                    _calcDisplay.value = "$current."
                }
            }
            else -> { // A digits 0-9
                if (current == "0" || isCalcResetOnInput) {
                    _calcDisplay.value = btn
                    isCalcResetOnInput = false
                } else {
                    if (current.length < 9) {
                        _calcDisplay.value = current + btn
                    }
                }
            }
        }
    }

    private fun formatCalcResult(v: Double): String {
        if (v.isNaN()) return "Erro"
        if (v == v.toLong().toDouble()) return v.toLong().toString()
        val str = v.toString()
        return if (str.length > 9) str.substring(0, 9) else str
    }

    // --- APP 4: WEATHER ---
    val weatherData = listOf(
        WeatherInfo("São Paulo", 24, "Ensolarado", "☀️", listOf("Ter" to 25, "Qua" to 26, "Qui" to 22, "Sex" to 19, "Sab" to 23)),
        WeatherInfo("Rio de Janeiro", 28, "Céu Limpo", "☀️", listOf("Ter" to 29, "Qua" to 30, "Qui" to 27, "Sex" to 25, "Sab" to 29)),
        WeatherInfo("Lisboa", 18, "Parcialmente Nublado", "⛅", listOf("Ter" to 19, "Qua" to 18, "Qui" to 17, "Sex" to 20, "Sab" to 21)),
        WeatherInfo("Nova York", 12, "Chuva Leve", "🌧️", listOf("Ter" to 11, "Qua" to 10, "Qui" to 13, "Sex" to 15, "Sab" to 14)),
        WeatherInfo("Tóquio", 16, "Nublado", "☁️", listOf("Ter" to 15, "Qua" to 17, "Qui" to 18, "Sex" to 14, "Sab" to 16))
    )

    private val _selectedWeatherIndex = MutableStateFlow(0)
    val selectedWeatherIndex = _selectedWeatherIndex.asStateFlow()

    fun selectWeatherCity(index: Int) {
        if (index in weatherData.indices) {
            _selectedWeatherIndex.value = index
        }
    }

    // --- APP 5: PHOTOS (FOTOS / CAMERA) ---
    private val _photosList = MutableStateFlow<List<PhotoItem>>(
        listOf(
            PhotoItem(1, "🌅", "Rio de Janeiro", "20/05/2026"),
            PhotoItem(2, "🍕", "São Paulo (Pizzaria)", "21/05/2026"),
            PhotoItem(3, "🗽", "Nova York", "22/05/2026"),
            PhotoItem(4, "🐕", "Parque do Ibirapuera", "23/05/2026")
        )
    )
    val photosList = _photosList.asStateFlow()

    private val _cameraFacingFront = MutableStateFlow(true)
    val cameraFacingFront = _cameraFacingFront.asStateFlow()

    fun toggleCameraFacing() {
        _cameraFacingFront.value = !_cameraFacingFront.value
    }

    fun takePhotoSimulated() {
        val dateStr = SimpleDateFormat("dd/01/yyyy", Locale.getDefault()).format(Date())
        val place = if (_cameraFacingFront.value) "Selfie no Espelho 🤳" else "Vista da Câmera Traseira 📸"
        val emojiPool = if (_cameraFacingFront.value) {
            listOf("😎", "😜", "😇", "😍", "😸", "🤳")
        } else {
            listOf("🏞️", "🏕️", "🏙️", "🌉", "☕", "🐱", "🐶", "🌸")
        }
        val chosenEmoji = emojiPool.random()

        val newPhoto = PhotoItem(
            id = (100..99999).random(),
            emoji = chosenEmoji,
            location = place,
            time = dateStr
        )
        _photosList.value = listOf(newPhoto) + _photosList.value
    }

    // --- APP 6: MUSIC (MÚSICA) ---
    val playlist = listOf(
        SongItem(1, "Lo-Fi Coding Beats", "Lofi Beats Producer", "Coding Chill", 184, listOf("Relaxing chords...", "Ambient bird sounds...", "Soft drum snare starts...", "Deep focus bass kick...", "Programming flow unlocked...")),
        SongItem(2, "iOS Sunset Neon Wave", "Cupertino Outrun", "iOS 15 Vibes", 202, listOf("Synth intro beats...", "Bright retro digital waves...", "California highway sounds...", "Chill saxophone solo...", "Wave fades into static...")),
        SongItem(3, "A15 Bionic Silicon Synth", "Tech Pioneers", "Apple Retro Synth", 160, listOf("Electronic microchip clicks...", "8-bit high tempo soundscape...", "Bass modulation pulsing...", "Bionic arpeggiator solo...", "Frequency sweep end."))
    )

    private val _currentSongIndex = MutableStateFlow(0)
    val currentSongIndex = _currentSongIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _songProgress = MutableStateFlow(0)
    val songProgress = _songProgress.asStateFlow()

    private var songTimerJob: Job? = null

    fun togglePlayPauseSong() {
        _isPlaying.value = !_isPlaying.value
        if (_isPlaying.value) {
            startSongTimer()
        } else {
            songTimerJob?.cancel()
        }
    }

    fun nextSong() {
        val nextIdx = (_currentSongIndex.value + 1) % playlist.size
        _currentSongIndex.value = nextIdx
        _songProgress.value = 0
        if (_isPlaying.value) {
            startSongTimer()
        }
    }

    fun prevSong() {
        var prevIdx = _currentSongIndex.value - 1
        if (prevIdx < 0) prevIdx = playlist.size - 1
        _currentSongIndex.value = prevIdx
        _songProgress.value = 0
        if (_isPlaying.value) {
            startSongTimer()
        }
    }

    private fun startSongTimer() {
        songTimerJob?.cancel()
        songTimerJob = viewModelScope.launch {
            while (_isPlaying.value) {
                delay(1000)
                val currSong = playlist[_currentSongIndex.value]
                if (_songProgress.value < currSong.durationSeconds) {
                    _songProgress.value += 1
                } else {
                    nextSong()
                    break
                }
            }
        }
    }

    fun setSongProgressSeconds(sec: Int) {
        val maxDur = playlist[_currentSongIndex.value].durationSeconds
        _songProgress.value = sec.coerceIn(0, maxDur)
    }

    // --- APP 7: PHONE (TELEFONE) ---
    private val _phoneDialText = MutableStateFlow("")
    val phoneDialText = _phoneDialText.asStateFlow()

    private val _phoneActiveCallName = MutableStateFlow<String?>(null) // null = idle
    val phoneActiveCallName = _phoneActiveCallName.asStateFlow()

    private val _phoneCallDuration = MutableStateFlow(0)
    val phoneCallDuration = _phoneCallDuration.asStateFlow()

    private var phoneCallJob: Job? = null

    fun pressPhoneKey(key: String) {
        if (_phoneDialText.value.length < 15) {
            _phoneDialText.value += key
        }
    }

    fun clearPhoneKey() {
        val cur = _phoneDialText.value
        if (cur.isNotEmpty()) {
            _phoneDialText.value = cur.substring(0, cur.length - 1)
        }
    }

    fun startCall(numberOrName: String) {
        if (numberOrName.isBlank()) return
        _phoneActiveCallName.value = numberOrName
        _phoneCallDuration.value = 0
        phoneCallJob?.cancel()
        phoneCallJob = viewModelScope.launch {
            while (_phoneActiveCallName.value != null) {
                delay(1000)
                _phoneCallDuration.value += 1
            }
        }
    }

    fun endCall() {
        _phoneActiveCallName.value = null
        phoneCallJob?.cancel()
    }

    // --- APP 8: MESSAGES (MENSAGENS) ---
    private val _messagesChatsList = MutableStateFlow<List<ContactItem>>(emptyList())
    val messagesChatsList = _messagesChatsList.asStateFlow()

    private val _activeContactIndex = MutableStateFlow<Int?>(null)
    val activeContactIndex = _activeContactIndex.asStateFlow()

    private val _typedMessage = MutableStateFlow("")
    val typedMessage = _typedMessage.asStateFlow()

    init {
        // Initial setup of messaging chats
        _messagesChatsList.value = listOf(
            ContactItem(
                name = "Mãe ❤️",
                statusText = "Online",
                emoji = "👩",
                activeMessages = listOf(
                    MessageItem("Filha, você está usando o iPhone novinho?", false, "12:30"),
                    MessageItem("Sim mãe! Estou no simulador mexendo em tudo!", true, "12:31"),
                    MessageItem("Que lindo! Coisa boa. Lembrou de almoçar?", false, "12:32")
                ),
                automatedReplies = listOf(
                    "Que bom filha! Juízo, hein? Beijo. 🥰",
                    "Te amo! Se cuida. Já tomou água hoje? 🥤",
                    "Muito chique esse iPhone! Me ensina a mexer depois?",
                    "Deus te abençoe! Estou fazendo bolo de banana."
                )
            ),
            ContactItem(
                name = "Gabi (Faculdade) 🎓",
                statusText = "Visto por último hoje às 11:15",
                emoji = "🙋‍♀️",
                activeMessages = listOf(
                    MessageItem("Amiga! Mandou muito bem no aplicativo de UI", false, "Onte m"),
                    MessageItem("Obrigada Gabi! Treinei bastante", true, "Ontem")
                ),
                automatedReplies = listOf(
                    "Nossa, ficou muito igual ao original! Onde você comprou?",
                    "Bora estudar na biblioteca mais tarde?",
                    "Isso é muito top! Vou baixar agora também.",
                    "Arrasou mulheeer! 🚀✨"
                )
            ),
            ContactItem(
                name = "Chefe 💼",
                statusText = "Ausente",
                emoji = "👨‍💼",
                activeMessages = listOf(
                    MessageItem("Parabéns pelo progresso no simulador iOS.", false, "Sexta"),
                    MessageItem("Obrigado Chefe, focado em trazer o design perfeito!", true, "Sexta")
                ),
                automatedReplies = listOf(
                    "Aprovado. Excelente atenção aos detalhes.",
                    "Traga as atualizações da máquina virtual na segunda-feira.",
                    "A equipe de design ficou impressionada com o Notch do iPhone 13.",
                    "Bom trabalho. Continue engajado."
                )
            )
        )
    }

    fun selectChatContact(index: Int?) {
        _activeContactIndex.value = index
    }

    fun updateTypedMessage(text: String) {
        _typedMessage.value = text
    }

    fun sendChatMessage() {
        val text = _typedMessage.value.trim()
        val index = _activeContactIndex.value
        if (text.isBlank() || index == null) return

        val contact = _messagesChatsList.value[index]
        val timeNow = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        val userMsg = MessageItem(text, isUser = true, timestamp = timeNow)
        val updatedMessages = contact.activeMessages + userMsg

        _messagesChatsList.value = _messagesChatsList.value.mapIndexed { idx, cItem ->
            if (idx == index) cItem.copy(activeMessages = updatedMessages) else cItem
        }
        _typedMessage.value = ""

        // Trigger bot-response delay
        viewModelScope.launch {
            delay(1500)
            val randomReply = contact.automatedReplies.random()
            val botMsg = MessageItem(randomReply, isUser = false, timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
            _messagesChatsList.value = _messagesChatsList.value.mapIndexed { idx, cItem ->
                if (idx == index) cItem.copy(activeMessages = cItem.activeMessages + botMsg) else cItem
            }
        }
    }
}
