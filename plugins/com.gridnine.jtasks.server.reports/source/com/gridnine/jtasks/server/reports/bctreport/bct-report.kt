/*****************************************************************
 * Gridnine AB http://www.gridnine.com
 * Project: Jasmine
 *****************************************************************/

package com.gridnine.jtasks.server.reports.bctreport

import com.gridnine.jasmine.common.core.storage.*
import com.gridnine.jasmine.common.reports.model.domain.*
import com.gridnine.jasmine.common.reports.model.misc.*
import com.gridnine.jasmine.common.standard.model.l10n.StandardL10nMessagesFactory
import com.gridnine.jasmine.server.reports.builders.report
import com.gridnine.jasmine.server.reports.model.*
import com.gridnine.jasmine.server.reports.model.EndDateReportRequestedParameterHandler
import com.gridnine.jtasks.common.core.model.domain.Project
import com.gridnine.jtasks.common.core.model.domain.TimerRecord
import com.gridnine.jtasks.server.reports.model.JTasksReportParametersIds
import com.gridnine.jtasks.server.reports.model.JTasksReportType

object BctProjectsReportRequestedParameterHandler : BaseObjectReferencesReportRequestedParameterHandler<Project>(
    JTasksReportParametersIds.PROJECTS,
    Project::class
)

class BctReportServerHandler : BaseSeverReportHandler(JTasksReportType.BASIC_COMMITTED_TIME_REPORT) {

    override fun getRequestedParametersDescriptions(): List<ReportRequestedParameterDescription> {
        return arrayListOf(
            StartDateReportRequestedParameterHandler,
            EndDateReportRequestedParameterHandler,
            BctProjectsReportRequestedParameterHandler
        ).map { it.createParameterDescription() }
    }

