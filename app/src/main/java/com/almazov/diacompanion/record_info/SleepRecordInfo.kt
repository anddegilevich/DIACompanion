package com.almazov.diacompanion.record_info

import android.app.AlertDialog
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.almazov.diacompanion.R
import com.almazov.diacompanion.data.AppDatabaseViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.model.GradientColor
import kotlinx.android.synthetic.main.fragment_sleep_record_info.*
import kotlin.math.exp
import kotlin.math.pow


class SleepRecordInfo : Fragment() {

    private val args by navArgs<SleepRecordInfoArgs>()
    private lateinit var appDatabaseViewModel: AppDatabaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sleep_record_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        appDatabaseViewModel = ViewModelProvider(this)[AppDatabaseViewModel::class.java]
        appDatabaseViewModel.readSleepRecord(args.selectedRecord.id).observe(viewLifecycleOwner, Observer { record ->

            tv_duration.text = record.duration.toString()
            sleepLineChartSetup(record.duration!!)

        })

        date.text = args.selectedRecord.date
        time.text = args.selectedRecord.time

        btn_edit.setOnClickListener{
            val action = SleepRecordInfoDirections.actionSleepRecordInfoToSleepAddRecord(args.selectedRecord)
            findNavController().navigate(action)
        }

        btn_delete.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setPositiveButton(this.resources.getString(R.string.Yes)) {_, _ ->
                appDatabaseViewModel.deleteSleepRecord(args.selectedRecord.id)
                args.selectedRecord.let { appDatabaseViewModel.deleteRecord(it) }
                findNavController().popBackStack()
            }
            builder.setNegativeButton(this.resources.getString(R.string.No)) {_, _ ->
            }
            builder.setTitle(this.resources.getString(R.string.DeleteRecord))
            builder.setMessage(this.resources.getString(R.string.AreUSureDeleteRecord))
            builder.create().show()
        }

        super.onViewCreated(view, savedInstanceState)

    }

    private fun sleepLineChartSetup(duration: Double) {
        val lineEntries = ArrayList<Entry>()

        val m = 6f
        val s = 2f
        val step = 0.1f
        val xys = createNormalDistribution(m, s, step)

        for (xy in xys) lineEntries.add(Entry(xy.first, xy.second))

        val firstColor = ContextCompat.getColor(requireContext(), R.color.pink_dark)
//        val transparentColor = ContextCompat.getColor(requireContext(), R.color.transparent)
        val lineDataSet = LineDataSet(lineEntries,"")
        lineDataSet.apply {
            axisDependency = YAxis.AxisDependency.LEFT
            lineWidth = 5f
            color = firstColor
            highLightColor = firstColor
            highlightLineWidth = 2f
            setDrawHorizontalHighlightIndicator(false)
            setDrawValues(false)
            setDrawCircles(false)
//            fillAlpha = 255
//            fillColor = firstColor
            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_pink)
            setDrawFilled(true)
        }
        val lineData = LineData(lineDataSet)
        line_chart.apply {
            description.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                axisLineColor = firstColor
                isGranularityEnabled = true
                granularity = 1.0f
                textColor = firstColor
                textSize = 16f
                setDrawGridLines(false)
            }
            axisRight.isEnabled = false
            axisLeft.isEnabled = false
            legend.isEnabled = false

            animateX(1000)
            data = lineData
            highlightValue(duration.toFloat(),0)
            isHighlightPerDragEnabled = false
        }

    }

    private fun createNormalDistribution(m: Float, s: Float, step: Float): MutableList<Pair<Float, Float>> {
        var xMin = m - 3 * s
        val xMax = m + 3 * s
        val xy = mutableListOf<Pair<Float,Float>>()
        while (xMin < xMax) {
            val yCurrent = exp((-0.5f) * ((xMin-m)/s).pow(2)) /
                    (s * (2f * 3.14f).pow(1/2))
            xy.add(Pair(xMin,yCurrent))
            xMin += step
        }
        return xy
    }

}