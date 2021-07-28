/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: jtasks
 *****************************************************************/
package com.gridnine.jtasks.web.core.task

import com.gridnine.jasmine.common.core.model.CustomValueWidget
import com.gridnine.jasmine.web.core.ui.WebUiLibraryAdapter
import com.gridnine.jasmine.web.core.ui.components.BaseWebNodeWrapper
import com.gridnine.jasmine.web.core.ui.components.WebTag
import com.gridnine.jasmine.web.core.utils.MiscUtilsJS
import kotlinx.browser.document
import org.w3c.dom.Element

class WebHtmlContentViewer :
    CustomValueWidget<WebHtmlContentViewerVMJS, WebHtmlContentViewerVSJS, WebHtmlContentViewerVVJS>,
    BaseWebNodeWrapper<WebTag>() {
    private val id = "htmlViewer${MiscUtilsJS.createUUID()}"
    private var element: Element? = null
    private var content: String? = null
    private var initialized = false

    init {
        _node = WebUiLibraryAdapter.get().createTag("div", id)
        _node.getStyle().setParameters("width" to "100%")
        _node.getClass().addClasses("jtasks-html-viewer")
        _node.setPostRenderAction {
            if (!initialized) {
                if (element == null) {
                    element = document.getElementById(id)
                    element!!.asDynamic().innerHTML = content
                }
                initialized = true
            }
        }
    }

    override fun getData(): WebHtmlContentViewerVMJS {
        return WebHtmlContentViewerVMJS().also { it.content = content }
    }

    override fun readData(vm: WebHtmlContentViewerVMJS, vs: WebHtmlContentViewerVSJS?) {
        content = vm.content
        if (initialized) {
            element!!.asDynamic().innerHtml = content
        }
    }

    override fun setReadonly(value: Boolean) {
        //noops
    }

    override fun showValidation(vv: WebHtmlContentViewerVVJS?) {
        //noops
    }
}