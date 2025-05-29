package com.fitgymtrack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitgymtrack.ui.theme.Indigo600
import com.fitgymtrack.utils.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Timer di recupero dopo una serie
 */
/**
 * Timer di recupero dopo una serie
 */
@Composable
fun RecoveryTimer(
    seconds: Int,
    isRunning: Boolean,
    onFinish: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Usiamo un valore ricordato per il conteggio alla rovescia, inizializzato una sola volta
    // al valore originale dei secondi, e poi modificato solo dal LaunchedEffect
    var timeLeft by remember(seconds) { mutableIntStateOf(seconds) }

    // Anche se il valore di isRunning cambia, non vogliamo resettare timeLeft
    // quindi lo gestiamo separatamente
    var timerActive by remember { mutableStateOf(isRunning) }

    // 🔊 Aggiungi per i suoni
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }
    val coroutineScope = rememberCoroutineScope()

    // Aggiorniamo lo stato del timer quando cambia isRunning
    LaunchedEffect(isRunning) {
        timerActive = isRunning
    }

    // Effetto per il countdown - questo è il cuore della soluzione
    LaunchedEffect(key1 = seconds, key2 = isRunning) {
        // Se il timer non è attivo, non fare nulla
        if (!isRunning) return@LaunchedEffect

        // Assicuriamoci che il valore iniziale sia corretto
        timeLeft = seconds

        // Ogni secondo esatto, decrementa timeLeft di 1
        while (timeLeft > 0 && timerActive) {
            // 🔊 BEEP PER GLI ULTIMI 3 SECONDI DEL RECUPERO
            if (timeLeft <= 3 && timeLeft > 0) {
                coroutineScope.launch {
                    soundManager.playWorkoutSound(SoundManager.WorkoutSound.COUNTDOWN_BEEP)
                }
            }

            // Attendi un secondo esatto prima di decrementare
            delay(1000)
            timeLeft -= 1
        }

        // Se il timer raggiunge 0, notifica il completamento
        if (timeLeft <= 0) {
            // 🔊 SUONO FINALE RECUPERO COMPLETATO
            coroutineScope.launch {
                soundManager.playWorkoutSound(SoundManager.WorkoutSound.REST_COMPLETE)
            }
            onFinish()
        }
    }

    // Formatta il tempo in formato mm:ss
    val formattedTime = remember(timeLeft) {
        val minutes = timeLeft / 60
        val remainingSeconds = timeLeft % 60
        String.format(Locale.getDefault(),"%02d:%02d", minutes, remainingSeconds)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        color = when {
            // 🎨 Cambia colore negli ultimi 3 secondi
            timerActive && timeLeft <= 3 -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            // 🎨 Icona colorata negli ultimi 3 secondi
                            if (timerActive && timeLeft <= 3)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else
                                Indigo600.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (timerActive && timeLeft <= 3)
                            MaterialTheme.colorScheme.primary
                        else
                            Indigo600
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = if (timerActive && timeLeft <= 3) "Recupero - QUASI FINITO!" else "Recupero",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (timerActive && timeLeft <= 3)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 20.sp,
                        color = if (timerActive && timeLeft <= 3)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Cambio nel pulsante: mostra "Ferma" o "Salta" in base allo stato del timer
            Button(
                onClick = {
                    timerActive = false // Ferma immediatamente il countdown
                    onStop()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (timerActive) {
                        if (timeLeft <= 3) MaterialTheme.colorScheme.primary else Indigo600
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    },
                    contentColor = if (timerActive) Color.White else MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    if (timerActive) {
                        if (timeLeft <= 3) "Salta" else "Ferma"
                    } else {
                        "Salta"
                    }
                )
            }
        }
    }
}

/**
 * Timer per esercizi isometrici
 */
