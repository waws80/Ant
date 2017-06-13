package pw.androidthanatos.main

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import pw.androidthanatos.ant.Ant
import pw.androidthanatos.ant.antImg
import pw.androidthanatos.ant.antJSON

class MainActivity : AppCompatActivity() {

    private lateinit var tv:TextView

    private val path: String = "https://www.qigeairen.com/server_api/images/activity/lunbo_1.png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv = findViewById(R.id.tv) as TextView

        val pro = findViewById(R.id.pro) as TextView

        val iv = findViewById(R.id.iv) as ImageView
        val iv1 = findViewById(R.id.iv1) as ImageView

        val pb = findViewById(R.id.pb) as TextView
        val pb1 = findViewById(R.id.pb1) as TextView
        Ant.init(this.applicationContext)
        Ant.openDebug()


        antImg {
            url = path
            target = iv
            progress {
                p -> pb.text = if (p == -1) "100%" else "$p%"
            }
            complate {
                toast("iv 下载完成")
                pb.visibility= View.GONE
            }
        }

        antImg {
            url = "https://www.qigeairen.com/server_api/images/activity/lunbo_4.png"
            target = iv1
            progress {
                p -> pb1.text = if (p == -1) "100%" else "$p%"
            }
            complate {
                toast("iv1 下载完成")
                pb1.visibility= View.GONE
            }
        }

        val header = HashMap<String,String>()
        header.put("token","")

        antJSON {
            url = "https://www.baidu.com"
            cache = false
            headers = header
            progress {
                t -> pro.text = "进度：${if(t == -1) 100 else t}"
            }

           complateJson {
               s -> tv.text= s.toString()
           }
            error {
                i -> log("错误码： $i")
                tv.text= "错误码： $i"
            }
        }
    }
}
fun log(msg: String , tag: String ="thanatos"){
    Log.w(tag, msg)
}

fun Context.toast(msg: String){
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}
