package ru.hse.equeue.ui.queues

import me.dm7.barcodescanner.zbar.Result
import ru.hse.equeue.ui.util.ScannerActivity

class ActiveQueueScannerActivity: ScannerActivity() {
    override fun handleResult(result: Result?) {
        supportFragmentManager.beginTransaction()
            .add(android.R.id.content, NotActiveQueueFragment()).commit()
        finish()
    }
}