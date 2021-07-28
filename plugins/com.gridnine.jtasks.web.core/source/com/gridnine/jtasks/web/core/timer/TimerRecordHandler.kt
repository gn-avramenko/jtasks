/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jtasks.web.core.timer

import com.gridnine.jasmine.web.standard.editor.ObjectEditorHandler
import com.gridnine.jasmine.web.standard.editor.WebEditor
import com.gridnine.jtasks.common.core.model.domain.*
import com.gridnine.jtasks.common.core.model.ui.ProjectEditor
import com.gridnine.jtasks.common.core.model.ui.TaskEditor
import com.gridnine.jtasks.common.core.model.ui.UserAccountEditor
import com.gridnine.jtasks.web.core.ActionsIds

class TimerRecordHandler:ObjectEditorHandler{
    override fun createEditor(): WebEditor<*, *, *> {
        return TimerRecordEditor()
    }

    override fun getActionsGroupId(): String {
        return ActionsIds.com_gridnine_jtasks_common_core_model_domain_TimerRecord
    }

    override fun getId(): String {
        return TimerRecordJS.objectId
    }
}
