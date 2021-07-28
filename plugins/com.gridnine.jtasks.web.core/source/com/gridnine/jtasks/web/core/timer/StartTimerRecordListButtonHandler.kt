/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.web.core.timer

import com.gridnine.jasmine.web.standard.editor.ObjectEditor
import com.gridnine.jasmine.web.standard.editor.ObjectEditorTool
import com.gridnine.jasmine.web.standard.list.ListLinkButtonHandler
import com.gridnine.jtasks.common.core.model.domain.TaskIndexJS
import com.gridnine.jtasks.common.core.model.domain.TimerRecordEditor
import com.gridnine.jtasks.common.core.model.domain.TimerRecordJS
import com.gridnine.jtasks.common.core.model.domain.TimerStatusJS
import com.gridnine.jtasks.common.core.model.rest.StartTaskTimerRequestJS
import com.gridnine.jtasks.common.core.model.rest.StartTimerRecordRequestJS
import com.gridnine.jtasks.common.core.model.ui.TaskEditor
import com.gridnine.jtasks.web.core.JtasksRestClient
import com.gridnine.jtasks.web.core.workspace.CurrentTaskTimerWidget

class StartTimerRecordListButtonHandler:ListLinkButtonHandler<TimerRecordJS> {
    override suspend fun invoke(selected: List<TimerRecordJS>) {
        if(selected.isEmpty()){
            return
        }
        val item = selected[0]
        val res = JtasksRestClient.jtasks_timer_startTimerRecord(StartTimerRecordRequestJS().also {
            it.timerUid = item.uid
        })
        CurrentTaskTimerWidget.get().setState(state = TimerStatusJS.STARTED, taskUid  = res.taskUid,
            taskTitle = res.taskTitle, taskKey = res.taskKey, lastStartTime = res.lastStarted, commitedTime = res.committedTime)
    }
}