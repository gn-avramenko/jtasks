/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: JTasks
 *****************************************************************/
package com.gridnine.jtasks.server.core.activator

import com.gridnine.jasmine.common.core.app.Environment
import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.common.core.meta.WebPluginsAssociationsRegistry
import com.gridnine.jasmine.common.core.storage.Storage
import com.gridnine.jasmine.server.core.storage.StorageRegistry
import com.gridnine.jasmine.server.core.utils.DigestUtils
import com.gridnine.jasmine.server.core.web.WebAppFilter
import com.gridnine.jasmine.server.core.web.WebApplication
import com.gridnine.jasmine.server.core.web.WebServerConfig
import com.gridnine.jasmine.server.standard.model.WorkspaceProvider
import com.gridnine.jasmine.server.standard.rest.ExceptionFilter
import com.gridnine.jasmine.server.standard.rest.KotlinFileDevFilter
import com.gridnine.jtasks.common.core.model.domain.UserAccount
import com.gridnine.jtasks.common.core.model.domain.UserAccountIndex
import com.gridnine.jtasks.server.core.storage.JTasksWorkspaceProvider
import com.gridnine.jtasks.server.core.storage.UserAccountIndexHandler
import com.gridnine.jtasks.server.core.web.JTasksAuthFilter
import java.io.File
import java.util.*

class JTasksServerCoreActivator:IPluginActivator {
    override fun configure(config: Properties) {
        addApp("","jtasks-index","lib/jtasks-index")
        WebPluginsAssociationsRegistry.get().links["com.gridnine.jasmine.web.core"] = "/jasmine-core/com.gridnine.jasmine.web.core.js"
        WebPluginsAssociationsRegistry.get().links["com.gridnine.jasmine.web.easyui"] = "/jasmine-easyui/com.gridnine.jasmine.web.easyui.js"
        WebPluginsAssociationsRegistry.get().links["com.gridnine.jasmine.web.standard"] = "/jasmine-standard/com.gridnine.jasmine.web.standard.js"
        WebPluginsAssociationsRegistry.get().links["com.gridnine.jasmine.web.reports"] = "/jasmine-reports/com.gridnine.jasmine.web.reports.js"
        addApp("/jasmine-core","jasmine-core","lib/jasmine-core.war")
        addApp("/jasmine-easyui","jasmine-easyui","lib/jasmine-easyui.war")
        addApp("/jasmine-standard","jasmine-standard","lib/jasmine-standard.war")
        addApp("/jasmine-reports","jasmine-reports","lib/jasmine-reports.war")
        addApp("/easyui-lib","easyui-lib","lib/easyui-lib.war")
        addApp("/select2-lib","select2-lib","lib/select2-lib.war")
        addApp("/easyui-adapter","easyui-adapter","lib/easyui-adapter.war")
        addApp("/jquery-lib","jquery-lib","lib/jquery-lib.war")
        addApp("/jtasks-core","jtasks-core","lib/jtasks-core.war")
        WebServerConfig.get().globalFilters.add(WebAppFilter("kotlin-dev-filter", KotlinFileDevFilter::class))
        WebServerConfig.get().globalFilters.add(WebAppFilter("exception-filter", ExceptionFilter::class))
        WebServerConfig.get().globalFilters.add(WebAppFilter("auth-filter", JTasksAuthFilter::class))
        StorageRegistry.get().register(UserAccountIndexHandler())
        Environment.publish(WorkspaceProvider::class, JTasksWorkspaceProvider())
    }
    private fun addApp(context: String, res: String, file: String) {
        val resource = javaClass.classLoader.getResource(res)
        if(resource != null){
            WebServerConfig.get().addApplication(WebApplication(context, resource, javaClass.classLoader))
            return
        }
        val resourceFile = File(file)
        if(resourceFile.exists()){
            WebServerConfig.get().addApplication(WebApplication(context, resourceFile.toURI().toURL(), javaClass.classLoader))
        }
    }

    override fun activate(config: Properties) {
        val adminProfile = Storage.get().findUniqueDocument(UserAccountIndex::class, UserAccountIndex.loginProperty, "admin")
        if(adminProfile != null){
            return
        }
        val account = UserAccount()
        account.login = "admin"
        account.passwordDigest = DigestUtils.getMd5Hash("admin")
        account.name= "Администратор"
        Storage.get().saveDocument(account, true, "setup")
    }
}