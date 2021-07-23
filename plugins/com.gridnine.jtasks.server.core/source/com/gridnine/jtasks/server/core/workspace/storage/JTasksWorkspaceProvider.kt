/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/

package com.gridnine.jtasks.server.core.workspace.storage

import com.gridnine.jasmine.common.core.model.BaseAsset
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.common.core.utils.AuthUtils
import com.gridnine.jasmine.common.standard.model.domain.*
import com.gridnine.jasmine.server.standard.model.WorkspaceProvider
import com.gridnine.jtasks.common.core.model.domain.ProjectIndex
import com.gridnine.jtasks.common.core.model.domain.UserAccountIndex

class JTasksWorkspaceProvider : WorkspaceProvider {
    override fun getWorkspace(): Workspace {
        val loginName = AuthUtils.getCurrentUser()
        return Storage.get().loadDocument(Workspace::class, "${loginName}_workspace")
                ?: createStandardWorkspace(loginName)
    }

    override fun saveWorkspace(workspace: Workspace):Workspace {
        val loginName = AuthUtils.getCurrentUser()
        workspace.uid = "${loginName}_workspace"
        workspace.setValue(BaseAsset.revision, -1)
        Storage.get().saveDocument(workspace)
        return workspace
    }

    private fun createStandardWorkspace(loginName: String): Workspace {
        val result = Workspace()
        result.uid = "${loginName}_workspace"
        run {
            val group = WorkspaceGroup()
            group.displayName = "Настройки"
            val item = ListWorkspaceItem()
            item.columns.add(UserAccountIndex.loginProperty.name)
            item.columns.add(UserAccountIndex.nameProperty.name)
            item.filters.add(UserAccountIndex.loginProperty.name)
            val order = SortOrder()
            order.orderType = SortOrderType.ASC
            order.field = UserAccountIndex.loginProperty.name
            item.listId = UserAccountIndex::class.qualifiedName
            item.displayName = "Профили"
            group.items.add(item)
            result.groups.add(group)
        }
        run {
            val group = WorkspaceGroup()
            group.displayName = "Проекты"
            val item = ListWorkspaceItem()
            item.columns.add(ProjectIndex.keyProperty.name)
            item.columns.add(ProjectIndex.nameProperty.name)
            val order = SortOrder()
            order.orderType = SortOrderType.ASC
            order.field = ProjectIndex.keyProperty.name
            item.listId = ProjectIndex::class.qualifiedName
            item.displayName = "Все проекты"
            group.items.add(item)
            result.groups.add(group)
        }
        saveWorkspace(result)
        return result
    }

}