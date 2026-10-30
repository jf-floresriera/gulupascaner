package co.edu.unal.gulupascan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.edu.unal.gulupascan.AppViewModel
import co.edu.unal.gulupascan.BuildConfig
import co.edu.unal.gulupascan.R
import co.edu.unal.gulupascan.data.AppSettings
import co.edu.unal.gulupascan.data.UserProfile
import co.edu.unal.gulupascan.domain.Analysis
import co.edu.unal.gulupascan.domain.Classification
import co.edu.unal.gulupascan.domain.Severity
import co.edu.unal.gulupascan.ui.components.BrandHeader
import co.edu.unal.gulupascan.ui.components.ColorDot
import co.edu.unal.gulupascan.ui.components.GsCard
import co.edu.unal.gulupascan.ui.components.GsTopBar
import co.edu.unal.gulupascan.ui.components.GulupaLogo
import co.edu.unal.gulupascan.ui.components.InfoNote
import co.edu.unal.gulupascan.ui.components.PathImage
import co.edu.unal.gulupascan.ui.components.PrimaryButton
import co.edu.unal.gulupascan.ui.components.StepBadge
import co.edu.unal.gulupascan.ui.components.pct
import co.edu.unal.gulupascan.ui.components.shareContent
import co.edu.unal.gulupascan.ui.theme.Cream
import co.edu.unal.gulupascan.ui.theme.Green
import co.edu.unal.gulupascan.ui.theme.GreenDark
import co.edu.unal.gulupascan.ui.theme.GreenLight
import co.edu.unal.gulupascan.ui.theme.GreenMid
import co.edu.unal.gulupascan.ui.theme.TextSoft
import co.edu.unal.gulupascan.ui.theme.WarnBg
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

// ---------------------------------------------------------------------------
// Historial ("Mis reportes anteriores")
// ---------------------------------------------------------------------------
@Composable
fun HistoryScreen(vm: AppViewModel, items: List<Analysis>, onOpen: (Analysis) -> Unit, onBack: (() -> Unit)?) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var toDelete by remember { mutableStateOf<Analysis?>(null) }
    val csvTitle = stringResource(R.string.hist_csv_share)

    Scaffold(topBar = {
        GsTopBar(stringResource(R.string.hist_title), onBack, actions = {
            if (items.isNotEmpty()) IconButton(onClick = {
                scope.launch { shareContent(context, csvTitle, vm.exportCsv(), "text/csv") }
            }) { Icon(Icons.Outlined.Download, stringResource(R.string.hist_csv)) }
        })
    }) { pad ->
        if (items.isEmpty()) {
            Column(Modifier.padding(pad).fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Icon(Icons.Outlined.History, null, Modifier.size(64.dp), tint = TextSoft)
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.hist_empty), textAlign = TextAlign.Center, color = TextSoft)
            }
        } else {
            LazyColumn(Modifier.padding(pad).fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(items, key = { it.id }) { a ->
                    GsCard(Modifier.clickable { onOpen(a) }) {
                        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            PathImage(a.thumbnail, Modifier.size(64.dp).clip(RoundedCornerShape(10.dp)))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(stringResource(a.classification.title), style = MaterialTheme.typography.titleSmall, color = a.classification.color)
                                Text(
                                    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(a.createdAt)),
                                    style = MaterialTheme.typography.bodySmall, color = TextSoft,
                                )
                                Text(stringResource(R.string.hist_damage, pct(a.totalDamagePct)) + if (a.isDemo) " · DEMO" else "",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { toDelete = a }) { Icon(Icons.Outlined.Delete, stringResource(R.string.delete), tint = TextSoft) }
                        }
                    }
                }
            }
        }
    }

    toDelete?.let { a ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            confirmButton = { TextButton(onClick = { vm.delete(a); toDelete = null }) { Text(stringResource(R.string.delete)) } },
            dismissButton = { TextButton(onClick = { toDelete = null }) { Text(stringResource(R.string.cancel)) } },
            title = { Text(stringResource(R.string.hist_delete_q)) },
        )
    }
}

