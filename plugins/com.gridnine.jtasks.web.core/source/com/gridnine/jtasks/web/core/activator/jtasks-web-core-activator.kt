/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.web.core.activator

import com.gridnine.jasmine.common.core.meta.DatabasePropertyTypeJS
import com.gridnine.jasmine.common.core.model.ObjectReferenceJS
import com.gridnine.jasmine.common.core.model.XeptionJS
import com.gridnine.jasmine.common.standard.model.domain.SortOrderTypeJS
import com.gridnine.jasmine.common.standard.model.rest.GetWorkspaceRequestJS
import com.gridnine.jasmine.jtasks.common.core.model.domain.TaskPriorityJS
import com.gridnine.jasmine.jtasks.common.core.model.domain.TaskStatusJS
import com.gridnine.jasmine.jtasks.common.core.model.domain.TaskTypeJS
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
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils
import com.gridnine.jasmine.web.standard.widgets.*
import com.gridnine.jtasks.common.core.model.domain.TaskIndexJS
import com.gridnine.jtasks.common.core.model.domain.UserAccountIndexJS
import com.gridnine.jtasks.web.core.DomainReflectionUtilsJS
import com.gridnine.jtasks.web.core.RestReflectionUtilsJS
import com.gridnine.jtasks.web.core.UiReflectionUtilsJS
import com.gridnine.jtasks.web.core.project.ProjectEditorHandler
import com.gridnine.jtasks.web.core.task.TaskEditorHandler
import com.gridnine.jtasks.web.core.timer.TimerRecordHandler
import com.gridnine.jtasks.web.core.userAccount.UserAccountEditorHandler
import com.gridnine.jtasks.web.core.workspace.JTasksMainFrame
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlin.js.Date

const val pluginId = "com.gridnine.jtasks.web.core"

const val draft = false

