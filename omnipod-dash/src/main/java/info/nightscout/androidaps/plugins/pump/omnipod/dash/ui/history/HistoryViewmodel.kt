package info.nightscout.androidaps.plugins.pump.omnipod.dash.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import info.nightscout.androidaps.logging.AAPSLogger
import info.nightscout.androidaps.logging.LTag
import info.nightscout.androidaps.plugins.pump.omnipod.dash.history.DashHistory
import info.nightscout.androidaps.plugins.pump.omnipod.dash.history.data.HistoryRecord
import info.nightscout.androidaps.utils.rx.AapsSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import java.lang.System.currentTimeMillis
import javax.inject.Inject

// TODO: refactor out base VM that does the onCleared part.
//  Create Activity? or Fragment?
//  Create Dagger module and hook in. In case of Activity, use new module?

class HistoryViewmodel @Inject constructor(
    dashHistory: DashHistory,
    schedulers: AapsSchedulers,
    logger: AAPSLogger
) : ViewModel() {

    private val disposable = CompositeDisposable()
    private val _historyDataLiveData = MutableLiveData<List<HistoryRecord>>()
    val historyDataLiveData: LiveData<List<HistoryRecord>> = _historyDataLiveData

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    init {
        disposable += dashHistory
            .getRecordsAfter(currentTimeMillis() - LOOKBACK_WINDOW_MS)
            .subscribeOn(schedulers.io)
            .subscribeBy(
                onError = { logger.error(LTag.UI, "error fetching records", it) },
                onSuccess = _historyDataLiveData::postValue
            )
    }

    companion object {

        const val LOOKBACK_WINDOW_MS = 5 * 24 * 60 * 60 * 1000
    }
}