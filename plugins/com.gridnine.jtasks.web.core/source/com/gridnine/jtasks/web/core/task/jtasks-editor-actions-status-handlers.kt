/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.web.core.task

import com.gridnine.jasmine.jtasks.common.core.model.domain.TaskStatusJS
import com.gridnine.jasmine.web.standard.editor.ObjectEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditorActionDisplayHandler
import com.gridnine.jasmine.web.standard.editor.ObjectEditorTool
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.mainframe.ObjectModificationEvent
import com.gridnine.jtasks.common.core.model.rest.ChangeTaskStatusRequestJS
import com.gridnine.jtasks.common.core.model.ui.TaskEditor
import com.gridnine.jtasks.web.core.JtasksRestClient

abstract class BaseTaskEditorStatusActionDisplayHandler(private val predicate:(TaskStatusJS)->Boolean) : ObjectEditorActionDisplayHandler<TaskEditor> {
    override fun isEnabled(editor: ObjectEditor<TaskEditor>): Boolean {
        return true
    }

    override fun isVisible(editor: ObjectEditor<TaskEditor>): Boolean {
        val taskStatus = editor.getEditor().overviewEditor.statusWidget.getValue()
        return predicate.invoke(taskStatus!!)
    }
}

class ResolveTaskEditorActionDisplayHandler : BaseTaskEditorStatusActionDisplayHandler({
    it == TaskStatusJS.NEW  || it == TaskStatusJS.REOPENED
})

class ReopenTaskEditorActionDisplayHandler : BaseTaskEditorStatusActionDisplayHandler({
    it == TaskStatusJS.RESOLVED
})


abstract class BaseTaskStatusEditorButtonHandler(private val status: TaskStatusJS) :ObjectEditorTool<TaskEditor> {
    override suspend fun invoke(editor: ObjectEditor<TaskEditor>) {
        JtasksRestClient.jtasks_task_changeStatus(ChangeTaskStatusRequestJS().apply {
            taskUid = editor.objectUid
            newStatus = status
        })
        MainFrame.get().publishEvent(ObjectModificationEvent(editor.objectType, editor.objectUid))
    }
}

class ResolveTaskStatusEditorButtonHandler: BaseTaskStatusEditorButtonHandler(TaskStatusJS.RESOLVED)

class ReopenTaskStatusEditorButtonHandler: BaseTaskStatusEditorButtonHandler(TaskStatusJS.REOPENED)