    override fun generateReport(parameters: List<BaseReportRequestedParameter>): ReportGenerationResult {
        val query = searchQuery {
            where {
                StartDateReportRequestedParameterHandler.getValue(parameters)?.let {
                    ge(TimerRecord.dateProperty, it)
                }
                EndDateReportRequestedParameterHandler.getValue(parameters)?.let {
                    le(TimerRecord.dateProperty, it)
                }
                val projects = BctProjectsReportRequestedParameterHandler.getValues(parameters)
                if (projects.isNotEmpty()) {
                    isIn(TimerRecord.projectProperty, projects)
                }
            }
        }
        val batches = arrayListOf<BctReportBatch>()
        Storage.get().searchAssets(TimerRecord::class, query).forEach { record ->
            val projectCode = record.project.toString()
            val batch = batches.find { it.project == projectCode } ?: run {
                val hBatch = BctReportBatch(projectCode)
                batches.add(hBatch)
                hBatch
            }
            val taskCode = "${record.task}/${record.taskName}"
            val entry = batch.entries.find { it.task == taskCode } ?: run {
                val hEntry = BctReportEntry(taskCode)
                batch.entries.add(hEntry)
                hEntry
            }
            entry.committedTime += record.committedTime!!
        }
        batches.sortBy { it.project }
        batches.forEach {batch ->
            batch.entries.sortBy { it.task }
        }
        val report = report {
            fileName = "basic-report.xlsx"
            val titleStyle = style {
                fontBold = true
                horizontalAlignment = GeneratedReportCellHorizontalAlignment.CENTER
                fontHeight = 20
            }
            val parameterNameStyle = style {
                fontBold = true
                fontHeight = 14
                horizontalAlignment = GeneratedReportCellHorizontalAlignment.RIGHT
            }
            val parameterValueDateStyle = style {
                fontHeight = 14
                horizontalAlignment = GeneratedReportCellHorizontalAlignment.LEFT
                format = "yyyy.MM.dd"
            }
            val parameterValueRefStyle = style {
                fontHeight = 14
                horizontalAlignment = GeneratedReportCellHorizontalAlignment.LEFT
            }
            val headerStyle = style {
                fontHeight = 12
                horizontalAlignment = GeneratedReportCellHorizontalAlignment.CENTER
                verticalAlignment = GeneratedReportCellVerticalAlignment.CENTER
                bottomBorderWidth = GeneratedReportCellBorderWidth.THICK
                topBorderWidth = GeneratedReportCellBorderWidth.THICK
                leftBorderWidth = GeneratedReportCellBorderWidth.THICK
                rightBorderWidth = GeneratedReportCellBorderWidth.THICK
                foregroundColor = GeneratedReportColor.GREY_25_PERCENT
                wrapText = true
            }
            val userDataStyle = style {
                fontHeight = 12
                horizontalAlignment = GeneratedReportCellHorizontalAlignment.CENTER
                verticalAlignment = GeneratedReportCellVerticalAlignment.CENTER
                bottomBorderWidth = GeneratedReportCellBorderWidth.THIN
                topBorderWidth = GeneratedReportCellBorderWidth.THIN
                leftBorderWidth = GeneratedReportCellBorderWidth.THIN
                rightBorderWidth = GeneratedReportCellBorderWidth.THIN
            }

            val stringDataStyle = style {
                parentStyle = userDataStyle
            }
            val timeDataStyle = style {
                parentStyle = userDataStyle
                horizontalAlignment = GeneratedReportCellHorizontalAlignment.RIGHT
            }
            val totalTextStyle = style {
                parentStyle = userDataStyle
                horizontalAlignment = GeneratedReportCellHorizontalAlignment.RIGHT
                fontBold = true
            }
            list {
                defaultRowHeight = 20
                title = "Отчет"
                columns {
                    column(30)
                    column(80)
                    column(20)
                }
                row(25)
                text("Отчет затрат по проектам", titleStyle, 3, 1)
                                row(25)
                row()
                text("Дата начала:", parameterNameStyle)
                date(StartDateReportRequestedParameterHandler.getValue(parameters), parameterValueDateStyle)
                row()
                text("Дата окончания:", parameterNameStyle)
                date(EndDateReportRequestedParameterHandler.getValue(parameters), parameterValueDateStyle)
                row()
                text("Проекты:", parameterNameStyle)
                val projects = BctProjectsReportRequestedParameterHandler.getValues(parameters)
                text(if(projects.isEmpty()) "Все" else projects.joinToString { it.caption!! }, parameterValueRefStyle)
                row()
                row(25)
                text("Проект", headerStyle)
                text("Задача", headerStyle)
                text("Потраченное время", headerStyle)
                batches.forEach { batch ->
                    batch.entries.withIndex().forEach { (index, entry) ->
                        row()
                        text(batch.project, stringDataStyle, 1, if(index == 0) batch.entries.size+1 else 1)
                        text(entry.task, stringDataStyle)
                        text(formatTime(entry.committedTime), timeDataStyle)
                    }
                    row()
                    text(batch.project, stringDataStyle)
                    text("Итого", totalTextStyle)
                    text(formatTime(batch.entries.map { it.committedTime }.reduce{acc, i ->  acc+i}), totalTextStyle)
                }
                row()
                text("Итого", totalTextStyle, 2,1)
                text("", totalTextStyle)
                text(formatTime(batches.flatMap { it.entries }.map { it.committedTime }.reduce{acc, i ->  acc+i}), totalTextStyle)
            }
        }
        return ReportGenerationResult().also { it.report = report }
    }

