/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.web.core.workspace

import com.gridnine.jasmine.web.core.ui.components.SimpleActionHandler
import com.gridnine.jtasks.common.core.model.rest.LogoutRequestJS
import com.gridnine.jtasks.web.core.JtasksRestClient
import kotlinx.browser.window

class LogoutActionHandler:SimpleActionHandler {
    override suspend fun invoke() {
        JtasksRestClient.jtasks_core_logout(LogoutRequestJS())
        window.location.href = "/login.html"
    }
}