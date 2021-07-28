/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.web.core.workspace

import com.gridnine.jasmine.web.core.common.EnvironmentJS
import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.mainframe.MainFrameConfiguration
import com.gridnine.jtasks.common.core.model.domain.TimerStatusJS
import com.gridnine.jtasks.common.core.model.rest.GetCurrentTimerRequestJS
import com.gridnine.jtasks.common.core.model.rest.StartTaskTimerRequestJS
import com.gridnine.jtasks.common.core.model.rest.StopTaskTimerRequestJS
import com.gridnine.jtasks.web.core.JtasksRestClient

class JTasksMainFrame(configure: MainFrameConfiguration.()->Unit):MainFrame(configure) {

    init {
        val centerContainer = WebUiLibraryAdapter.get().createBorderContainer {
            width = "100%"
            height = "100%"
        }
        val timerWidget = CurrentTaskTimerWidget({ taskUid, widget ->
                val res = JtasksRestClient.jtasks_task_startTaskTimer(StartTaskTimerRequestJS().also {
                    it.taskUid = taskUid
                })
            widget.setState(state = TimerStatusJS.STARTED, taskUid = taskUid, taskKey = res.taskKey,
                taskTitle =   res.taskTitle, lastStartTime = res.lastStarted, commitedTime = res.committedTime)
        },{ taskUid, widget ->
            val res = JtasksRestClient.jtasks_timer_stopTaskTimer(StopTaskTimerRequestJS().also {
                it.taskUid = taskUid
            })
            widget.setState(state = res.taskStatus?:TimerStatusJS.STOPPED, taskUid = taskUid, taskKey = res.taskKey,
                taskTitle =   res.taskTitle, lastStartTime = res.lastStarted, commitedTime = res.committedTime)
        })
        centerContainer.setNorthRegion {
            collapsible = false
            showBorder = false
            showSplitLine =false
            content = timerWidget
        }
        centerContainer.setCenterRegion {
            collapsible = false
            showBorder = false
            showSplitLine =false
            content = tabs
        }
        _node.setCenterRegion {
            collapsible = false
            showBorder = false
            showSplitLine =false
            content = centerContainer
        }
        EnvironmentJS.publish(timerWidget)
        launch {
            val res = JtasksRestClient.jtasks_timer_getCurrentTimer(GetCurrentTimerRequestJS())
            timerWidget.setState(state=res.taskStatus?:TimerStatusJS.STOPPED,
                commitedTime = res.committedTime, lastStartTime = res.lastStarted,taskKey = res.taskKey,
                taskTitle = res.taskTitle, taskUid = res.taskUid)
        }
    }
}