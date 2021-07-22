/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.web.core.workspace

import com.gridnine.jasmine.web.core.remote.launch
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.mainframe.MainFrameConfiguration
import kotlin.js.Date

class JTasksMainFrame(configure: MainFrameConfiguration.()->Unit):MainFrame(configure) {

    init {
        val centerContainer = WebUiLibraryAdapter.get().createBorderContainer {
            width = "100%"
            height = "100%"
        }
        val timerWidget = CurrentTaskTimerWidget({ taskStr, widget ->
            console.log("started:${taskStr}")
        },{ taskStr, widget ->
            console.log("stopped:${taskStr}")
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
        launch {
            timerWidget.setState(state=CurrentTaskTimerState.STARTED,
                commitedTime = 0, lastStartTime = Date(),taskKey = "XTR-1",
                taskTitle = "Test task"
            )
        }
    }
}