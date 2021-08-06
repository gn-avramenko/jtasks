/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.web.core.activator

import com.gridnine.jasmine.common.core.meta.DatabasePropertyTypeJS
import com.gridnine.jasmine.common.core.model.ObjectReferenceJS
import com.gridnine.jasmine.common.standard.model.rest.GetWorkspaceRequestJS
import com.gridnine.jasmine.jtasks.common.core.model.domain.TaskPriorityJS
import com.gridnine.jasmine.jtasks.common.core.model.domain.TaskStatusJS
import com.gridnine.jasmine.jtasks.common.core.model.domain.TaskTypeJS
import com.gridnine.jasmine.web.core.common.ActivatorJS
import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.common.RegistryJS
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
import com.gridnine.jasmine.web.standard.widgets.WebGridLayoutWidget
import com.gridnine.jtasks.common.core.model.domain.TaskIndexJS
import com.gridnine.jtasks.web.core.DomainReflectionUtilsJS
import com.gridnine.jtasks.web.core.RestReflectionUtilsJS
import com.gridnine.jtasks.web.core.UiReflectionUtilsJS
import com.gridnine.jtasks.web.core.project.ProjectEditorHandler
import com.gridnine.jtasks.web.core.task.TaskEditorHandler
import com.gridnine.jtasks.web.core.timer.TimerRecordHandler
import com.gridnine.jtasks.web.core.userAccount.UserAccountEditorHandler
import com.gridnine.jtasks.web.core.workspace.JTasksMainFrame
import kotlinx.browser.window
import kotlin.js.Date

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
                    content =  if(it.text.toLowerCase().contains("профил")){
                        TestProfileListEditor()
                    } else {
                        val h = WebUiLibraryAdapter.get().createTag("div")
                        h.setText("Content of ${it.text}")
                        h
                    }
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

class TestProfileListEditor: BaseWebNodeWrapper<WebBorderContainer>(){
    init {
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            fit = true
        }
        val button1 = WebUiLibraryAdapter.get().createLinkButton {
            title = "Button1"
        }
        button1.setHandler {
            console.log("clicked Button1")
        }

        val button2 = WebUiLibraryAdapter.get().createMenuButton {
            title = "Advanced"
            elements.add(StandardMenuItem().also {
                it.id  = "item1"
                it.title = "Item1"
            })
            elements.add(StandardMenuItem().also {
                it.id  = "item2"
                it.title = "Item2"
            })
        }
        button2.setHandler("item1"){
            console.log("clicked 1")
        }
        button2.setHandler("item2"){
            console.log("clicked 2")
        }

        val searchText = WebUiLibraryAdapter.get().createSearchBox {
            prompt = "Search"
        }
        searchText.setSearcher {
            console.log(it)
        }
        val northContent = WebGridLayoutWidget{
            width ="100%"
        }
        northContent.setColumnsWidths("auto","auto","100%", "auto")
        northContent.addRow(arrayListOf(button1, button2, null, searchText))
        _node.setNorthRegion {
            content = northContent
        }
        val centerContent = WebUiLibraryAdapter.get().createDataGrid<TaskIndexJS> {
            fit = true
            fitColumns = true
            dataType = DataGridDataType.LOCAL
            selectionType = DataGridSelectionType.SINGLE
            showPagination = true
            column {
                fieldId = "key"
                sortable = true
                title = "Ключ"
            }
            column {
                fieldId = "name"
                sortable = true
                title = "Название"
            }
            column {
                fieldId = "project"
                sortable = true
                title = "Проект"
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.ENTITY_REFERENCE)
            }
            column {
                fieldId = "type"
                sortable = true
                title = "Тип"
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.ENUM)
            }
            column {
                fieldId = "status"
                sortable = true
                title = "Статус"
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.ENUM)
            }
            column {
                fieldId = "assignee"
                sortable = true
                title = "Исполнитель"
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.ENTITY_REFERENCE)
            }
            column {
                fieldId = "reporter"
                sortable = true
                title = "Создатель"
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.ENTITY_REFERENCE)
            }
            column {
                fieldId = "priority"
                sortable = true
                title = "Приоритет"
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.ENUM)
            }
            column {
                fieldId = "created"
                sortable = true
                title = "Создана"
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.LOCAL_DATE_TIME)
            }
            column {
                fieldId = "resolved"
                sortable = true
                title = "Решена"
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.LOCAL_DATE_TIME)
            }
            column {
                fieldId = "dueDate"
                sortable = true
                title = "Выполнить до"
                formatter = MiscUtilsJS.createListFormatter(DatabasePropertyTypeJS.LOCAL_DATE_TIME)
            }
        }
        val localData = arrayListOf<TaskIndexJS>()
        for(n in 0..100){
            val item = TaskIndexJS()
            item.assignee = createObjectReference("assignee", n)
            item.created = Date()
            item.dueDate = Date()
            item.key = "key$n"
            item.name = "name$n"
            item.priority = if(n%2 ==0) TaskPriorityJS.MAJOR else TaskPriorityJS.CRITICAL
            item.project =  createObjectReference("project", n)
            item.reporter = createObjectReference("reporter", n)
            item.resolved = Date()
            item.status= if(n%2 ==0) TaskStatusJS.NEW else TaskStatusJS.RESOLVED
            item.type =  if(n%2 ==0) TaskTypeJS.BUG else TaskTypeJS.IMPROVEMENT
            localData.add(item)
        }
        centerContent.setLocalData(localData)
        _node.setCenterRegion {
            content = centerContent
        }
    }

    private fun createObjectReference(key: String, n: Int): ObjectReferenceJS? {
            return ObjectReferenceJS("object", "$key$n", "$key$n")
    }
}