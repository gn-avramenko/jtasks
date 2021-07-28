/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/
package com.gridnine.jtasks.server.core.task.storage

import com.gridnine.jasmine.server.core.storage.IndexHandler
import com.gridnine.jtasks.common.core.model.domain.*

class TaskIndexHandler : IndexHandler<Task, TaskIndex> {
    override val documentClass = Task::class
    override val indexClass = TaskIndex::class
    override fun createIndexes(doc: Task): List<TaskIndex> {
        val idx = TaskIndex()
        idx.uid = doc.uid
        idx.key = doc.key
        idx.name = doc.name
        idx.assignee = doc.assignee
        idx.created = doc.created
        idx.priority = doc.priority
        idx.project = doc.project
        idx.reporter = doc.reporter
        idx.resolved = doc.resolved
        idx.status = doc.status
        idx.type = doc.type
        return arrayListOf(idx)
    }
}