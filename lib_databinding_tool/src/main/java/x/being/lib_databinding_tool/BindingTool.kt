@file:Suppress("unused")

package x.being.lib_databinding_tool

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import x.being.lib_databinding_tool.databinding.ViewInputTextBinding
import x.being.lib_databinding_tool.databinding.ViewShowTextBinding
import java.text.SimpleDateFormat
import java.util.*


class BindingTool(private var layoutInflater: LayoutInflater, private var rootView: ViewGroup) {
    private val variableId = BR.model
    private val map = hashMapOf<String, DataBindEntity>()

    fun addInputText(
        key: String,
        hint: String,
        value: String? = null,
        notNull: Boolean = false,
        isShow: Boolean = true
    ) {
        val model = DataBindEntity(hint, value)
        val binding = createView(InputType.INPUT_TEXT).apply {
            setVariable(variableId, model)
        }
        binding.root.tag = key
        binding.root.findViewById<View>(R.id.iv_not_null).visibility =
            if (notNull) View.VISIBLE else View.GONE
        binding.root.setOnClickListener { v ->
            safeClick(v) {
                v.findViewById<View>(R.id.et).showSoftInput()
            }
        }
        rootView.addView(binding.root)
        binding.root.visibility = if (isShow) View.VISIBLE else View.GONE
        map[key] = model
    }

    fun addInputTextPassword(
        key: String,
        hint: String,
        value: String? = null,
        notNull: Boolean = false,
        isShow: Boolean = true
    ) {
        val model = DataBindEntity(hint, value)
        val binding = createView(InputType.INPUT_TEXT_PASSWORD).apply {
            setVariable(variableId, model)
        }
        binding.root.tag = key
        binding.root.findViewById<View>(R.id.iv_not_null).visibility =
            if (notNull) View.VISIBLE else View.GONE
        binding.root.setOnClickListener { v ->
            safeClick(v) {
                v.findViewById<View>(R.id.et).showSoftInput()
            }
        }
        rootView.addView(binding.root)
        binding.root.visibility = if (isShow) View.VISIBLE else View.GONE
        map[key] = model
    }

    fun addInputNumber(key: String, hint: String, value: Int? = null, notNull: Boolean = false) {
        val model = DataBindEntity(hint, value?.toString())
        val binding = createView(InputType.INPUT_NUMBER).apply {
            setVariable(variableId, model)
        }
        binding.root.tag = key
        binding.root.findViewById<View>(R.id.iv_not_null).visibility =
            if (notNull) View.VISIBLE else View.GONE
        binding.root.setOnClickListener { v ->
            safeClick(v) {
                v.findViewById<View>(R.id.et).showSoftInput()
            }
        }
        rootView.addView(binding.root)
        map[key] = model
    }

    fun addInputDecimal(
        key: String,
        hint: String,
        value: Double? = null,
        notNull: Boolean = false,
        isShow: Boolean = true
    ) {
        val model = DataBindEntity(hint, value?.toString())
        val binding = createView(InputType.INPUT_DECIMAL).apply {
            setVariable(variableId, model)
        }
        binding.root.tag = key
        binding.root.findViewById<View>(R.id.iv_not_null).visibility =
            if (notNull) View.VISIBLE else View.GONE
        binding.root.setOnClickListener { v ->
            safeClick(v) {
                v.findViewById<View>(R.id.et).showSoftInput()
            }
        }
        rootView.addView(binding.root)
        binding.root.visibility = if (isShow) View.VISIBLE else View.GONE
        map[key] = model
    }

    fun addInputSingleSelect(
        key: String,
        hint: String,
        showData: Array<String>,
        defaultSelect: Int,
        emptyText: String,
        notNull: Boolean = false,
        onChangeListener: (String) -> Unit = {}
    ) {
        val model = DataBindEntity(hint, showData[defaultSelect])
        val binding = createView(InputType.INPUT_SINGLE_SELECT).apply {
            setVariable(variableId, model)
        }
        binding.root.tag = key
        binding.root.findViewById<View>(R.id.iv_not_null).visibility =
            if (notNull) View.VISIBLE else View.GONE
        binding.root.setOnClickListener { v ->
            safeClick(v) {
                rootView.hideSoftInput()
                addFieldSingleSelect(
                    v.findViewById(R.id.tv),
                    hint,
                    showData,
                    defaultSelect,
                    emptyText,
                    onChangeListener
                )
            }
        }
        rootView.addView(binding.root)
        map[key] = model
    }

