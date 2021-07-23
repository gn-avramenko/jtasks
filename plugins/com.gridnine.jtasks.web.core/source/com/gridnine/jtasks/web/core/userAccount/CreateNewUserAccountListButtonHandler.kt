/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.web.core.userAccount

import com.gridnine.jasmine.common.core.model.BaseIdentityJS
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.standard.WebMessages
import com.gridnine.jasmine.web.standard.editor.OpenObjectData
import com.gridnine.jasmine.web.standard.editor.WebEditor
import com.gridnine.jasmine.web.standard.list.ListLinkButtonHandler
import com.gridnine.jasmine.web.standard.mainframe.MainFrame
import com.gridnine.jasmine.web.standard.utils.StandardUiUtils
import com.gridnine.jtasks.common.core.model.domain.UserAccountIndexJS
import com.gridnine.jtasks.common.core.model.rest.CreateUserAccountRequestJS
import com.gridnine.jtasks.common.core.model.ui.NewUserAccountEditor
import com.gridnine.jtasks.web.core.JtasksRestClient

class CreateNewUserAccountListButtonHandler : ListLinkButtonHandler<BaseIdentityJS> {
    override suspend fun invoke(selected: List<BaseIdentityJS>) {
        val editor = NewUserAccountEditor()
        WebUiLibraryAdapter.get().showDialog(editor){
            title = "Создание нового пользователя"
            button {
                displayName = WebMessages.apply
                handler = {
                    val vm = it.getContent().getData()
                    val response = JtasksRestClient.jtasks_userAccount_create(CreateUserAccountRequestJS().also {resp ->
                        resp.vm = vm
                    })
                    if(StandardUiUtils.hasValidationErrors(response.vv)){
                        it.getContent().showValidation(response.vv)
                        response.vv
                    } else{
                        it.close()
                        MainFrame.get().openTab(OpenObjectData(UserAccountIndexJS.objectId, response.objectUid!!, null, true))
                    }
                }
            }
            cancelButton()
        }
    }

}