var executingStressTest = false
fun main() {
    EnvironmentJS.restBaseUrl = "/ui-rest"
    RegistryJS.get().register(WebJTasksCoreActivator())
    if(window.asDynamic().testMode as Boolean? == true){
        return
    }
    if(draft){
        launch {
            RegistryJS.get().allOf(ActivatorJS.TYPE).forEach { it.activate() }
            if(false){
                val innerBorderContainer = WebUiLibraryAdapter.get().createBorderContainer {
                    fit = true
                }
                innerBorderContainer.setNorthRegion {
                    content = WebLabelWidget("north")
                }
                val centerContent = WebUiLibraryAdapter.get().createTag("div")
                for(n in 0..100){
                    val label =   WebUiLibraryAdapter.get().createTag("div" )
                    label.setText("text${n}")
                    centerContent.getChildren().addChild(label)
                }
                innerBorderContainer.setCenterRegion {
                    content = centerContent
                }
//                val tabsWrapper = WebUiLibraryAdapter.get().createBorderContainer {
//                    fit = true
//                }
//                tabsWrapper.setNorthRegion {
//                    content = WebLabelWidget("tabs")
//                }
//                tabsWrapper.setCenterRegion {
//                    content = innerBorderContainer
//                }
                val tabs = WebUiLibraryAdapter.get().createTabsContainer {
                    height ="100%"
                    width = "100%"
                }
                tabs.addTab {
                    title = "Tab"
                    content = innerBorderContainer
                }
                val outerBorderContainer = WebUiLibraryAdapter.get().createBorderContainer {
                    fit = true
                }
                outerBorderContainer.setWestRegion {
                    width = 200
                    content = WebLabelWidget("west")
                }
                outerBorderContainer.setCenterRegion {
                    content = tabs
                }
                WebUiLibraryAdapter.get().showWindow(outerBorderContainer)
                return@launch
            }
            val workspace = StandardRestClient.standard_standard_getWorkspace(GetWorkspaceRequestJS()).workspace
            val leftContent = WebUiLibraryAdapter.get().createTree {
                mold = WebTreeMold.NAVIGATION
                fit = true
            }

            var centerContent:WebTabsContainer? = null
            centerContent = WebUiLibraryAdapter.get().createTabsContainer {
                fit = true
                tools.add(WebTabsContainerTool().also {
                    it.displayName = "Выход"
                    it.handler = {
                        console.log("Выход")
                    }
                })
                tools.add(WebTabsContainerTool().also {
                    it.displayName = "Начать стресс тест"
                    it.handler = {
                        launch {
                            executingStressTest = true
                            var idx = 1
                            while (executingStressTest) {
                                idx++
                                delay(1000)
                                centerContent!!.addTab {
                                    title = "Профили ${idx}"
                                    content = TestProfileListEditor(idx)
                                }
                                delay((2000))
                                centerContent!!.let {
                                    val tab = it.getTabs()[0]
                                    it.removeTab(tab.id)
                                }
                            }
                        }
                    }
                })
                tools.add(WebTabsContainerTool().also {
                    it.displayName = "Закончить стресс тест"
                    it.handler = {
                        executingStressTest = false
                    }
                })
            }
            leftContent.setSelectListener {
                centerContent.addTab {
                    title = it.text
                    closable = true
                    content =  if(it.text.toLowerCase().contains("профил")){
                        TestProfileListEditor(0)
                    } else if(it.text.toLowerCase().contains("проект")){
                        //TestProjectditor()
//                        TestPanelEditor()
                        //TestAccordionEditor()
//                        TestRteEditor()
                        TestTreeEditor()
                    }else {
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
            title = ""
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
class TestProfileListEditor(startIndex: Int): BaseWebNodeWrapper<WebBorderContainer>(){
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
            item.assignee = createObjectReference("assignee", startIndex+n)
            item.created = Date()
            item.dueDate = Date()
            item.key = "key${startIndex+n}"
            item.name = "name$${startIndex+n}"
            item.priority = if(n%2 ==0) TaskPriorityJS.MAJOR else TaskPriorityJS.CRITICAL
            item.project =  createObjectReference("project", startIndex+n)
            item.reporter = createObjectReference("reporter", startIndex+n)
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
class TestPanelEditor: BaseWebNodeWrapper<WebBorderContainer>() {
    init {
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            fit = true
        }
        _node.setNorthRegion {
            content = WebLabelWidget("north")
        }
        val panel = WebUiLibraryAdapter.get().createPanel {
            fit = true
            tools.add(PanelToolConfiguration(MiscUtilsJS.createUUID(), "core:link"))
            content = WebLabelWidget("center")
        }
        panel.setToolHandler{ key, webPanel ->
            console.log("pressed $key")
        }

        panel.setTitle("Title")
        _node.setCenterRegion {
            content = panel
        }
    }
}

class TestTreeEditor: BaseWebNodeWrapper<WebBorderContainer>() {
    init {
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            fit = true
        }
        _node.setNorthRegion {
            content = WebLabelWidget("north")
        }
        val tree = WebUiLibraryAdapter.get().createTree {
            width = "200px"
            height = "100%"
            mold = WebTreeMold.STANDARD
            enableDnd = true
        }
        val data = arrayListOf<WebTreeNode>()
        for(n in 0..10){
            val parent = WebTreeNode("parent$n", "Parent $n", null)
            data.add(parent)
            for(m in 0..5){
                val child = WebTreeNode("child$n-$m", "Child $n $m", null)
                parent.children.add(child)
            }
        }
        tree.setData(data)
        tree.setContextMenuBuilder {
            if(it.children.isNotEmpty()){
                null
            } else {
                listOf(1,2,3,4).map {
                    WebContextMenuStandardItem("child ${it}", null, false){
                        console.log("PRESSED $it")
                    }
                }
            }
        }
        tree.setSelectListener {
            console.log("selected ${it.text}")
        }

        tree.setOnDropListener { target, source, point ->
            tree.remove(source.id)
            tree.insertAfter(source, target.id)
        }

        _node.setCenterRegion {
            content = tree
        }
    }
}

class TestRteEditor: BaseWebNodeWrapper<WebBorderContainer>() {
    init {
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            fit = true
        }
        val rte = WebUiLibraryAdapter.get().createRichTextEditor {
            width = "100%"
            height = "500px"
        }
        val button = WebUiLibraryAdapter.get().createLinkButton {
            title = "Get content"
        }
        button.setHandler {
            console.log(rte.getContent())
        }
        _node.setNorthRegion {
            content = button
        }

        _node.setCenterRegion {
            content = rte
        }
    }
}
class TestAccordionEditor: BaseWebNodeWrapper<WebBorderContainer>() {
    init {
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            fit = true
        }
        _node.setNorthRegion {
            content = WebLabelWidget("north")
        }
        val panel = WebUiLibraryAdapter.get().createAccordionContainer {
            fit = true
        }
        panel.addPanel {
            title = "Panel 1"
            content = WebLabelWidget("Panel1")
            id = MiscUtilsJS.createUUID()
        }
        panel.addPanel {
            title = "Panel 2"
            content = WebLabelWidget("Panel2")
            id = MiscUtilsJS.createUUID()
        }
        _node.setCenterRegion {
            content = panel
        }
    }
}
class TestProjectditor: BaseWebNodeWrapper<WebBorderContainer>() {
    init {
        _node = WebUiLibraryAdapter.get().createBorderContainer {
            fit = true
        }
        _node.setNorthRegion {
            content = WebLabelWidget("north")
        }
//        val centerContent = WebUiLibraryAdapter.get().createTag("div")
////        centerContent.getStyle().setParameters("overflowY" to "scroll")
////        centerContent.getStyle().setParameters("height" to "100vh")
//        for(n in 0..100){
//            val label =   WebUiLibraryAdapter.get().createTag("div" )
//            label.setText("text${n}")
//            centerContent.getChildren().addChild(label)
//        }
//        _node.setCenterRegion {
//            content = centerContent
//        }
//
        val grid = WebGridLayoutWidget {
            width = "100%"
        }
        _node.setCenterRegion {
            content = grid
        }
        grid.setColumnsWidths("600px")
        val enum1 = EnumValueWidget<SortOrderTypeJS>{
            width = "100%"
            allowNull = true
            enumClass = SortOrderTypeJS::class
        }
        enum1.setValue(SortOrderTypeJS.ASC)
        val enum2 = EnumMultiValuesWidget<SortOrderTypeJS>{
            width = "100%"
            showClearIcon = true
            enumClassName = ReflectionFactoryJS.get().getQualifiedClassName(SortOrderTypeJS::class)
        }
        enum2.setValues(arrayListOf(SortOrderTypeJS.ASC))
        enum2.showValidation("Error")
        grid.addRow(WebGridCellWidget("Test ", enum1))
        grid.addRow(WebGridCellWidget("Test 2", enum2))
        val ett1 = EntitySelectWidget{
            width = "100%"
            showClearIcon = true
            showLinkButton = true
            handler = AutocompleteHandler.createMetadataBasedAutocompleteHandler(UserAccountIndexJS.objectId+"JS")
        }
        grid.addRow(WebGridCellWidget("Entity 1", ett1))

        val ett2 = EntityMultiValuesWidget{
            width = "100%"
            showClearIcon = true
            handler = AutocompleteHandler.createMetadataBasedAutocompleteHandler(UserAccountIndexJS.objectId+"JS")
        }
        grid.addRow(WebGridCellWidget("Entity 2", ett2))
        val date = DateBoxWidget{
            width = "100%"
            showClearIcon = true
        }
        date.setValue(Date())
        grid.addRow(WebGridCellWidget("Date", date))
        val dateTime = DateTimeBoxWidget{
            width = "100%"
            showClearIcon = true
        }
        dateTime.setValue(Date())
        grid.addRow(WebGridCellWidget("Date time", dateTime))
        val text = TextBoxWidget{
            width = "100%"
            showClearIcon = true
        }
        text.setValue("test")
        grid.addRow(WebGridCellWidget("Text", text))
        val password = WebUiLibraryAdapter.get().createPasswordBox{
            width = "100%"
        }
        password.setValue("test")
        grid.addRow(WebGridCellWidget("Password", password))
        val floatNumber = FloatNumberBoxWidget{
            width = "100%"
        }
        floatNumber.setValue(2.0)
        grid.addRow(WebGridCellWidget("Float", floatNumber))
        val intNumber = IntegerNumberBoxWidget{
            width = "100%"
        }
        intNumber.setValue(2)
        grid.addRow(WebGridCellWidget("Integer", intNumber))
        val button = WebUiLibraryAdapter.get().createLinkButton {
            title = "Ошибка"
        }
        button.setHandler {
            StandardUiUtils.showError("Текст ошибки")
        }
        grid.addRow(button)
        val dialogButton = WebUiLibraryAdapter.get().createLinkButton {
            title = "Диалог"
        }
        dialogButton.setHandler {
            val textBox = TextBoxWidget{
                width = "100%"
            }
            WebUiLibraryAdapter.get().showDialog(textBox){
                title = "Простой диалог"
                button {
                    displayName = "OK"
                    handler ={ dialog ->
                        console.log("OK pressed")
                        dialog.close()
                    }
                }
                cancelButton()
            }
        }
        grid.addRow(dialogButton)
        val booleanBox = BooleanBoxWidget{}
        grid.addRow(booleanBox)
    }


}

