/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/

package com.gridnine.jtasks.server.core.storage

import com.gridnine.jasmine.server.core.storage.cache.CacheConfiguration
import com.gridnine.jtasks.common.core.model.domain.UserAccount
import com.gridnine.jtasks.common.core.model.domain.UserAccountIndex
import kotlin.reflect.KClass

class UserAccountLoginPropertyFindHandler: CacheConfiguration.CachedPropertyHandler<UserAccount>{
    override fun getIndexClass(): KClass<*> {
        return UserAccountIndex::class
    }

    override fun getPropertyName(): String {
        return UserAccountIndex.loginProperty.name
    }

    override fun getIdentityClass(): KClass<UserAccount> {
        return UserAccount::class
    }

    override fun getValue(obj: UserAccount): Any? {
        return obj.login
    }
}