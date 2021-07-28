/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jtasks.server.core.task.rest

import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.AuthUtils
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.jtasks.common.core.model.domain.TaskStatus
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.standard.helpers.ValidationUtils
import com.gridnine.jasmine.server.standard.model.SequenceNumberGenerator
import com.gridnine.jtasks.common.core.model.domain.Task
import com.gridnine.jtasks.common.core.model.domain.UserAccountIndex
import com.gridnine.jtasks.common.core.model.rest.CreateTaskRequest
import com.gridnine.jtasks.common.core.model.rest.CreateTaskResponse
import com.gridnine.jtasks.common.core.model.ui.NewTaskEditorVV
import java.time.LocalDateTime

class CreateTaskRestHandler :RestHandler<CreateTaskRequest,CreateTaskResponse>{
    override fun service(request: CreateTaskRequest, ctx: RestOperationContext): CreateTaskResponse {
        val result = CreateTaskResponse()
        val validation = NewTaskEditorVV()
        if(TextUtils.isBlank(request.vm.name)){
            validation.name = StandardL10nMessagesFactory.Empty_field()
        }
        if(TextUtils.isBlank(request.vm.description)){
            validation.description = StandardL10nMessagesFactory.Empty_field()
        }
        if(request.vm.assignee == null){
            validation.assignee = StandardL10nMessagesFactory.Empty_field()
        }
        if(request.vm.priority == null){
            validation.priority = StandardL10nMessagesFactory.Empty_field()
        }
        if(request.vm.project == null){
            validation.project = StandardL10nMessagesFactory.Empty_field()
        }
        if(request.vm.type == null){
            validation.type = StandardL10nMessagesFactory.Empty_field()
        }
        if(ValidationUtils.hasValidationErrors(validation)){
            result.vv = validation
            return result
        }

        val task = Task()
        val project = Storage.get().loadDocument(request.vm.project)!!
        task.key = "${project.key}-${SequenceNumberGenerator.get().incrementAndGet(project.key!!)}"
        task.assignee = request.vm.assignee
        task.created = LocalDateTime.now()
        task.description = request.vm.description
        task.dueDate = request.vm.dueDate
        task.name = request.vm.name
        task.priority = request.vm.priority
        task.reporter = Storage.get().findUniqueDocumentReference(UserAccountIndex::class, UserAccountIndex.loginProperty, AuthUtils.getCurrentUser())
        task.project = request.vm.project
        task.status = TaskStatus.NEW
        task.type = request.vm.type
        Storage.get().saveDocument(task)
        result.objectUid = task.uid
        return result
    }
}