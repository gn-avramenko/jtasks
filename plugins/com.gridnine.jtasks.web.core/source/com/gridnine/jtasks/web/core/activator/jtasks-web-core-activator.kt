/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.web.core.activator

import com.gridnine.jasmine.common.standard.model.rest.GetWorkspaceRequestJS
import com.gridnine.jasmine.web.antd.components.ReactFacade
import com.gridnine.jasmine.web.core.common.ActivatorJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.common.RegistryJS
import com.gridnine.jasmine.web.core.reflection.ReflectionFactoryJS
import com.gridnine.jasmine.web.core.remote.WebCoreMetaRegistriesUpdater
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.*
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import com.gridnine.jasmine.web.standard.ActionsIds
import com.gridnine.jasmine.web.standard.StandardRestClient
import com.gridnine.jasmine.web.standard.mainframe.ActionWrapper
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.mainframe.WebActionsHandler
import com.gridnine.jtasks.common.core.model.ui.UserAccountEditor
import com.gridnine.jtasks.web.core.DomainReflectionUtilsJS
import com.gridnine.jtasks.web.core.RestReflectionUtilsJS
import com.gridnine.jtasks.web.core.UiReflectionUtilsJS
import com.gridnine.jtasks.web.core.project.ProjectEditorHandler
import com.gridnine.jtasks.web.core.task.TaskEditorHandler
import com.gridnine.jtasks.web.core.timer.TimerRecordHandler
import com.gridnine.jtasks.web.core.userAccount.UserAccountEditorHandler
import com.gridnine.jtasks.web.core.workspace.JTasksMainFrame
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element

const val pluginId = "com.gridnine.jtasks.web.core"

const val draft = true

fun main() {
    EnvironmentJS.restBaseUrl = "/ui-rest"
    RegistryJS.get().register(WebJTasksCoreActivator())
    if(window.asDynamic().testMode as Boolean? == true){
        return
    }
    if(draft){
        launch {
            RegistryJS.get().allOf(ActivatorJS.TYPE).forEach { it.activate() }
            val workspace = StandardRestClient.standard_standard_getWorkspace(GetWorkspaceRequestJS()).workspace
            val leftContent = WebUiLibraryAdapter.get().createTree {
                mold = WebTreeMold.NAVIGATION
                fit = true
            }

            val centerContent = WebUiLibraryAdapter.get().createTabsContainer {
                fit = true
                tools.add(WebTabsContainerTool().also {
                    it.displayName = "Выход"
                    it.handler = {
                        console.log("Выход")
                    }
                })
            }
            leftContent.setSelectListener {
                centerContent.addTab {
                    title = it.text
                    closable = true
                    val cnt = WebUiLibraryAdapter.get().createTag("div")
                    cnt.setText("Content of ${it.text}")
                    content =  cnt
                }
            }
            val mainFrame = WebUiLibraryAdapter.get().createBorderContainer {
                fit = true
            }
            mainFrame.setWestRegion {
                content = leftContent
            }
            mainFrame.setCenterRegion {
                content = centerContent
            }
           WebUiLibraryAdapter.get().showWindow(mainFrame)
            val data = arrayListOf<WebTreeNode>()
            workspace.groups.forEach {wg ->
                val group = WebTreeNode(wg.uid!!, wg.displayName!!, wg)
                data.add(group)
                wg.items.forEach {wi ->
                    group.children.add(WebTreeNode(wi.id, wi.text, wi))
                }
            }
            leftContent.setData(data)
        }
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
        RegistryJS.get().register(ProjectEditorHandler())
        RegistryJS.get().register(TaskEditorHandler())
        RegistryJS.get().register(TimerRecordHandler())
        console.log("jtasks core module activated")
    }

    override fun getId(): String {
        return pluginId
    }

}

class TestMainFrame : BaseWebNodeWrapper<WebBorderContainer>(){
    init {
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            fit = true
        }
    }
}