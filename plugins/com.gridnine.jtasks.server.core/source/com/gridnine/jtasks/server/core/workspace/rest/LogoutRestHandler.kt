/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/
package com.gridnine.jtasks.server.core.workspace.rest

import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jtasks.common.core.model.rest.LogoutRequest
import com.gridnine.jtasks.common.core.model.rest.LogoutResponse
import com.gridnine.jtasks.server.core.web.JTasksAuthFilter

class LogoutRestHandler:RestHandler<LogoutRequest,LogoutResponse> {
    override fun service(request: LogoutRequest, ctx: RestOperationContext): LogoutResponse {
        ctx.response.setHeader("Set-Cookie", "${JTasksAuthFilter.JTASKS_AUTH_COOKIE}=deleted; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT")
        return LogoutResponse()
    }
}