// ---------------------------------------------------------------------------
// Alertas y notificaciones
// ---------------------------------------------------------------------------
@Composable
fun AlertsScreen(items: List<Analysis>, onOpen: (Analysis) -> Unit, onBack: (() -> Unit)?) {
    val alerts = items.filter {
        it.classification == Classification.MODERATE || it.classification == Classification.SEVERE ||
            it.harmfulDamages.any { d -> Severity.level(d.pct) >= 6 }
    }
    Scaffold(topBar = { GsTopBar(stringResource(R.string.alerts_title), onBack) }) { pad ->
        LazyColumn(Modifier.padding(pad).fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                GsCard(color = GreenLight) {
                    Row(Modifier.padding(14.dp)) {
                        Icon(Icons.Outlined.TipsAndUpdates, null, tint = GreenDark)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(stringResource(R.string.alerts_tip_title), style = MaterialTheme.typography.titleSmall)
                            Text(stringResource(R.string.alerts_tip_body), style = MaterialTheme.typography.bodySmall, color = TextSoft)
                        }
                    }
                }
            }
            if (alerts.isEmpty()) item {
                Text(stringResource(R.string.alerts_empty), Modifier.fillMaxWidth().padding(24.dp), textAlign = TextAlign.Center, color = TextSoft)
            }
            items(alerts, key = { it.id }) { a ->
                GsCard(Modifier.clickable { onOpen(a) }, color = WarnBg) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.NotificationsActive, null, tint = a.classification.color)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            val top = a.harmfulDamages.firstOrNull()
                            Text(stringResource(a.classification.title), style = MaterialTheme.typography.titleSmall, color = a.classification.color)
                            if (top != null) Text(
                                stringResource(R.string.alerts_item, damageName(top.key), pct(top.pct), Severity.level(top.pct)),
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Text(DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(a.createdAt)),
                                style = MaterialTheme.typography.bodySmall, color = TextSoft)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextSoft)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Perfil de usuario (lámina 2, pantalla 2)
// ---------------------------------------------------------------------------
@Composable
fun ProfileScreen(
    profile: UserProfile,
    onHistory: () -> Unit,
    onAlerts: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    onLogout: () -> Unit,
) {
    val notSet = stringResource(R.string.not_set)
    Column(Modifier.fillMaxSize().background(Cream).verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth().height(120.dp).background(GreenMid))
            Box(
                Modifier.padding(top = 56.dp).size(128.dp).align(Alignment.TopCenter)
                    .clip(CircleShape).background(Cream).border(4.dp, Cream, CircleShape),
                contentAlignment = Alignment.Center,
            ) { GulupaLogo(size = 96.dp) }
        }
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(8.dp))
            Text(profile.name.ifBlank { notSet }, style = MaterialTheme.typography.titleLarge)
            Text(profile.role.ifBlank { stringResource(R.string.prof_role_default) }, style = MaterialTheme.typography.bodyMedium, color = TextSoft)
            Spacer(Modifier.height(16.dp))
            InfoRow(Icons.Outlined.LocationOn, stringResource(R.string.prof_location), profile.location.ifBlank { notSet })
            InfoRow(Icons.Outlined.Landscape, stringResource(R.string.prof_hectares), profile.hectares.ifBlank { notSet })
            InfoRow(Icons.Outlined.Phone, stringResource(R.string.prof_contact), profile.phone.ifBlank { notSet })
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            MenuRow(Icons.Outlined.History, stringResource(R.string.prof_reports), onHistory)
            MenuRow(Icons.Outlined.NotificationsActive, stringResource(R.string.prof_alerts), onAlerts)
            MenuRow(Icons.Outlined.Settings, stringResource(R.string.prof_settings), onSettings)
            MenuRow(Icons.Outlined.Groups, stringResource(R.string.prof_about), onAbout)
            MenuRow(Icons.AutoMirrored.Outlined.Logout, stringResource(R.string.prof_logout), onLogout, chevron = false)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = GreenDark, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text("$label: ", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun MenuRow(icon: ImageVector, text: String, onClick: () -> Unit, chevron: Boolean = true) {
    Column {
        Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = GreenDark)
            Spacer(Modifier.width(12.dp))
            Text(text, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            if (chevron) Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextSoft)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}

// ---------------------------------------------------------------------------
// Configuración de cuenta + servidor
// ---------------------------------------------------------------------------
@Composable
fun SettingsScreen(profile: UserProfile, settings: AppSettings, onSave: (UserProfile, AppSettings) -> Unit, onBack: () -> Unit) {
    var name by remember(profile) { mutableStateOf(profile.name) }
    var role by remember(profile) { mutableStateOf(profile.role) }
    var location by remember(profile) { mutableStateOf(profile.location) }
    var hectares by remember(profile) { mutableStateOf(profile.hectares) }
    var phone by remember(profile) { mutableStateOf(profile.phone) }
    var server by remember(settings) { mutableStateOf(settings.serverUrl) }
    var demo by remember(settings) { mutableStateOf(settings.demoMode) }

    Scaffold(topBar = { GsTopBar(stringResource(R.string.set_title), onBack) }) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.set_profile), style = MaterialTheme.typography.titleSmall, color = Green)
            Field(stringResource(R.string.auth_name), name) { name = it }
            Field(stringResource(R.string.set_role), role) { role = it }
            Field(stringResource(R.string.prof_location), location) { location = it }
            Field(stringResource(R.string.prof_hectares), hectares) { hectares = it }
            Field(stringResource(R.string.prof_contact), phone) { phone = it }

            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.set_analysis), style = MaterialTheme.typography.titleSmall, color = Green)
            Field(stringResource(R.string.set_server), server) { server = it }
            TextButton(onClick = { server = BuildConfig.DEFAULT_SERVER_URL }) {
                Text(stringResource(R.string.set_server_reset), color = GreenDark)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.set_demo), style = MaterialTheme.typography.bodyLarge)
                    Text(stringResource(R.string.set_demo_desc), style = MaterialTheme.typography.bodySmall, color = TextSoft)
                }
                Switch(demo, { demo = it }, colors = SwitchDefaults.colors(checkedTrackColor = GreenDark))
            }
            InfoNote(stringResource(R.string.set_server_note))
            Spacer(Modifier.height(8.dp))
            PrimaryButton(stringResource(R.string.save), {
                onSave(
                    profile.copy(name = name.trim(), role = role.trim(), location = location.trim(), hectares = hectares.trim(), phone = phone.trim()),
                    AppSettings(serverUrl = server.trim(), demoMode = demo),
                )
            })
        }
    }
}

