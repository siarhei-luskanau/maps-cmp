package template.ui.search

sealed interface DepartureMode {
    data object Loading : DepartureMode

    data object CurrentLocation : DepartureMode

    data object ManualEntry : DepartureMode
}
