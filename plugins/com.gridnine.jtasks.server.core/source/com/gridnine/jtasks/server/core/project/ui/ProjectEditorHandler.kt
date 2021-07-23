/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jtasks.server.core.project.ui

import com.gridnine.jasmine.common.core.model.TextBoxConfiguration
import com.gridnine.jasmine.common.core.utils.TextUtils
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorHandler
import com.gridnine.jtasks.common.core.model.domain.Project
import com.gridnine.jtasks.common.core.model.domain.UserAccount
import com.gridnine.jtasks.common.core.model.ui.*
import kotlin.reflect.KClass

class ProjectEditorHandler:ObjectEditorHandler<Project, ProjectEditorVM, ProjectEditorVS, ProjectEditorVV>{
    override fun getObjectClass(): KClass<Project> {
        return Project::class
    }

    override fun getVMClass(): KClass<ProjectEditorVM> {
        return ProjectEditorVM::class
    }

    override fun getVSClass(): KClass<ProjectEditorVS> {
        return ProjectEditorVS::class
    }

    override fun getVVClass(): KClass<ProjectEditorVV> {
        return ProjectEditorVV::class
    }

    override fun fillSettings(entity: Project, vsEntity: ProjectEditorVS, vmEntity: ProjectEditorVM, ctx: MutableMap<String, Any?>) {
        vsEntity.key = TextBoxConfiguration{notEditable = true}
    }
    override fun read(entity: Project, vmEntity: ProjectEditorVM, ctx: MutableMap<String, Any?>) {
        vmEntity.key  = entity.key
        vmEntity.name = entity.name
    }

    override fun getTitle(entity: Project, vmEntity: ProjectEditorVM, vsEntity: ProjectEditorVS, ctx: MutableMap<String, Any?>): String? {
        return entity.name
    }

    override fun write(entity: Project, vmEntity: ProjectEditorVM, ctx: MutableMap<String, Any?>) {
        entity.name = vmEntity.name
    }

    override fun validate(vmEntity: ProjectEditorVM, vvEntity: ProjectEditorVV, ctx: MutableMap<String, Any?>) {
        if(TextUtils.isBlank(vmEntity.name)){
            vvEntity.name = StandardL10nMessagesFactory.Empty_field()
        }
    }
}