@Composable
private fun Field(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(value, onChange, Modifier.fillMaxWidth(), label = { Text(label) }, singleLine = true, shape = RoundedCornerShape(10.dp))
}

// ---------------------------------------------------------------------------
// Acerca de nosotros (lámina 2, pantalla 3)
// ---------------------------------------------------------------------------
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(topBar = { GsTopBar(stringResource(R.string.about_title), onBack) }) { pad ->
        Column(Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BrandHeader(stringResource(R.string.about_commitment))

            Text(stringResource(R.string.about_mission_t), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.about_mission), style = MaterialTheme.typography.bodyMedium)

            Text(stringResource(R.string.about_team_t), style = MaterialTheme.typography.titleLarge)
            listOf(
                "Daniel Felipe Ahumada Hernández",
                "Diego Humberto Lavado González",
                "Jesús Enrique Flores Riera",
                "Luis Alberto Castillo Sanabria",
            ).forEach { member ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(34.dp).clip(CircleShape).background(GreenLight), contentAlignment = Alignment.Center) {
                        Text(member.split(" ").let { "${it[0][0]}${it.getOrNull(2)?.get(0) ?: ""}" }, color = GreenDark, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(member, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Text(stringResource(R.string.about_course), style = MaterialTheme.typography.bodySmall, color = TextSoft)

            Text(stringResource(R.string.about_how_t), style = MaterialTheme.typography.titleLarge)
            listOf(R.string.how1, R.string.how2, R.string.how3, R.string.how4, R.string.how5).forEachIndexed { i, res ->
                Row(verticalAlignment = Alignment.Top) {
                    StepBadge(i + 1, 26.dp)
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(res), Modifier.offset(y = 3.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }

            Text(stringResource(R.string.about_scope_t), style = MaterialTheme.typography.titleLarge)
            InfoNote(stringResource(R.string.about_scope))

            Text(stringResource(R.string.about_base), style = MaterialTheme.typography.bodySmall, color = TextSoft)
            Row(verticalAlignment = Alignment.CenterVertically) {
                ColorDot(Color(0xFF4CAF50), 8.dp); Spacer(Modifier.width(6.dp))
                Text("v${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodySmall, color = TextSoft)
            }
        }
    }
}
