/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.web.core.task

import com.gridnine.jasmine.web.standard.editor.ObjectEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditorTool
import com.gridnine.jtasks.common.core.model.domain.TimerStatusJS
import com.gridnine.jtasks.common.core.model.rest.StartTaskTimerRequestJS
import com.gridnine.jtasks.common.core.model.ui.TaskEditor
import com.gridnine.jtasks.web.core.JtasksRestClient
import com.gridnine.jtasks.web.core.workspace.CurrentTaskTimerWidget

class StartTaskEditorButtonHandler : ObjectEditorTool<TaskEditor> {
    override suspend fun invoke(editor: ObjectEditor<TaskEditor>) {
        val res = JtasksRestClient.jtasks_task_startTaskTimer(StartTaskTimerRequestJS().also {
            it.taskUid = editor.objectUid
        })
        CurrentTaskTimerWidget.get().setState(state = TimerStatusJS.STARTED, taskUid = editor.objectUid,
        taskTitle = res.taskTitle, taskKey = res.taskKey, lastStartTime = res.lastStarted, commitedTime = res.committedTime)
    }
}