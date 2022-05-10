package ru.hse.equeue.ui.profile

import android.widget.Toast
import me.dm7.barcodescanner.zbar.Result
import ru.hse.equeue.ui.profile.queue.ProfileQueueFragment
import ru.hse.equeue.ui.util.ScannerActivity

class ProfileScannerActivity : ScannerActivity() {

    override fun handleResult(res: Result?) {
        Toast.makeText(this, res?.contents.toString(), Toast.LENGTH_SHORT).show()
        supportFragmentManager.beginTransaction()
            .add(android.R.id.content, ProfileQueueFragment()).commit()
        finish()

    }
}