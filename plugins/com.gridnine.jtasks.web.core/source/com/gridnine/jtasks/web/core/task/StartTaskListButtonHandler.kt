/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.web.core.task

import com.gridnine.jasmine.web.standard.list.ListLinkButtonHandler
import com.gridnine.jtasks.common.core.model.domain.TaskIndexJS
import com.gridnine.jtasks.common.core.model.domain.TimerStatusJS
import com.gridnine.jtasks.common.core.model.rest.StartTaskTimerRequestJS
import com.gridnine.jtasks.web.core.JtasksRestClient
import com.gridnine.jtasks.web.core.workspace.CurrentTaskTimerWidget

class StartTaskListButtonHandler:ListLinkButtonHandler<TaskIndexJS> {
    override suspend fun invoke(selected: List<TaskIndexJS>) {
        if(selected.isEmpty()){
            return
        }
        val item = selected[0]
        val res = JtasksRestClient.jtasks_task_startTaskTimer(StartTaskTimerRequestJS().also {
            it.taskUid = item.document!!.uid
        })
        CurrentTaskTimerWidget.get().setState(state = TimerStatusJS.STARTED, taskUid  = item.document!!.uid,
            taskTitle = res.taskTitle, taskKey = res.taskKey, lastStartTime = res.lastStarted, commitedTime = res.committedTime)
    }
}