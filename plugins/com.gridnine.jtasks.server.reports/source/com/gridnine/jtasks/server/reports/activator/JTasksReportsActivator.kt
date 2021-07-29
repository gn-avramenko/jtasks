/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.server.reports.activator

import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.common.core.app.Registry
import com.gridnine.jasmine.common.core.meta.MiscMetaRegistry
import com.gridnine.jasmine.common.core.parser.MiscMetadataParser
import com.gridnine.jtasks.server.reports.bctreport.BctReportServerHandler
import java.util.*

class JTasksReportsActivator:IPluginActivator {
    override fun configure(config: Properties) {
        MiscMetadataParser.updateMiscMetaRegistry(MiscMetaRegistry.get(), "com/gridnine/jtasks/server/reports/model/jtasks-reports-misc.xml", javaClass.classLoader)
        Registry.get().register(BctReportServerHandler())
    }
}