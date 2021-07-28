/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.server.core.timer.rest

import com.gridnine.jasmine.common.core.model.L10nMessage
import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.storage.searchQuery
import com.gridnine.jasmine.common.core.utils.AuthUtils
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jtasks.common.core.model.domain.TimerRecord
import com.gridnine.jtasks.common.core.model.domain.TimerStatus
import com.gridnine.jtasks.common.core.model.domain.UserAccountIndex
import com.gridnine.jtasks.common.core.model.rest.StartTimerRecordRequest
import com.gridnine.jtasks.common.core.model.rest.StartTimerRecordResponse
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class StartTimerRecordRestHandler:RestHandler<StartTimerRecordRequest, StartTimerRecordResponse> {
    override fun service(request: StartTimerRecordRequest, ctx: RestOperationContext): StartTimerRecordResponse {
        val record = Storage.get().loadAsset(TimerRecord::class, request.timerUid)!!
        if(record.date != LocalDate.now()){
            throw Xeption.forEndUser(L10nMessage("можно выбрать только таймер за текущую дату"), )
        }
        val currentTask = findCurrentRunningTask()
        if(currentTask != null){
            if(currentTask.uid == request.timerUid){
                return createResponse(currentTask)
            }
            stopTask(currentTask)
        }
        record.lastStarted = LocalDateTime.now()
        record.status = TimerStatus.STARTED
        Storage.get().saveAsset(record, false)
        return createResponse(record)
    }

    private fun createResponse(record: TimerRecord): StartTimerRecordResponse {
        return StartTimerRecordResponse().also {
            it.committedTime = record.committedTime!!
            it.lastStarted = record.lastStarted
            it.taskKey =record.task.toString()
            it.taskTitle = record.taskName!!
            it.taskUid = record.task!!.uid
        }
    }



    companion object{
        fun stopTask(currentTask: TimerRecord) {
            currentTask.status = TimerStatus.STOPPED
            if(currentTask.lastStarted != null) {
                currentTask.committedTime =  currentTask.committedTime!!+ Duration.between (currentTask.lastStarted!!, LocalDateTime.now()).toSeconds().toInt()
                currentTask.lastStarted = null
            }
            Storage.get().saveAsset(currentTask, false)
        }
        fun findCurrentRunningTask():TimerRecord?{
            val query = searchQuery {
                where {
                    eq(TimerRecord.dateProperty, LocalDate.now())
                    eq(TimerRecord.statusProperty, TimerStatus.STARTED)
                    eq(TimerRecord.assigneeProperty, Storage.get().findUniqueDocumentReference(UserAccountIndex::class, UserAccountIndex.loginProperty, AuthUtils.getCurrentUser())!!)
                }
            }
            val records = Storage.get().searchAssets(TimerRecord::class, query, true)
            return if(records.isEmpty()) null else records.first()
        }
    }
}