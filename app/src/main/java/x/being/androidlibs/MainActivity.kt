package x.being.androidlibs

import android.content.DialogInterface
import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import x.being.androidlibs.model.JsonResult
import x.being.androidlibs.model.Status
import x.being.androidlibs.widget.base.BaseActivity
import x.being.androidlibs.widget.initToolbar
import x.being.androidlibs.widget.safeHttpCallback
import x.being.lib_databinding_tool.BindingTool
import x.being.lib_loading_view.LoadingView
import x.being.lib_net.RequestWrapper
import x.being.lib_net.http
import x.being.lib_net.post

class MainActivity : BaseActivity() {
    private val bind by lazy { BindingTool(layoutInflater, root) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar(subtitle = "this is a demo")
        bind.addShow("Input your info", "Demo")
        bind.addInputText("k1", "Name", "Linx", true)
        bind.addInputText("k2", "Nickname", "Linx", isShow = false)
        bind.addInputNumber("k3", "Age", 27)
        bind.addInputDecimal("k4", "Weight", 50.0)
        bind.addInputSingleSelect("k5", "Have nickname?", arrayOf("Yes", "No"), 1, "", false){
            bind.setVisibility("k2", it == "Yes")
        }

        btn_post.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Confirm this")
                .setMessage("Name: ${bind.getValue("k1")} \n" +
                        "Nickname: ${bind.getValue("k2")} \n" +
                        "Age: ${bind.getValue("k3")} \n" +
                        "Weight: ${bind.getValue("k4")} \n")
                .setPositiveButton("POST", DialogInterface.OnClickListener { dialog, which ->
                    post()
                })
                .setNegativeButton("CANCEL", null)
                .show()
        }
    }

    private fun post(){
        val loadingView = LoadingView()
        "http://api.com".http()
            .param("name", bind.getValue("k1"))
//            .param(Model(bind.getValue("k1")))  //Appliction/json
//            .params(mapOf("name" to bind.getValue("k1"), "age" to bind.getValue("k3")))
            //more...
            .post<JsonResult<User>>(safeHttpCallback(
                onStart = {
                    loadingView.show(supportFragmentManager, null)
                },
                onFinish = {
                    loadingView.dismiss()
                },
                onSuccess = { rs ->
                    ToastUtils.showShort(rs?.message)
                    if (rs?.status == Status.OK) {
                        //TODO
                    }
                }
            ))
    }

    data class Model(
        var name : String? = null
    ) : RequestWrapper.JsonObjectParam

    data class User(
        var name : String? = null,
        var token : String? = null
    )
}
