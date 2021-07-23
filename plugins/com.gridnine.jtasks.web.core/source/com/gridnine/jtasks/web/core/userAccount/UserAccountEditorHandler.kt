/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jtasks.web.core.userAccount

import com.gridnine.jasmine.web.standard.editor.ObjectEditorHandler
import com.gridnine.jasmine.web.standard.editor.WebEditor
import com.gridnine.jtasks.common.core.model.domain.UserAccountIndexJS
import com.gridnine.jtasks.common.core.model.ui.UserAccountEditor
import com.gridnine.jtasks.web.core.ActionsIds

class UserAccountEditorHandler:ObjectEditorHandler{
    override fun createEditor(): WebEditor<*, *, *> {
        return UserAccountEditor()
    }

    override fun getActionsGroupId(): String {
        return ActionsIds.com_gridnine_jtasks_common_core_model_domain_UserAccount
    }

    override fun getId(): String {
        return UserAccountIndexJS.objectId
    }
}