@Composable
fun IsometricTimer(
    seconds: Int,
    seriesNumber: Int,
    modifier: Modifier = Modifier,
    onSeriesCompleted: () -> Unit = {}

) {
    var timeLeft by remember { mutableIntStateOf(seconds) }
    var timerRunning by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }
    val coroutineScope = rememberCoroutineScope()

    // Aggiorna timeLeft quando cambia seconds o seriesNumber
    LaunchedEffect(seconds, seriesNumber) {
        timeLeft = seconds
        isCompleted = false
        timerRunning = false
    }

    // Effetto per gestire il countdown
    LaunchedEffect(key1 = timerRunning) {
        if (timerRunning) {
            while (timeLeft > 0) {
                // 🔊 BEEP PER GLI ULTIMI 3 SECONDI
                if (timeLeft <= 3 && timeLeft > 0) {
                    coroutineScope.launch {
                        soundManager.playWorkoutSound(SoundManager.WorkoutSound.COUNTDOWN_BEEP)
                    }
                }

                delay(1000L)
                timeLeft--
            }
            timerRunning = false
            isCompleted = true

            // 🔊 SUONO FINALE PIÙ FORTE
            coroutineScope.launch {
                soundManager.playWorkoutSound(SoundManager.WorkoutSound.TIMER_COMPLETE)
            }

            // Completamento automatico della serie
            onSeriesCompleted()

            // Reset automatico dopo 3 secondi (più tempo per sentire il suono)
            delay(3000L)
            timeLeft = seconds
            isCompleted = false
        }
    }

    // Resto del codice UI rimane uguale...
    val formattedTime = remember(timeLeft) {
        val minutes = timeLeft / 60
        val seconds = timeLeft % 60
        String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = when {
            isCompleted -> MaterialTheme.colorScheme.primaryContainer
            timerRunning -> if (timeLeft <= 3) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Timer Isometrico",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formattedTime,
                style = MaterialTheme.typography.titleLarge,
                color = when {
                    isCompleted -> MaterialTheme.colorScheme.primary
                    timerRunning && timeLeft <= 3 -> MaterialTheme.colorScheme.error // Rosso per ultimi 3 secondi
                    timerRunning -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nota sul completamento automatico
            if (!isCompleted) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(
                            color = if (timerRunning && timeLeft <= 3)
                                MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                            else
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = if (timerRunning && timeLeft <= 3)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (timerRunning && timeLeft <= 3)
                            "COUNTDOWN FINALE!"
                        else
                            "Serie completata automaticamente a fine timer",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (timerRunning && timeLeft <= 3)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (!isCompleted) {
                        timerRunning = !timerRunning
                    }
                },
                enabled = !isCompleted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        isCompleted -> MaterialTheme.colorScheme.primary
                        timerRunning && timeLeft <= 3 -> MaterialTheme.colorScheme.error
                        timerRunning -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    }
                )
            ) {
                Text(
                    text = when {
                        isCompleted -> "Completato"
                        timerRunning -> "Pausa"
                        else -> "Avvia"
                    }
                )
            }
        }
    }
}

/**
 * Componente per visualizzare il progresso dell'allenamento
 */
@Composable
fun WorkoutProgressIndicator(
    activeExercises: Int,
    completedExercises: Int,
    totalExercises: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Esercizi attivi
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Attivi",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = "$activeExercises",
                    style = MaterialTheme.typography.titleMedium,
                    color = Indigo600
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            )

            // Esercizi completati
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Completati",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = "$completedExercises",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            )

            // Esercizi totali
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Totali",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = "$totalExercises",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            )

            // Progresso
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Progresso",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

/**
 * Dialog di conferma per uscire dall'allenamento
 */
@Composable
fun ExitWorkoutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Conferma uscita")
        },
        text = {
            Text(
                text = "Sei sicuro di voler uscire dall'allenamento in corso? Tutti i progressi non salvati andranno persi."
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Esci")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss
            ) {
                Text("Annulla")
            }
        }
    )
}

/**
 * Dialog di conferma per completare l'allenamento
 */
@Composable
fun CompleteWorkoutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    // Aggiungi il suono per allenamento completato
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Conferma completamento")
        },
        text = {
            Text(
                text = "Vuoi completare l'allenamento corrente? Potrai visualizzarlo nello storico allenamenti."
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    // 🔊 SUONO ALLENAMENTO COMPLETATO
                    coroutineScope.launch {
                        soundManager.playWorkoutSound(SoundManager.WorkoutSound.WORKOUT_COMPLETE)
                    }
                    onConfirm()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Completa")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss
            ) {
                Text("Annulla")
            }
        }
    )
}

/**
 * Chip per visualizzare un valore (peso o ripetizioni)
 */
@Composable
fun ValueChip(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}