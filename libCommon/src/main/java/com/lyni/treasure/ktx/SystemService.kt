@file:Suppress("unused")

package com.lyni.treasure.ktx

import android.accessibilityservice.AccessibilityService
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.*
import android.app.admin.DevicePolicyManager
import android.app.job.JobScheduler
import android.app.role.RoleManager
import android.app.slice.SliceManager
import android.app.usage.NetworkStatsManager
import android.app.usage.StorageStatsManager
import android.app.usage.UsageStatsManager
import android.appwidget.AppWidgetManager
import android.bluetooth.BluetoothManager
import android.companion.CompanionDeviceManager
import android.content.ClipboardManager
import android.content.Context
import android.content.RestrictionsManager
import android.content.pm.CrossProfileApps
import android.content.pm.LauncherApps
import android.content.pm.ShortcutManager
import android.hardware.ConsumerIrManager
import android.hardware.SensorManager
import android.hardware.biometrics.BiometricManager
import android.hardware.camera2.CameraManager
import android.hardware.display.DisplayManager
import android.hardware.input.InputManager
import android.hardware.usb.UsbManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaRouter
import android.media.midi.MidiManager
import android.media.projection.MediaProjectionManager
import android.media.session.MediaSessionManager
import android.media.tv.TvInputManager
import android.net.ConnectivityManager
import android.net.IpSecManager
import android.net.nsd.NsdManager
import android.net.wifi.WifiManager
import android.net.wifi.aware.WifiAwareManager
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.rtt.WifiRttManager
import android.nfc.NfcManager
import android.os.*
import android.os.health.SystemHealthManager
import android.os.storage.StorageManager
import android.print.PrintManager
import android.telecom.TelecomManager
import android.telephony.CarrierConfigManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.telephony.euicc.EuiccManager
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.CaptioningManager
import android.view.autofill.AutofillManager
import android.view.contentcapture.ContentCaptureManager
import android.view.inputmethod.InputMethodManager
import android.view.textclassifier.TextClassificationManager
import android.view.textservice.TextServicesManager
import androidx.annotation.RequiresApi

/**
 * @date 2022/5/25
 * @author Liangyong Ni
 * description SystemService
 */
inline val AccessibilityService.windowManager get() = getSystemService(Context.WINDOW_SERVICE) as WindowManager
inline val Context.windowManager get() = getSystemService(Context.WINDOW_SERVICE) as WindowManager
inline val View.windowManager get() = context.windowManager
inline val windowManager: WindowManager get() = getSystemService(Context.WINDOW_SERVICE)
inline val Context.layoutInflater: LayoutInflater get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
inline val View.layoutInflater: LayoutInflater get() = context.layoutInflater
inline val activityManager: ActivityManager get() = getSystemService(Context.ACTIVITY_SERVICE)
inline val powerManager: PowerManager get() = getSystemService(Context.POWER_SERVICE)
inline val alarmManager: AlarmManager get() = getSystemService(Context.ALARM_SERVICE)
inline val notificationManager: NotificationManager get() = getSystemService(Context.NOTIFICATION_SERVICE)
inline val keyguardManager: KeyguardManager get() = getSystemService(Context.KEYGUARD_SERVICE)
inline val locationManager: LocationManager get() = getSystemService(Context.LOCATION_SERVICE)
inline val searchManager: SearchManager get() = getSystemService(Context.SEARCH_SERVICE)
inline val sensorManager: SensorManager get() = getSystemService(Context.SENSOR_SERVICE)
inline val storageManager: StorageManager get() = getSystemService(Context.STORAGE_SERVICE)

/** Null if invoked in an instant app. */
inline val wallpaperManager: WallpaperManager? get() = getSystemService(Context.WALLPAPER_SERVICE)
inline val vibrator: Vibrator get() = getSystemService(Context.VIBRATOR_SERVICE)
inline val connectivityManager: ConnectivityManager get() = getSystemService(Context.CONNECTIVITY_SERVICE)

/** Null if invoked in an instant app. */
inline val wifiManager: WifiManager?
    @SuppressLint("WifiManagerLeak") get() = getSystemService(Context.WIFI_SERVICE)

/** Null if invoked in an instant app. */
inline val wifiP2pManager: WifiP2pManager? get() = getSystemService(Context.WIFI_P2P_SERVICE)
inline val audioManager: AudioManager get() = getSystemService(Context.AUDIO_SERVICE)
inline val telephonyManager: TelephonyManager get() = getSystemService(Context.TELEPHONY_SERVICE)
inline val inputMethodManager: InputMethodManager get() = getSystemService(Context.INPUT_METHOD_SERVICE)
inline val downloadManager: DownloadManager get() = getSystemService(Context.DOWNLOAD_SERVICE)
inline val uiModeManager: UiModeManager get() = getSystemService(Context.UI_MODE_SERVICE)

/** Null if invoked in an instant app. */
inline val usbManager: UsbManager? get() = getSystemService(Context.USB_SERVICE)
inline val nfcManager: NfcManager get() = getSystemService(Context.NFC_SERVICE)

/** Null if invoked in an instant app. */
inline val devicePolicyManager: DevicePolicyManager? get() = getSystemService(Context.DEVICE_POLICY_SERVICE)
inline val textServicesManager: TextServicesManager
    get() = getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE)
inline val clipboardManager: ClipboardManager get() = getSystemService(Context.CLIPBOARD_SERVICE)
inline val accessibilityManager: AccessibilityManager get() = getSystemService(Context.ACCESSIBILITY_SERVICE)
inline val accountManager: AccountManager get() = getSystemService(Context.ACCOUNT_SERVICE)
inline val dropBoxManager: DropBoxManager get() = getSystemService(Context.DROPBOX_SERVICE)

