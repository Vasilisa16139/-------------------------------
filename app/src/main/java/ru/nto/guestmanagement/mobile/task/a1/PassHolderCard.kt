package ru.nto.guestmanagement.mobile.task.a1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.nto.guestmanagement.mobile.R
import ru.nto.guestmanagement.mobile.model.PassDetails
import ru.nto.guestmanagement.mobile.ui.TestIds
import java.time.format.DateTimeFormatter

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

/**
 * A1. Карточка владельца: ФИО, организация, срок действия, лимит проходов.
 * Подробности и теги — в README.md и в техническом задании.
 */
@Composable
fun PassHolderCard(pass: PassDetails, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = pass.visitor.fullName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.testTag(TestIds.PASS_HOLDER),
            )

            val organization = pass.visitor.organization
            if (!organization.isNullOrBlank()) {
                Text(
                    text = organization,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag(TestIds.PASS_ORGANIZATION),
                )
            }

            Text(
                text = validPeriodText(pass),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag(TestIds.PASS_VALID_PERIOD),
            )

            Text(
                text = entriesLimitText(pass.maxEntries),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag(TestIds.PASS_ENTRIES_LIMIT),
            )
        }
    }
}

@Composable
private fun validPeriodText(pass: PassDetails): String {
    return if (pass.validFrom.toLocalDate() == pass.validTo.toLocalDate()) {
        stringResource(
            R.string.pass_valid_same_day,
            pass.validFrom.format(dateFormatter),
            pass.validFrom.format(timeFormatter),
            pass.validTo.format(timeFormatter),
        )
    } else {
        stringResource(
            R.string.pass_valid_range,
            pass.validFrom.format(dateTimeFormatter),
            pass.validTo.format(dateTimeFormatter),
        )
    }
}

@Composable
private fun entriesLimitText(maxEntries: Int): String {
    if (maxEntries == 1) return stringResource(R.string.pass_single_entry)
    return stringResource(entriesLimitResource(maxEntries), maxEntries)
}

/** Форма слова «проход» по правилам задания, независимо от локали устройства. */
private fun entriesLimitResource(count: Int): Int {
    val lastTwo = count % 100
    val last = count % 10
    return when {
        lastTwo in 11..14 -> R.string.pass_entries_limit_many
        last == 1 -> R.string.pass_entries_limit_one
        last in 2..4 -> R.string.pass_entries_limit_few
        else -> R.string.pass_entries_limit_many
    }
}
