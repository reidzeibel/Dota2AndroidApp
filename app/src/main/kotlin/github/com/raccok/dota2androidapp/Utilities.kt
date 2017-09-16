package github.com.raccok.dota2androidapp

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.widget.Toast

fun appIsMissingPermissions(context: Context) : Boolean {
  val prefix = "android.permission."
  if (!appHasPermission(context, prefix + "ACCESS_NETWORK_STATE")) {
    Toast.makeText(context,
                   "Application needs permission to access the current network state!",
                   Toast.LENGTH_LONG).show()
    return true
  }
  if (!appHasPermission(context, prefix + "INTERNET")) {
    Toast.makeText(context,
                   "Application needs permission to connect to the internet!",
                   Toast.LENGTH_LONG).show()
    return true
  }
  return false
}

private fun appHasPermission(context: Context, permission: String): Boolean {
  val res = ContextCompat.checkSelfPermission(context, permission)
  return res == PackageManager.PERMISSION_GRANTED
}