inline val nsdManager: NsdManager get() = getSystemService(Context.NSD_SERVICE)
inline val mediaRouter: MediaRouter get() = getSystemService(Context.MEDIA_ROUTER_SERVICE)
inline val inputManager: InputManager get() = getSystemService(Context.INPUT_SERVICE)

inline val displayManager: DisplayManager get() = getSystemService(Context.DISPLAY_SERVICE)
inline val userManager: UserManager get() = getSystemService(Context.USER_SERVICE)

inline val bluetoothManager: BluetoothManager
    get() = getSystemService(Context.BLUETOOTH_SERVICE)

inline val appOpsManager: AppOpsManager get() = getSystemService(Context.APP_OPS_SERVICE)
inline val printManager: PrintManager get() = getSystemService(Context.PRINT_SERVICE)
inline val consumerIrManager: ConsumerIrManager
    get() = getSystemService(Context.CONSUMER_IR_SERVICE)
inline val captioningManager: CaptioningManager
    get() = getSystemService(Context.CAPTIONING_SERVICE)

inline val appWidgetManager: AppWidgetManager
    get() = getSystemService(Context.APPWIDGET_SERVICE)
inline val mediaSessionManager: MediaSessionManager
    get() = getSystemService(Context.MEDIA_SESSION_SERVICE)
inline val telecomManager: TelecomManager get() = getSystemService(Context.TELECOM_SERVICE)
inline val launcherApps: LauncherApps
    get() = getSystemService(Context.LAUNCHER_APPS_SERVICE)
inline val restrictionsManager: RestrictionsManager
    get() = getSystemService(Context.RESTRICTIONS_SERVICE)
inline val cameraManager: CameraManager get() = getSystemService(Context.CAMERA_SERVICE)
inline val tvInputManager: TvInputManager get() = getSystemService(Context.TV_INPUT_SERVICE)
inline val batteryManager: BatteryManager get() = getSystemService(Context.BATTERY_SERVICE)
inline val jobScheduler: JobScheduler
    get() = getSystemService(Context.JOB_SCHEDULER_SERVICE)
inline val mediaProjectionManager: MediaProjectionManager
    get() = getSystemService(Context.MEDIA_PROJECTION_SERVICE)

inline val usageStatsManager: UsageStatsManager
    get() = getSystemService(Context.USAGE_STATS_SERVICE)
inline val subscriptionManager: SubscriptionManager
    get() = getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE)

@Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
/** Null if invoked in an instant app. */
@Deprecated("Use android.hardware.biometrics.BiometricPrompt instead (back-ported into JetPack).")
inline val fingerPrintManager: android.hardware.fingerprint.FingerprintManager?
    get() = getSystemService(Context.FINGERPRINT_SERVICE)
inline val networkStatsManager: NetworkStatsManager
    get() = getSystemService(Context.NETWORK_STATS_SERVICE)
inline val carrierConfigManager: CarrierConfigManager
    get() = getSystemService(Context.CARRIER_CONFIG_SERVICE)
inline val midiManager: MidiManager get() = getSystemService(Context.MIDI_SERVICE)

inline val hardwarePropertiesManager: HardwarePropertiesManager
    @RequiresApi(24) get() = getSystemService(Context.HARDWARE_PROPERTIES_SERVICE)
inline val systemHealthManager: SystemHealthManager
    @RequiresApi(24) get() = getSystemService(Context.SYSTEM_HEALTH_SERVICE)

/** Null if invoked in an instant app. */
inline val shortcutManager: ShortcutManager?
    @RequiresApi(25) get() = getSystemService(Context.SHORTCUT_SERVICE)

inline val companionDeviceManager: CompanionDeviceManager
    @RequiresApi(26) get() = getSystemService(Context.COMPANION_DEVICE_SERVICE)
inline val storageStatsManager: StorageStatsManager
    @RequiresApi(26) get() = getSystemService(Context.STORAGE_STATS_SERVICE)
inline val textClassificationManager: TextClassificationManager
    @RequiresApi(26) get() = getSystemService(Context.TEXT_CLASSIFICATION_SERVICE)

/** Null if invoked in an instant app. */
inline val wifiAwareManager: WifiAwareManager?
    @RequiresApi(26) get() = getSystemService(Context.WIFI_AWARE_SERVICE)
inline val autofillManager: AutofillManager
    @RequiresApi(26) get() = appContext.getSystemService(AutofillManager::class.java)

inline val crossProfileApps: CrossProfileApps
    @RequiresApi(28) get() = getSystemService(Context.CROSS_PROFILE_APPS_SERVICE)
inline val euiccManager: EuiccManager @RequiresApi(28) get() = getSystemService(Context.EUICC_SERVICE)
inline val ipSecManager: IpSecManager @RequiresApi(28) get() = getSystemService(Context.IPSEC_SERVICE)
inline val wifiRttManager: WifiRttManager
    @RequiresApi(28) get() = getSystemService(Context.WIFI_RTT_RANGING_SERVICE)
inline val sliceManager: SliceManager
    @RequiresApi(28) get() = appContext.getSystemService(SliceManager::class.java)

inline val biometricManager: BiometricManager
    @RequiresApi(29) get() = getSystemService(Context.BIOMETRIC_SERVICE)

inline val roleManager: RoleManager @RequiresApi(29) get() = getSystemService(Context.ROLE_SERVICE)
inline val contentCaptureManager: ContentCaptureManager
    @RequiresApi(29) get() = appContext.getSystemService(ContentCaptureManager::class.java)

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <T> getSystemService(name: String) = appContext.getSystemService(name) as T