package com.gridnine.jtasks.common.core.activator

import com.gridnine.jasmine.common.core.app.IPluginActivator
import com.gridnine.jasmine.common.core.meta.DomainMetaRegistry
import com.gridnine.jasmine.common.core.meta.RestMetaRegistry
import com.gridnine.jasmine.common.core.meta.UiMetaRegistry
import com.gridnine.jasmine.common.core.meta.WebPluginsAssociationsRegistry
import com.gridnine.jasmine.common.core.parser.DomainMetadataParser
import com.gridnine.jasmine.common.core.parser.RestMetadataParser
import com.gridnine.jasmine.common.core.parser.UiMetadataParser
import com.gridnine.jtasks.common.core.WebPluginsAssociations
import java.util.*

class JTasksCoreCommonActivator:IPluginActivator {
    override fun configure(config: Properties) {
        DomainMetadataParser.updateDomainMetaRegistry(DomainMetaRegistry.get(), "com/gridnine/jtasks/common/core/model/jtasks-core-model-domain.xml", javaClass.classLoader)
        RestMetadataParser.updateRestMetaRegistry(RestMetaRegistry.get(), "com/gridnine/jtasks/common/core/model/jtasks-core-model-rest.xml", javaClass.classLoader)
        UiMetadataParser.updateUiMetaRegistry(UiMetaRegistry.get(), "com/gridnine/jtasks/common/core/model/jtasks-core-model-ui.xml", javaClass.classLoader)
        WebPluginsAssociations.registerAssociations()
    }
}