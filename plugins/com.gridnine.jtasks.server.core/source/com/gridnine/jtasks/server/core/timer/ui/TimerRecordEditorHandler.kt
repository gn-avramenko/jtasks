/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jtasks.server.core.timer.ui

import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.standard.helpers.ObjectEditorHandler
import com.gridnine.jtasks.common.core.model.domain.TimerRecord
import com.gridnine.jtasks.common.core.model.domain.TimerRecordEditorVM
import com.gridnine.jtasks.common.core.model.domain.TimerRecordEditorVS
import com.gridnine.jtasks.common.core.model.domain.TimerRecordEditorVV
import kotlin.reflect.KClass

class TimerRecordEditorHandler :
    ObjectEditorHandler<TimerRecord, TimerRecordEditorVM, TimerRecordEditorVS, TimerRecordEditorVV> {
    override fun getObjectClass(): KClass<TimerRecord> {
        return TimerRecord::class
    }

    override fun getVMClass(): KClass<TimerRecordEditorVM> {
        return TimerRecordEditorVM::class
    }

    override fun getVSClass(): KClass<TimerRecordEditorVS> {
        return TimerRecordEditorVS::class
    }

    override fun getVVClass(): KClass<TimerRecordEditorVV> {
        return TimerRecordEditorVV::class
    }

    override fun fillSettings(
        entity: TimerRecord,
        vsEntity: TimerRecordEditorVS,
        vmEntity: TimerRecordEditorVM,
        ctx: MutableMap<String, Any?>
    ) {
        //noops
    }

    override fun read(entity: TimerRecord, vmEntity: TimerRecordEditorVM, ctx: MutableMap<String, Any?>) {
        vmEntity.date = entity.date
        vmEntity.lastStarted = entity.lastStarted
        vmEntity.status = entity.status
        vmEntity.task = entity.task
        entity.committedTime?.let {
            var remainder = it
            val hours = remainder / 3600
            remainder -= hours * 3600
            val minutes = remainder / 60
            remainder -= minutes * 60
            vmEntity.committedTime = "${formatTimePart(hours)}:${formatTimePart(minutes)}:${formatTimePart(remainder)}"
        }
    }

    private fun formatTimePart(value: Int): String {
        if (value >= 10) {
            return value.toString()
        }
        return "0${value}"
    }

    override fun getTitle(
        entity: TimerRecord,
        vmEntity: TimerRecordEditorVM,
        vsEntity: TimerRecordEditorVS,
        ctx: MutableMap<String, Any?>
    ): String? {
        return entity.task!!.caption
    }

    override fun write(entity: TimerRecord, vmEntity: TimerRecordEditorVM, ctx: MutableMap<String, Any?>) {
        entity.task = vmEntity.task
        entity.committedTime = parseTime(vmEntity.committedTime)
    }

    private fun parseTime(committedTime: String?): Int? {
        if (committedTime == null) {
            return null
        }
        val hoursString = committedTime.substringBefore(":")
        val minutesAndSeconds = committedTime.substringAfter(":")
        val minutesString = minutesAndSeconds.substringBefore(":")
        val secondsString = minutesAndSeconds.substringAfter(":")
        return 3600 * hoursString.toInt() + 60 * minutesString.toInt() + secondsString.toInt()
    }

    override fun validate(vmEntity: TimerRecordEditorVM, vvEntity: TimerRecordEditorVV, ctx: MutableMap<String, Any?>) {
        if (vmEntity.task == null) {
            vvEntity.task = StandardL10nMessagesFactory.Empty_field()
        }
        try {
            parseTime(vmEntity.committedTime)
        } catch (t: Throwable) {
            vvEntity.committedTime = "Некорректный формат"
        }
    }
}