    fun addInputDate(
        key: String,
        hint: String,
        value: String? = null,
        notNull: Boolean = false,
        isShow: Boolean = true
    ) {
        val model = DataBindEntity(hint, value)
        val binding = createView(InputType.INPUT_DATE).apply {
            setVariable(variableId, model)
        }

        val tv = binding.root.findViewById<TextView>(R.id.tv)
        tv.text = value

        binding.root.tag = key
        binding.root.findViewById<View>(R.id.iv_not_null).visibility =
            if (notNull) View.VISIBLE else View.GONE
        binding.root.setOnClickListener { v ->
            safeClick(v) {
                rootView.hideSoftInput()
                showDatePicker(key, tv)
            }
        }
        rootView.addView(binding.root)
        binding.root.visibility = if (isShow) View.VISIBLE else View.GONE
        map[key] = model
    }

    fun addShow(hint: String, value: String?, @LayoutRes res: Int? = null) {
        val model = DataBindEntity(hint, value)
        val binding = createView(InputType.SHOW_TEXT, res).apply {
            setVariable(variableId, model)
        }
        rootView.addView(binding.root)
    }

    private fun addFieldSingleSelect(
        tv: TextView,
        hint: String,
        showData: Array<String>,
        defaultSelect: Int,
        emptyText: String,
        onChangeListener: (String) -> Unit = {}
    ) {
        if (showData.isEmpty()) {
            Toast.makeText(rootView.context, emptyText, Toast.LENGTH_SHORT).show()
            return
        }
        val index = showData.indexOf(tv.text.toString())
        var select = if (index == -1) defaultSelect else index
        MaterialAlertDialogBuilder(rootView.context)
            .setTitle(hint)
            .setSingleChoiceItems(showData, select) { _, which ->
                select = which
            }
            .setPositiveButton("确定") { _, _ ->
                val s = showData[select]
                tv.text = s
                onChangeListener(s)
            }
            .show()
    }

    private fun createView(type: InputType, @LayoutRes res: Int? = null) = when (type) {
        InputType.INPUT_TEXT -> {
            DataBindingUtil.inflate<ViewInputTextBinding>(
                layoutInflater,
                R.layout.view_input_text,
                rootView,
                false
            )
        }
        InputType.INPUT_TEXT_PASSWORD -> {
            DataBindingUtil.inflate<ViewInputTextBinding>(
                layoutInflater,
                R.layout.view_input_text_password,
                rootView,
                false
            )
        }
        InputType.INPUT_NUMBER -> {
            DataBindingUtil.inflate<ViewInputTextBinding>(
                layoutInflater,
                R.layout.view_input_number,
                rootView,
                false
            )
        }
        InputType.INPUT_DECIMAL -> {
            DataBindingUtil.inflate<ViewInputTextBinding>(
                layoutInflater,
                R.layout.view_input_decimal,
                rootView,
                false
            )
        }
        InputType.INPUT_DATE, InputType.INPUT_SINGLE_SELECT -> {
            DataBindingUtil.inflate<ViewShowTextBinding>(
                layoutInflater,
                R.layout.view_input_link,
                rootView,
                false
            )
        }
        InputType.SHOW_TEXT -> {
            DataBindingUtil.inflate<ViewShowTextBinding>(
                layoutInflater,
                res ?: R.layout.view_show_text,
                rootView,
                false
            )
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDatePicker(key: String, tv: TextView) {
        val time: Calendar
        if (tv.text.toString().isNotEmpty()) {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val instance = Calendar.getInstance()
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            instance.time = sdf.parse(tv.text.toString())
            time = instance
        } else {
            time = Calendar.getInstance()
        }

        DatePickerDialog(
            tv.context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val m = month + 1
                val mStr = if (m < 10) "0$m" else "$m"
                val dStr = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                val format = String.format("$year-$mStr-$dStr")
                tv.text = format
                map[key]?.text = format
            },
            time.get(Calendar.YEAR),
            time.get(Calendar.MONTH),
            time.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun setValue(key: String, value: String) {
        map[key]?.text = value
    }

    fun getValue(key: String): String? {
        return map[key]?.text
    }

    fun setVisibility(key: String, isShow: Boolean) {
        rootView.findViewWithTag<View>(key).visibility = if (isShow) View.VISIBLE else View.GONE
    }


    /**
     * 显示软键盘
     */
    private fun View.showSoftInput() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        postDelayed({
            requestFocus()
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
        }, 200)
    }

    /**
     * 隐藏软键盘
     */
    private fun View.hideSoftInput() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private val safeClickMap = WeakHashMap<Any, Any>()

    private fun safeClick(tag: View, action: () -> Unit) {
        synchronized(safeClickMap) {
            if (!safeClickMap.containsKey(tag)) {
                safeClickMap[tag] = action
                action()
                tag.postDelayed({
                    safeClickMap.remove(tag)
                }, 400)
            }
        }
    }

    enum class InputType {
        SHOW_TEXT,
        INPUT_TEXT,
        INPUT_TEXT_PASSWORD,
        INPUT_NUMBER,
        INPUT_DECIMAL,
        INPUT_DATE,
        INPUT_SINGLE_SELECT
    }

}
