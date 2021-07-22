/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.web.core.workspace

import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebLinkButton
import com.gridnine.jasmine.web.core.ui.components.WebTag
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlinx.browser.window
import kotlin.js.Date

enum class CurrentTaskTimerState{
    STOPPED,
    STARTED
}

class CurrentTaskTimerWidget(private val startCallback: suspend (String, CurrentTaskTimerWidget) ->Unit, private val  stopCallback: suspend (String, CurrentTaskTimerWidget) -> Unit):BaseWebNodeWrapper<WebTag>(){

    private var state:CurrentTaskTimerState = CurrentTaskTimerState.STOPPED
    private var taskKey:String? = null
    private var taskTitle:String? = null
    private var commitedTime: Int? = null
    private var lastStartTime: Date? = null
    private val startButtonContainer:WebTag
    private val startButton:WebLinkButton
    private val stopButtonContainer:WebTag
    private val stopButton:WebLinkButton
    private val commitedTimeDiv:WebTag
    private val taskNameDiv:WebTag
    init {
        _node = WebUiLibraryAdapter.get().createTag("div", "currentTaskTimerWidget")
        _node.getClass().addClasses("jtasks-current-task-timer-widget")
        _node.getStyle().setParameters("width" to "100%", "display" to "grid",
        "grid-template-columns" to "50px 60px 1fr", "grid-template-rows" to "1fr")
        val buttonsContainer = WebUiLibraryAdapter.get().createTag("div", "currentTaskTimerButtonsContainer")
        buttonsContainer.getStyle().setParameters("width" to "100%", "grid-row" to "1", "grid-column" to "1")
        _node.getChildren().addChild(buttonsContainer)
        startButtonContainer = WebUiLibraryAdapter.get().createTag("div", "currentTaskTimerStartButtonContainer")
        buttonsContainer.getChildren().addChild(startButtonContainer)
        startButton = WebUiLibraryAdapter.get().createLinkButton {
            title = "Старт"
            width = "100%"
            height = "100%"
        }

        startButtonContainer.getChildren().addChild(startButton)
        stopButtonContainer = WebUiLibraryAdapter.get().createTag("div", "currentTaskTimerStopButtonContainer")
        buttonsContainer.getChildren().addChild(stopButtonContainer)
        stopButton = WebUiLibraryAdapter.get().createLinkButton {
            title = "Стоп"
            width = "100%"
            height = "100%"
        }
        stopButton.setEnabled(false)
        stopButtonContainer.getChildren().addChild(stopButton)
        stopButtonContainer.setVisible(false)
        startButton.setHandler {
            if(taskKey != null) {
                startCallback.invoke(taskKey!!, this)
            }
        }
        stopButton.setHandler {
            stopCallback.invoke(taskKey!!, this)
        }
        commitedTimeDiv = WebUiLibraryAdapter.get().createTag("div", "currentTaskTimerCommitedTimeDiv")
        commitedTimeDiv.getStyle().setParameters("width" to "100%", "grid-row" to "1", "grid-column" to "2")
        commitedTimeDiv.getClass().addClasses("jtasks-current-task-timer-text-element")
        taskNameDiv = WebUiLibraryAdapter.get().createTag("div", "currentTaskTimerTaskNameDiv")
        taskNameDiv.getStyle().setParameters("width" to "100%", "grid-row" to "1", "grid-column" to "3")
        taskNameDiv.getClass().addClasses("jtasks-current-task-timer-text-element")
        _node.getChildren().addChild(commitedTimeDiv)
        _node.getChildren().addChild(taskNameDiv)
        window.setInterval({updateUi()}, 1000)
    }

    private fun updateUi(){
        when(state){
            CurrentTaskTimerState.STARTED ->{
                startButtonContainer.setVisible(false)
                stopButtonContainer.setVisible(true)
                stopButton.setEnabled(true)
            }
            CurrentTaskTimerState.STOPPED ->{
                stopButtonContainer.setVisible(false)
                startButtonContainer.setVisible(true)
                startButton.setEnabled(MiscUtilsJS.isNotBlank(taskKey))
            }
        }
        if(MiscUtilsJS.isBlank(taskKey)){
            commitedTimeDiv.setText("")
            taskNameDiv.setText("")
            return
        }
        var remainder = this.commitedTime?:0
        if(this.lastStartTime != null){
            remainder = (Date.now() - this.lastStartTime!!.getTime()).toInt()
        }
        val hours = remainder/3600000
        val hoursString = if(hours < 10) "0${hours}" else hours.toString()
        remainder -= hours * 3600000
        val minutes = remainder/60000
        val minutesString = if(minutes < 10) "0${minutes}" else minutes.toString()
        remainder -= minutes * 60000
        val seconds = remainder/1000
        val secondsString = if(seconds < 10) "0${seconds}" else seconds.toString()
        commitedTimeDiv.setText("${hoursString}:${minutesString}:${secondsString}")
        taskNameDiv.setText("${taskKey}: ${taskTitle}")
    }

    fun setState(state:CurrentTaskTimerState,  taskKey:String?, taskTitle:String?, commitedTime: Int?, lastStartTime: Date?){
        this.state = state
        this.taskKey = taskKey
        this.taskTitle = taskTitle
        this.commitedTime = commitedTime
        this.lastStartTime = lastStartTime
        updateUi()
    }
}