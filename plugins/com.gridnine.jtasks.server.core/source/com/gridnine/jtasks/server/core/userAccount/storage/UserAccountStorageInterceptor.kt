/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.server.core.userAccount.storage

import com.gridnine.jasmine.common.core.model.BaseDocument
import com.gridnine.jasmine.common.core.model.L10nMessage
import com.gridnine.jasmine.common.core.model.Xeption
import com.gridnine.jasmine.common.core.utils.AuthUtils
import com.gridnine.jasmine.server.core.storage.OperationContext
import com.gridnine.jasmine.server.core.storage.StorageInterceptor
import com.gridnine.jtasks.common.core.model.domain.UserAccount

class UserAccountStorageInterceptor() :StorageInterceptor{
    override val priority: Double = 1.0

    override fun <D : BaseDocument> onDelete(doc: D, context: OperationContext<D>) {
        if(doc is UserAccount){
            if(doc.login == "admin"){
                throw Xeption.forEndUser(L10nMessage("Нельзя удалить учетную запись администратора"))
            }
            if(doc.login == AuthUtils.getCurrentUser()){
                throw Xeption.forEndUser(L10nMessage("Нельзя удалить учетную запись текущего пользователя"))
            }
        }

    }

}