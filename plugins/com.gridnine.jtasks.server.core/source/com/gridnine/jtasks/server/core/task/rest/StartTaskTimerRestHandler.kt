/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.server.core.task.rest

import com.gridnine.jasmine.common.core.model.EntityUtils
import com.gridnine.jasmine.common.core.model.ObjectReference
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.storage.searchQuery
import com.gridnine.jasmine.common.core.utils.AuthUtils
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jtasks.common.core.model.domain.Task
import com.gridnine.jtasks.common.core.model.domain.TimerRecord
import com.gridnine.jtasks.common.core.model.domain.TimerStatus
import com.gridnine.jtasks.common.core.model.domain.UserAccountIndex
import com.gridnine.jtasks.common.core.model.rest.StartTaskTimerRequest
import com.gridnine.jtasks.common.core.model.rest.StartTaskTimerResponse
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class StartTaskTimerRestHandler:RestHandler<StartTaskTimerRequest, StartTaskTimerResponse> {
    override fun service(request: StartTaskTimerRequest, ctx: RestOperationContext): StartTaskTimerResponse {
        val currentTask = findCurrentRunningTask()
        if(currentTask != null){
            if(currentTask.task!!.uid == request.taskUid){
                return createResponse(currentTask)
            }
            stopTask(currentTask)
        }
        val currentUserRef = Storage.get().findUniqueDocumentReference(UserAccountIndex::class, UserAccountIndex.loginProperty, AuthUtils.getCurrentUser())!!
        val query = searchQuery {
            where {
                eq(TimerRecord.dateProperty, LocalDate.now())
                eq(TimerRecord.taskProperty, ObjectReference(Task::class, request.taskUid, null))
                eq(TimerRecord.assigneeProperty, currentUserRef)
            }
        }
        val record =  Storage.get().searchAssets(TimerRecord::class, query, true).let {lst ->
            if(lst.isNotEmpty()){
                lst.first()
            } else {
                TimerRecord().also {
                    it.assignee = currentUserRef
                    it.committedTime = 0
                    it.date = LocalDate.now()
                    it.status = TimerStatus.STOPPED
                    val task = Storage.get().loadDocument(Task::class, request.taskUid)!!
                    it.task = EntityUtils.toReference(task)
                    it.taskName = task.name
                    it.project = task.project
                }
            }
        }
        record.lastStarted = LocalDateTime.now()
        record.status = TimerStatus.STARTED
        Storage.get().saveAsset(record, false)
        return createResponse(record)
    }


    private fun createResponse(record: TimerRecord): StartTaskTimerResponse {
        return StartTaskTimerResponse().also {
            it.committedTime = record.committedTime!!
            it.lastStarted = record.lastStarted
            it.taskKey =record.task.toString()
            it.taskTitle = record.taskName!!
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