/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.server.core.timer.rest

import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jtasks.common.core.model.domain.TimerRecord
import com.gridnine.jtasks.common.core.model.rest.StopTaskTimerRequest
import com.gridnine.jtasks.common.core.model.rest.StopTaskTimerResponse
import com.gridnine.jtasks.server.core.task.rest.StartTaskTimerRestHandler

class StopTaskTimerRestHandler:RestHandler<StopTaskTimerRequest,StopTaskTimerResponse> {
    override fun service(request: StopTaskTimerRequest, ctx: RestOperationContext): StopTaskTimerResponse {
        val currentTask = StartTaskTimerRestHandler.findCurrentRunningTask() ?: return StopTaskTimerResponse()
        if(currentTask.task!!.uid != request.taskUid){
            return createResponse(currentTask)
        }
        StartTaskTimerRestHandler.stopTask(currentTask)
        return createResponse(currentTask)
    }

    private fun createResponse(record: TimerRecord): StopTaskTimerResponse {
        return StopTaskTimerResponse().also {
            it.committedTime = record.committedTime!!
            it.lastStarted = record.lastStarted
            it.taskKey =record.task!!.toString()
            it.taskTitle = record.taskName!!
            it.taskStatus = record.status
            it.taskUid = record.task!!.uid
        }
    }
}