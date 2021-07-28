/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jtasks.server.core.task.ui

import com.gridnine.jasmine.common.core.model.TextBoxConfiguration
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorHandler
import com.gridnine.jtasks.common.core.model.domain.Task
import com.gridnine.jtasks.common.core.model.ui.*
import com.gridnine.jtasks.web.core.task.WebHtmlContentViewerVM
import kotlin.reflect.KClass

class TaskEditorHandler:ObjectEditorHandler<Task, TaskEditorVM, TaskEditorVS, TaskEditorVV>{
    override fun getObjectClass(): KClass<Task> {
        return Task::class
    }

    override fun getVMClass(): KClass<TaskEditorVM> {
        return TaskEditorVM::class
    }

    override fun getVSClass(): KClass<TaskEditorVS> {
        return TaskEditorVS::class
    }

    override fun getVVClass(): KClass<TaskEditorVV> {
        return TaskEditorVV::class
    }

    override fun fillSettings(entity: Task, vsEntity: TaskEditorVS, vmEntity: TaskEditorVM, ctx: MutableMap<String, Any?>) {
        vsEntity.details = TaskDetailsPanelVS().also { details ->
            details.key = TextBoxConfiguration().also { it.notEditable = true }
        }
    }
    override fun read(entity: Task, vmEntity: TaskEditorVM, ctx: MutableMap<String, Any?>) {
        vmEntity.overview = TaskOverviewPanelVM().also {
            it.assignee = entity.assignee
            it.created = entity.created
            it.dueDate = entity.dueDate
            it.name = "${entity.key}/${entity.name}"
            it.priority = entity.priority
            it.reporter = entity.reporter
            it.resolved = entity.resolved
            it.status = entity.status
            it.type = entity.type
            it.description = WebHtmlContentViewerVM().apply { content = entity.description }
        }
        vmEntity.details = TaskDetailsPanelVM().also {
            it.assignee = entity.assignee
            it.description = entity.description
            it.dueDate = entity.dueDate
            it.name = entity.name
            it.priority = entity.priority
            it.type = entity.type
            it.key = entity.key
        }
    }

    override fun getTitle(entity: Task, vmEntity: TaskEditorVM, vsEntity: TaskEditorVS, ctx: MutableMap<String, Any?>): String? {
        return entity.key
    }

    override fun write(entity: Task, vmEntity: TaskEditorVM, ctx: MutableMap<String, Any?>) {
        entity.assignee =vmEntity.details.assignee
//        entity.description = vmEntity.details.description
        entity.dueDate = vmEntity.details.dueDate
        entity.name = vmEntity.details.name
        entity.priority = vmEntity.details.priority
        entity.type = vmEntity.details.type
    }

    override fun validate(vmEntity: TaskEditorVM, vvEntity: TaskEditorVV, ctx: MutableMap<String, Any?>) {
        vvEntity.details = TaskDetailsPanelVV().also { details ->
            if(TextUtils.isBlank(vmEntity.details.name)){
                details.name = StandardL10nMessagesFactory.Empty_field()
            }
//            if(TextUtils.isBlank(vmEntity.details.description)){
//                details.description = StandardL10nMessagesFactory.Empty_field()
//            }
            if(vmEntity.details.assignee == null){
                details.assignee = StandardL10nMessagesFactory.Empty_field()
            }
            if(vmEntity.details.priority == null){
                details.priority = StandardL10nMessagesFactory.Empty_field()
            }
            if(vmEntity.details.type == null){
                details.type = StandardL10nMessagesFactory.Empty_field()
            }
        }

    }
}