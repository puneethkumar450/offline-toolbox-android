package com.puneeth450.offlinetoolbox.app.feature.finance.expense

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

enum class TransactionType { INCOME, EXPENSE }

enum class TrackerTab { DASHBOARD, HISTORY, REPORTS }

enum class ExpenseCategory(val label: String) {
    ALL("All"),
    FOOD("Food"),
    TRANSPORT("Transport"),
    SHOPPING("Shopping"),
    BILLS("Bills"),
    HEALTH("Health"),
    ENTERTAINMENT("Entertainment"),
    EDUCATION("Education"),
    OTHERS("Others");

    companion object {
        val addableCategories: List<ExpenseCategory> = entries.filter { it != ALL }
    }
}

data class Transaction(
    val id: Long,
    val amount: Double,
    val type: TransactionType,
    val category: ExpenseCategory,
    val note: String,
    val date: LocalDate
)

data class ExpenseTrackerUiState(
    val transactions: List<Transaction> = emptyList(),
    val activeTab: TrackerTab = TrackerTab.DASHBOARD,
    val searchQuery: String = "",
    val selectedCategory: ExpenseCategory = ExpenseCategory.ALL,
    val showAddSheet: Boolean = false,
    // add sheet fields
    val addAmount: String = "",
    val addType: TransactionType = TransactionType.EXPENSE,
    val addCategory: ExpenseCategory = ExpenseCategory.FOOD,
    val addNote: String = ""
) {
    val today: LocalDate get() = LocalDate.now()
    val weekFields: WeekFields get() = WeekFields.of(Locale.getDefault())

    val totalIncome: Double get() = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalExpense: Double get() = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val totalBalance: Double get() = totalIncome - totalExpense

    val todayTotal: Double get() = transactions
        .filter { it.date == today }
        .sumOf { if (it.type == TransactionType.EXPENSE) it.amount else -it.amount }
        .coerceAtLeast(0.0)

    val thisWeekTotal: Double get() {
        val currentWeek = today.get(weekFields.weekOfWeekBasedYear())
        return transactions
            .filter { it.date.get(weekFields.weekOfWeekBasedYear()) == currentWeek && it.date.year == today.year }
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
    }

    val thisMonthTotal: Double get() = transactions
        .filter { it.date.month == today.month && it.date.year == today.year }
        .filter { it.type == TransactionType.EXPENSE }
        .sumOf { it.amount }

    val filteredTransactions: List<Transaction> get() {
        return transactions
            .filter { t ->
                (selectedCategory == ExpenseCategory.ALL || t.category == selectedCategory)
                    && (searchQuery.isBlank() || t.note.contains(searchQuery, ignoreCase = true)
                        || t.category.label.contains(searchQuery, ignoreCase = true))
            }
            .sortedByDescending { it.date }
    }

    val categorySpending: Map<ExpenseCategory, Double> get() = transactions
        .filter { it.type == TransactionType.EXPENSE && it.date.month == today.month && it.date.year == today.year }
        .groupBy { it.category }
        .mapValues { (_, txns) -> txns.sumOf { it.amount } }

    val canAddTransaction: Boolean get() = addAmount.toDoubleOrNull()?.let { it > 0 } == true
}

@HiltViewModel
class ExpenseTrackerViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseTrackerUiState())
    val uiState: StateFlow<ExpenseTrackerUiState> = _uiState

    private var nextId = 1L

    fun onTabChange(tab: TrackerTab) = _uiState.update { it.copy(activeTab = tab) }
    fun onSearchChange(q: String) = _uiState.update { it.copy(searchQuery = q) }
    fun onCategoryFilter(cat: ExpenseCategory) = _uiState.update { it.copy(selectedCategory = cat) }

    fun openAddSheet() = _uiState.update { it.copy(showAddSheet = true) }
    fun closeAddSheet() = _uiState.update {
        it.copy(
            showAddSheet = false,
            addAmount = "", addNote = "",
            addType = TransactionType.EXPENSE,
            addCategory = ExpenseCategory.FOOD
        )
    }

    fun onAddAmountChange(v: String) = _uiState.update { it.copy(addAmount = v) }
    fun onAddTypeChange(t: TransactionType) = _uiState.update { it.copy(addType = t) }
    fun onAddCategoryChange(c: ExpenseCategory) = _uiState.update { it.copy(addCategory = c) }
    fun onAddNoteChange(v: String) = _uiState.update { it.copy(addNote = v) }

    fun saveTransaction() {
        val s = _uiState.value
        val amount = s.addAmount.toDoubleOrNull() ?: return
        val txn = Transaction(
            id = nextId++,
            amount = amount,
            type = s.addType,
            category = s.addCategory,
            note = s.addNote,
            date = LocalDate.now()
        )
        _uiState.update { it.copy(transactions = it.transactions + txn) }
        closeAddSheet()
    }

    fun deleteTransaction(id: Long) = _uiState.update {
        it.copy(transactions = it.transactions.filter { t -> t.id != id })
    }
}
