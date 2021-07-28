/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.server.core.task.rest

import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.jtasks.common.core.model.domain.TaskStatus
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jtasks.common.core.model.domain.Task
import com.gridnine.jtasks.common.core.model.rest.ChangeTaskStatusRequest
import com.gridnine.jtasks.common.core.model.rest.ChangeTaskStatusResponse
import java.time.LocalDateTime

class ChangeTaskStatusRestHandler:RestHandler<ChangeTaskStatusRequest, ChangeTaskStatusResponse> {
    override fun service(request: ChangeTaskStatusRequest, ctx: RestOperationContext): ChangeTaskStatusResponse {
        val task = Storage.get().loadDocument(Task::class,request.taskUid, true)
        task!!.status = request.newStatus
        if(request.newStatus == TaskStatus.RESOLVED){
            task.resolved = LocalDateTime.now()
        } else {
            task.resolved = null
        }
        Storage.get().saveDocument(task, true, "change status")
        return ChangeTaskStatusResponse()
    }
}