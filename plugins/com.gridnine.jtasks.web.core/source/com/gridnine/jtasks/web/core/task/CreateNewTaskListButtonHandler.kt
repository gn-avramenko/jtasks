/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.web.core.task

import com.gridnine.jasmine.common.core.model.BaseIdentityJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.standard.WebMessages
import com.gridnine.jasmine.web.standard.editor.OpenObjectData
import com.gridnine.jasmine.web.standard.list.ListLinkButtonHandler
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils
import com.gridnine.jtasks.common.core.model.domain.TaskIndexJS
import com.gridnine.jtasks.common.core.model.rest.CreateTaskRequestJS
import com.gridnine.jtasks.common.core.model.ui.NewTaskEditor
import com.gridnine.jtasks.web.core.JtasksRestClient

class CreateNewTaskListButtonHandler : ListLinkButtonHandler<BaseIdentityJS> {
    override suspend fun invoke(selected: List<BaseIdentityJS>) {
        val editor = NewTaskEditor()
        WebUiLibraryAdapter.get().showDialog(editor){
            title = "Создание задачи"
            button {
                displayName = WebMessages.apply
                handler = {
                    val vm = it.getContent().getData()
                    val response = JtasksRestClient.jtasks_task_create(CreateTaskRequestJS().also { resp ->
                        resp.vm = vm
                    })
                    if(StandardUiUtils.hasValidationErrors(response.vv)){
                        it.getContent().showValidation(response.vv)
                        response.vv
                    } else{
                        it.close()
                        MainFrame.get().openTab(OpenObjectData(TaskIndexJS.objectId, response.objectUid!!, null, true))
                    }
                }
            }
            cancelButton()
        }
    }

}