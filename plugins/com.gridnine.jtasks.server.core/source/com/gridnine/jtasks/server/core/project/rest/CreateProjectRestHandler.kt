/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jtasks.server.core.project.rest

import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.core.rest.RestHandler
import com.gridnine.jasmine.server.core.rest.RestOperationContext
import com.gridnine.jasmine.server.standard.helpers.ValidationUtils
import com.gridnine.jtasks.common.core.model.domain.Project
import com.gridnine.jtasks.common.core.model.domain.ProjectIndex
import com.gridnine.jtasks.common.core.model.rest.CreateProjectRequest
import com.gridnine.jtasks.common.core.model.rest.CreateProjectResponse
import com.gridnine.jtasks.common.core.model.ui.NewProjectEditorVV

class CreateProjectRestHandler :RestHandler<CreateProjectRequest,CreateProjectResponse>{
    override fun service(request: CreateProjectRequest, ctx: RestOperationContext): CreateProjectResponse {
        val result = CreateProjectResponse()
        val validation = NewProjectEditorVV()
        if(TextUtils.isBlank(request.vm.key)){
            validation.key = StandardL10nMessagesFactory.Empty_field()
        }
        if(TextUtils.isBlank(request.vm.name)){
            validation.name = StandardL10nMessagesFactory.Empty_field()
        }
        if(!TextUtils.isNotBlank(request.vm.key)){
            if(Storage.get().findUniqueDocumentReference(ProjectIndex::class, ProjectIndex.keyProperty, request.vm.key) != null){
                validation.key = "Такой ключ уже сущентсвует"
            }
        }
        if(ValidationUtils.hasValidationErrors(validation)){
            result.vv = validation
            return result
        }
        val project = Project()
        project.key = request.vm.key
        project.name = request.vm.name
        Storage.get().saveDocument(project)
        result.objectUid = project.uid
        return result
    }
}