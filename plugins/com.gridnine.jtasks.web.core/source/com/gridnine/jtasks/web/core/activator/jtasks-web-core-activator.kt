/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.web.core.activator

import com.gridnine.jasmine.common.standard.model.rest.GetWorkspaceRequestJS
import com.gridnine.jasmine.web.core.common.ActivatorJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.remote.WebCoreMetaRegistriesUpdater
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.SimpleActionHandler
import com.gridnine.jasmine.web.core.ui.components.WebTabsContainerTool
import com.gridnine.jasmine.web.standard.ActionsIds
import com.gridnine.jasmine.web.standard.StandardRestClient
import com.gridnine.jasmine.web.standard.mainframe.ActionWrapper
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.mainframe.WebActionsHandler
import com.gridnine.jtasks.common.core.model.ui.UserAccountEditor
import com.gridnine.jtasks.web.core.DomainReflectionUtilsJS
import com.gridnine.jtasks.web.core.RestReflectionUtilsJS
import com.gridnine.jtasks.web.core.UiReflectionUtilsJS
import com.gridnine.jtasks.web.core.userAccount.UserAccountEditorHandler
import com.gridnine.jtasks.web.core.workspace.JTasksMainFrame
import kotlinx.browser.window

const val pluginId = "com.gridnine.jtasks.web.core"

fun main() {
    EnvironmentJS.restBaseUrl = "/ui-rest"
    RegistryJS.get().register(WebJTasksCoreActivator())
    if(window.asDynamic().testMode as Boolean? == true){
        return
    }
    launch {
        RegistryJS.get().allOf(ActivatorJS.TYPE).forEach { it.activate() }
        val mainFrameTools = WebActionsHandler.get().getActionsFor(ActionsIds.standard_workspace_tools).actions.map {
            it as ActionWrapper
            WebTabsContainerTool().apply {
                displayName = it.displayName
                handler = {
                    it.getActionHandler<SimpleActionHandler>().invoke()
                }
            }
        }
        val mainFrame = JTasksMainFrame {
            title = "Таски"
            navigationWidth = 200
            tools.addAll(mainFrameTools)
        }
        val workspace = StandardRestClient.standard_standard_getWorkspace(GetWorkspaceRequestJS())
        mainFrame.setWorkspace(workspace.workspace)

        EnvironmentJS.publish(MainFrame::class, mainFrame)
        WebUiLibraryAdapter.get().showWindow(mainFrame)
    }
}

class WebJTasksCoreActivator : ActivatorJS {
    override suspend fun activate() {
        WebCoreMetaRegistriesUpdater.updateMetaRegistries(pluginId)
        DomainReflectionUtilsJS.registerWebDomainClasses()
        RestReflectionUtilsJS.registerWebRestClasses()
        UiReflectionUtilsJS.registerWebUiClasses()
        RegistryJS.get().register(UserAccountEditorHandler())
        console.log("jtasks core module activated")
    }

    override fun getId(): String {
        return pluginId
    }

}