/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jtasks.web.core.project

import com.gridnine.jasmine.web.standard.editor.ObjectEditorHandler
import com.gridnine.jasmine.web.standard.editor.WebEditor
import com.gridnine.jtasks.common.core.model.domain.ProjectIndexJS
import com.gridnine.jtasks.common.core.model.domain.UserAccountIndexJS
import com.gridnine.jtasks.common.core.model.ui.ProjectEditor
import com.gridnine.jtasks.common.core.model.ui.UserAccountEditor
import com.gridnine.jtasks.web.core.ActionsIds

class ProjectEditorHandler:ObjectEditorHandler{
    override fun createEditor(): WebEditor<*, *, *> {
        return ProjectEditor()
    }

    override fun getActionsGroupId(): String {
        return ActionsIds.com_gridnine_jtasks_common_core_model_domain_Project
    }

    override fun getId(): String {
        return ProjectIndexJS.objectId
    }
}
