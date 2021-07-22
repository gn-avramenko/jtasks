/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/
package com.gridnine.jtasks.server.core.storage

import com.gridnine.jasmine.server.core.storage.IndexHandler
import com.gridnine.jtasks.common.core.model.domain.UserAccount
import com.gridnine.jtasks.common.core.model.domain.UserAccountIndex

class UserAccountIndexHandler : IndexHandler<UserAccount, UserAccountIndex> {
    override val documentClass = UserAccount::class
    override val indexClass = UserAccountIndex::class
    override fun createIndexes(doc: UserAccount): List<UserAccountIndex> {
        val idx = UserAccountIndex()
        idx.uid = doc.uid
        idx.login = doc.login
        idx.name = doc.name
        return arrayListOf(idx)
    }
}