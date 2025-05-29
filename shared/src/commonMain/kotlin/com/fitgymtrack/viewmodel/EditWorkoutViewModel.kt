package com.fitgymtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitgymtrack.api.ExerciseItem
import com.fitgymtrack.models.*
import com.fitgymtrack.repository.WorkoutRepository
import com.fitgymtrack.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditWorkoutViewModel(
    private val repository: WorkoutRepository = WorkoutRepository()
) : ViewModel() {

    // Stato del caricamento per il salvataggio
    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    // Stato del caricamento per il caricamento della scheda
    private val _workoutLoadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val workoutLoadingState: StateFlow<LoadingState> = _workoutLoadingState.asStateFlow()

    // Dati della scheda
    private val _workoutName = MutableStateFlow("")
    val workoutName = _workoutName.asStateFlow()

    private val _workoutDescription = MutableStateFlow("")
    val workoutDescription = _workoutDescription.asStateFlow()

    // Esercizi selezionati
    private val _selectedExercises = MutableStateFlow<List<WorkoutExercise>>(emptyList())
    val selectedExercises = _selectedExercises.asStateFlow()

    // Esercizi rimossi (per l'aggiornamento)
    private val _removedExercises = MutableStateFlow<List<WorkoutExerciseToRemove>>(emptyList())
    val removedExercises = _removedExercises.asStateFlow()

    // Esercizi disponibili
    private val _availableExercises = MutableStateFlow<List<ExerciseItem>>(emptyList())
    val availableExercises = _availableExercises.asStateFlow()

    // Stato del caricamento esercizi
    private val _exercisesLoadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val exercisesLoadingState: StateFlow<LoadingState> = _exercisesLoadingState.asStateFlow()

    /**
     * Carica i dati della scheda
     */
    fun loadWorkoutPlan(schedaId: Int, sessionManager: SessionManager) {
        _workoutLoadingState.value = LoadingState.Loading

        viewModelScope.launch {
            try {
                // Ottiene l'ID utente corrente dalla sessione
                val userId = sessionManager.getUserData().first()?.id
                if (userId == null || userId <= 0) {
                    _workoutLoadingState.value = LoadingState.Error("Utente non autenticato")
                    return@launch
                }

                // Carica le informazioni della scheda
                val plansResult = repository.getWorkoutPlans(userId)

                plansResult.fold(
                    onSuccess = { plans ->
                        val plan = plans.find { it.id == schedaId }
                        if (plan != null) {
                            _workoutName.value = plan.nome
                            _workoutDescription.value = plan.descrizione ?: ""

                            // Carica gli esercizi della scheda
                            val exercisesResult = repository.getWorkoutExercises(schedaId)

                            exercisesResult.fold(
                                onSuccess = { exercises ->
                                    // Assicuriamo che setType non sia mai null
                                    val safeExercises = exercises.map { exercise ->
                                        if (exercise.setType.isNullOrEmpty()) {
                                            exercise.safeCopy(setType = "normal") // MODIFICATO: usa safeCopy
                                        } else {
                                            exercise
                                        }
                                    }
                                    _selectedExercises.value = safeExercises
                                    _workoutLoadingState.value = LoadingState.Success
                                },
                                onFailure = { e ->
                                    _workoutLoadingState.value = LoadingState.Error(e.message ?: "Errore nel caricamento degli esercizi")
                                }
                            )
                        } else {
                            _workoutLoadingState.value = LoadingState.Error("Scheda non trovata")
                        }
                    },
                    onFailure = { e ->
                        _workoutLoadingState.value = LoadingState.Error(e.message ?: "Errore nel caricamento della scheda")
                    }
                )
            } catch (e: Exception) {
                _workoutLoadingState.value = LoadingState.Error(e.message ?: "Si è verificato un errore")
            }
        }
    }

    /**
     * Carica gli esercizi disponibili
     */
    fun loadAvailableExercises(userId: Int) {
        _exercisesLoadingState.value = LoadingState.Loading

        viewModelScope.launch {
            try {
                val result = repository.getAvailableExercises(userId)

                result.fold(
                    onSuccess = { exercises ->
                        _availableExercises.value = exercises
                        _exercisesLoadingState.value = LoadingState.Success
                    },
                    onFailure = { e ->
                        _exercisesLoadingState.value = LoadingState.Error(e.message ?: "Errore nel caricamento degli esercizi")
                    }
                )
            } catch (e: Exception) {
                _exercisesLoadingState.value = LoadingState.Error(e.message ?: "Si è verificato un errore")
            }
        }
    }

    /**
     * Aggiorna il nome della scheda
     */
    fun updateWorkoutName(name: String) {
        _workoutName.value = name
    }

    /**
     * Aggiorna la descrizione della scheda
     */
    fun updateWorkoutDescription(description: String) {
        _workoutDescription.value = description
    }

    /**
     * Aggiunge un esercizio alla scheda - MODIFICATO per usare createWorkoutExercise
     */
    fun addExercise(exercise: ExerciseItem) {
        val newExercise = createWorkoutExercise(
            id = exercise.id,
            nome = exercise.nome,
            gruppoMuscolare = exercise.gruppo_muscolare,
            attrezzatura = exercise.attrezzatura,
            descrizione = exercise.descrizione,
            serie = exercise.serie_default ?: 3,
            ripetizioni = exercise.ripetizioni_default ?: 10,
            peso = exercise.peso_default ?: 0.0,
            ordine = _selectedExercises.value.size + 1,
            isIsometric = exercise.is_isometric
        )

        _selectedExercises.value = _selectedExercises.value + newExercise
    }

    /**
     * Rimuove un esercizio dalla scheda
     * Se l'esercizio ha un ID scheda_esercizio_id, lo aggiunge agli esercizi da rimuovere
     * MODIFICATO per usare safeCopy
     */
    fun removeExercise(index: Int) {
        if (index < 0 || index >= _selectedExercises.value.size) return

        val exercise = _selectedExercises.value[index]

        // Se l'esercizio ha un ID scheda_esercizio_id, lo aggiungiamo agli esercizi da rimuovere
        if (exercise.schedaEsercizioId != null) {
            _removedExercises.value = _removedExercises.value + WorkoutExerciseToRemove(exercise.id)
        }

        // Rimuoviamo l'esercizio dalla lista
        val updatedList = _selectedExercises.value.toMutableList()
        updatedList.removeAt(index)

        // Aggiorniamo l'ordine degli esercizi
        _selectedExercises.value = updatedList.mapIndexed { i, ex ->
            ex.safeCopy(ordine = i + 1)
        }
    }

    /**
     * Aggiorna i dettagli di un esercizio
     */
    fun updateExerciseDetails(index: Int, updatedExercise: WorkoutExercise) {
        if (index < 0 || index >= _selectedExercises.value.size) return

        val updatedList = _selectedExercises.value.toMutableList()
        updatedList[index] = updatedExercise
        _selectedExercises.value = updatedList
    }

    /**
     * Sposta un esercizio su - MODIFICATO per usare safeCopy
     */
    fun moveExerciseUp(index: Int) {
        if (index <= 0 || index >= _selectedExercises.value.size) return

        val updatedList = _selectedExercises.value.toMutableList()

        // Prima di scambiare, controlliamo se l'esercizio corrente è collegato al precedente
        val currentEx = updatedList[index]

        // Se l'esercizio è collegato al precedente, rimuoviamo il collegamento
        if (currentEx.linkedToPrevious) {
            updatedList[index] = currentEx.safeCopy(linkedToPrevious = false)
        }

        // Scambiamo gli esercizi
        val temp = updatedList[index]
        updatedList[index] = updatedList[index - 1]
        updatedList[index - 1] = temp

        // Aggiorniamo l'ordine degli esercizi
        _selectedExercises.value = updatedList.mapIndexed { i, ex ->
            ex.safeCopy(ordine = i + 1)
        }
    }

    /**
     * Sposta un esercizio giù - MODIFICATO per usare safeCopy
     */
    fun moveExerciseDown(index: Int) {
        if (index < 0 || index >= _selectedExercises.value.size - 1) return

        val updatedList = _selectedExercises.value.toMutableList()

        // Controlliamo se l'esercizio successivo è collegato all'attuale
        val nextEx = updatedList[index + 1]

        // Se è collegato, rimuoviamo il collegamento
        if (nextEx.linkedToPrevious) {
            updatedList[index + 1] = nextEx.safeCopy(linkedToPrevious = false)
        }

        // Scambiamo gli esercizi
        val temp = updatedList[index]
        updatedList[index] = updatedList[index + 1]
        updatedList[index + 1] = temp

        // Aggiorniamo l'ordine degli esercizi
        _selectedExercises.value = updatedList.mapIndexed { i, ex ->
            ex.safeCopy(ordine = i + 1)
        }
    }

    /**
     * Aggiorna la scheda
     */
    fun updateWorkoutPlan(schedaId: Int) {
        if (_workoutName.value.isBlank()) {
            _loadingState.value = LoadingState.Error("Il nome della scheda è obbligatorio")
            return
        }

        if (_selectedExercises.value.isEmpty()) {
            _loadingState.value = LoadingState.Error("Aggiungi almeno un esercizio alla scheda")
            return
        }

        _loadingState.value = LoadingState.Loading

        // Converti gli esercizi nel formato richiesto per l'API
        val exerciseRequests = _selectedExercises.value.map { exercise ->
            WorkoutExerciseRequest(
                id = exercise.id,
                serie = exercise.serie,
                ripetizioni = exercise.ripetizioni,
                peso = exercise.peso,
                ordine = exercise.ordine,
                tempo_recupero = exercise.tempoRecupero,
                note = exercise.note,
                // CORREZIONE: Garantiamo che set_type non sia mai null
                set_type = exercise.setType.takeIf { !it.isNullOrEmpty() } ?: "normal",
                linked_to_previous = if (exercise.linkedToPrevious) 1 else 0
            )
        }

        val updateRequest = UpdateWorkoutPlanRequest(
            scheda_id = schedaId,
            nome = _workoutName.value,
            descrizione = _workoutDescription.value, // CORREZIONE: Invia sempre la stringa, anche se vuota
            esercizi = exerciseRequests,
            rimuovi = _removedExercises.value
        )

        viewModelScope.launch {
            try {
                val result = repository.updateWorkoutPlan(updateRequest)

                result.fold(
                    onSuccess = { response ->
                        if (response.success) {
                            _loadingState.value = LoadingState.Success
                        } else {
                            _loadingState.value = LoadingState.Error(response.message)
                        }
                    },
                    onFailure = { e ->
                        _loadingState.value = LoadingState.Error(e.message ?: "Errore durante il salvataggio")
                    }
                )
            } catch (e: Exception) {
                _loadingState.value = LoadingState.Error(e.message ?: "Si è verificato un errore")
            }
        }
    }
    /**
     * Resetta lo stato di caricamento
     */
    fun resetLoadingState() {
        _loadingState.value = LoadingState.Idle
    }

    // Stato di caricamento
    sealed class LoadingState {
        object Idle : LoadingState()
        object Loading : LoadingState()
        object Success : LoadingState()
        data class Error(val message: String) : LoadingState()
    }
}