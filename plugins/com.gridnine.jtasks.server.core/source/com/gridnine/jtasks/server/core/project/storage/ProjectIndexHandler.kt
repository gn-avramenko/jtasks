/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/
package com.gridnine.jtasks.server.core.project.storage

import com.gridnine.jasmine.server.core.storage.IndexHandler
import com.gridnine.jtasks.common.core.model.domain.Project
import com.gridnine.jtasks.common.core.model.domain.ProjectIndex
import com.gridnine.jtasks.common.core.model.domain.UserAccount
import com.gridnine.jtasks.common.core.model.domain.UserAccountIndex

class ProjectIndexHandler : IndexHandler<Project, ProjectIndex> {
    override val documentClass = Project::class
    override val indexClass = ProjectIndex::class
    override fun createIndexes(doc: Project): List<ProjectIndex> {
        val idx = ProjectIndex()
        idx.uid = doc.uid
        idx.key = doc.key
        idx.name = doc.name
        return arrayListOf(idx)
    }
}