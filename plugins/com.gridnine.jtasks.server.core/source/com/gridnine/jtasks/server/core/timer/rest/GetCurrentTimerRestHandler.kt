/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.server.core.timer.rest

import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jtasks.common.core.model.rest.GetCurrentTimerRequest
import com.gridnine.jtasks.common.core.model.rest.GetCurrentTimerResponse
import com.gridnine.jtasks.server.core.task.rest.StartTaskTimerRestHandler

class GetCurrentTimerRestHandler:RestHandler<GetCurrentTimerRequest, GetCurrentTimerResponse> {
    override fun service(request: GetCurrentTimerRequest, ctx: RestOperationContext): GetCurrentTimerResponse {
        val record = StartTaskTimerRestHandler.findCurrentRunningTask()?:return GetCurrentTimerResponse()
        return GetCurrentTimerResponse().also {
            it.committedTime = record.committedTime
            it.lastStarted = record.lastStarted
            it.taskKey = record.task.toString()
            it.taskTitle = record.taskName
            it.taskStatus = record.status
            it.taskUid = record.task!!.uid
        }
    }
}