    private fun formatTime(committedTime: Int): String? {
        var remainder = committedTime
        val hours = remainder/3600
        val hoursString = if(hours < 10) "0${hours}" else hours.toString()
        remainder -= hours * 3600
        val minutes = remainder/60
        val minutesString = if(minutes < 10) "0${minutes}" else minutes.toString()
        remainder -= minutes * 60
        val seconds = remainder
        val secondsString = if(seconds < 10) "0${seconds}" else seconds.toString()
        return "${hoursString}:${minutesString}:${secondsString}"
    }
//            { it.entries.sortBy { entry -> entry.dateValue } }
//        val report = report {

//            val userDataStyle = style {
//                fontHeight = 12
//                horizontalAlignment = GeneratedReportCellHorizontalAlignment.CENTER
//                verticalAlignment = GeneratedReportCellVerticalAlignment.CENTER
//                bottomBorderWidth = GeneratedReportCellBorderWidth.THIN
//                topBorderWidth = GeneratedReportCellBorderWidth.THIN
//                leftBorderWidth = GeneratedReportCellBorderWidth.THIN
//                rightBorderWidth = GeneratedReportCellBorderWidth.THIN
//            }
//
//            val stringDataStyle = style {
//                parentStyle = userDataStyle
//            }
//            val dateDataStyle = style {
//                parentStyle = userDataStyle
//                format = "yyyy.MM.dd"
//            }
//            val numberDataStyle = style {
//                parentStyle = userDataStyle
//                horizontalAlignment = GeneratedReportCellHorizontalAlignment.RIGHT
//                format = "0.#"
//            }
//            val totalTextStyle = style {
//                parentStyle = userDataStyle
//                horizontalAlignment = GeneratedReportCellHorizontalAlignment.RIGHT
//                fontBold = true
//            }
//            val totalNumberStyle = style {
//                parentStyle = numberDataStyle
//                horizontalAlignment = GeneratedReportCellHorizontalAlignment.RIGHT
//                fontBold = true
//            }
//            list {
//                defaultRowHeight = 20
//                title = "лист 1"
//                columns {
//                    column(30)
//                    column(20)
//                    column(20)
//                    column(20)
//                }
//                row(25)
//                text("Демонстрационный отчет", titleStyle, 4, 1)
//                row(25)
//                row()
//                text("Дата начала:", parameterNameStyle)
//                date(StartDateReportRequestedParameterHandler.getValue(parameters), parameterValueDateStyle)
//                row()
//                text("Дата окончания:", parameterNameStyle)
//                date(EndDateReportRequestedParameterHandler.getValue(parameters), parameterValueDateStyle)
//                row()
//                text("Пользователь:", parameterNameStyle)
//                text(DemoUserReportRequestedParameterHandler.getValue(parameters)?.caption, parameterValueRefStyle)
//                row()
//                row(25)
//                text("Пользователь", headerStyle)
//                text("Перечисление", headerStyle)
//                text("Дата", headerStyle)
//                text("Число", headerStyle)
//                if (lst.isNotEmpty()) {
//                    var totalNumber = BigDecimal.ZERO
//                    val subTotalIndexes = arrayListOf<Int>()
//
//                    lst.forEach { batch ->
//                        val batchStartRow = getCurrentRowIndex() + 1
//                        var batchTotalNumber: BigDecimal = 0.toBigDecimal()
//                        batch.entries.withIndex().forEach { (index, entry) ->
//                            row()
//                            text(batch.userName, stringDataStyle, 1, if (index == 0) batch.entries.size + 1 else 1)
//                            text(entry.enumValue, stringDataStyle)
//                            date(entry.dateValue, dateDataStyle)
//                            number(entry.numberValue, numberDataStyle)
//                            batchTotalNumber = batchTotalNumber.add(entry.numberValue ?: 0.toBigDecimal())
//                        }
//                        row()
//                        emptyCell()
//                        text("Итого", totalTextStyle, 2, 1)
//                        emptyCell()
//                        numberFormula("SUM(D${batchStartRow + 1}:D${getCurrentRowIndex()})", batchTotalNumber, totalNumberStyle)
//                        subTotalIndexes.add(getCurrentRowIndex())
//                        totalNumber = totalNumber.add(batchTotalNumber)
//                    }
//                    row()
//                    text("Итого", totalTextStyle, 3, 1)
//                    emptyCell()
//                    emptyCell()
//                    numberFormula("SUM(${subTotalIndexes.joinToString(",") { "D$it" }})", totalNumber, totalNumberStyle)
//                }
//            }
//        }
//        return ReportGenerationResult().let {
//            it.report = report
//            it
//        }
//    }
//
    override fun validate(params: List<BaseReportRequestedParameter>): Map<String, String?>? {
        StartDateReportRequestedParameterHandler.getValue(params)?: return mapOf(StartDateReportRequestedParameterHandler.getId() to StandardL10nMessagesFactory.Empty_field())
        return null
    }
}

class BctReportEntry(val task: String?, var committedTime:Int = 0)
class BctReportBatch(val project:String, val entries:MutableList<BctReportEntry> = arrayListOf())
