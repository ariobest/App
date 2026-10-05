package com.ario.filemanager

import android.app.*
import android.os.*
import android.content.*
import android.graphics.Color
import android.net.Uri
import android.provider.Settings
import android.view.*
import android.widget.*
import java.io.File
import java.text.DecimalFormat

class MainActivity : Activity() {
    private lateinit var list: LinearLayout
    private lateinit var pathText: TextView
    private var current = EnvironmentRoot()
    private val blue = Color.rgb(77,124,255)
    private val bg = Color.rgb(7,10,18)
    private val card = Color.rgb(15,20,32)
    private val white = Color.rgb(235,240,255)
    private val muted = Color.rgb(145,155,180)

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        build()
        show(current)
    }

    private fun EnvironmentRoot(): File =
        android.os.Environment.getExternalStorageDirectory()

    private fun build() {
        val root=LinearLayout(this).apply {
            orientation=LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(16,20,16,8)
        }
        val top=LinearLayout(this).apply {
            gravity=Gravity.CENTER_VERTICAL
        }
        val logo=TextView(this).apply {
            text="ARIO"
            textSize=24f
            setTextColor(blue)
            typeface=android.graphics.Typeface.DEFAULT_BOLD
        }
        top.addView(logo, LinearLayout.LayoutParams(0,60,1f))
        val newBtn=Button(this).apply {
            text="+ NEW"; setTextColor(white); setBackgroundColor(card)
            setOnClickListener{newMenu()}
        }
        top.addView(newBtn, LinearLayout.LayoutParams(105,55))
        root.addView(top)

        pathText=TextView(this).apply {
            textSize=12f; setTextColor(muted); setPadding(4,2,4,12)
        }
        root.addView(pathText)

        val bar=LinearLayout(this)
        val back=Button(this).apply {
            text="‹"; setTextColor(white); setOnClickListener{
                current.parentFile?.let{show(it)}
            }
        }
        val refresh=Button(this).apply {
            text="↻"; setTextColor(white); setOnClickListener{show(current)}
        }
        bar.addView(back,LinearLayout.LayoutParams(0,50,1f))
        bar.addView(refresh,LinearLayout.LayoutParams(0,50,1f))
        root.addView(bar)

        val scroll=ScrollView(this)
        list=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
        scroll.addView(list)
        root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f))
        setContentView(root)
    }

    private fun show(dir: File) {
        if (!dir.exists() || !dir.isDirectory) return
        current=dir
        pathText.text="> "+dir.absolutePath
        list.removeAllViews()
        val items=dir.listFiles()?.sortedWith(compareBy<File>{!it.isDirectory}.thenBy{it.name.lowercase()}) ?: emptyList()
        if(items.isEmpty()){
            list.addView(TextView(this).apply{
                text="\n  [ EMPTY DIRECTORY ]"; textSize=14f; setTextColor(muted)
            })
            return
        }
        items.forEach { f ->
            val row=LinearLayout(this).apply{
                orientation=LinearLayout.VERTICAL
                setPadding(14,10,14,10)
                setBackgroundColor(card)
                setOnClickListener{
                    if(f.isDirectory) show(f) else preview(f)
                }
                setOnLongClickListener{ itemMenu(f); true }
            }
            val title=TextView(this).apply{
                text=(if(f.isDirectory)"▸ " else "  ")+f.name
                textSize=15f; setTextColor(white)
            }
            val sub=TextView(this).apply{
                text=if(f.isDirectory)"DIRECTORY" else size(f.length())
                textSize=11f; setTextColor(muted)
            }
            row.addView(title); row.addView(sub)
            val lp=LinearLayout.LayoutParams(-1,LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0,0,0,5); list.addView(row,lp)
        }
    }

    private fun newMenu(){
        val names=arrayOf("New folder","New text file")
        AlertDialog.Builder(this).setTitle("ARIO // CREATE").setItems(names){_,which->
            val e=EditText(this); e.hint=if(which==0)"Folder name" else "File name.txt"
            AlertDialog.Builder(this).setTitle("Name").setView(e).setPositiveButton("CREATE"){_,_->
                val n=e.text.toString().trim()
                if(n.isNotEmpty()) try {
                    if(which==0) File(current,n).mkdirs()
                    else File(current,n).createNewFile()
                    show(current)
                }catch(_:Exception){}
            }.setNegativeButton("CANCEL",null).show()
        }.setNegativeButton("CLOSE",null).show()
    }

    private fun itemMenu(f:File){
        AlertDialog.Builder(this).setTitle(f.name)
            .setItems(arrayOf("Rename","Delete","Share")){_,w->
                when(w){
                    0->rename(f);1->delete(f);2->share(f)
                }
            }.show()
    }

    private fun rename(f:File){
        val e=EditText(this); e.setText(f.name)
        AlertDialog.Builder(this).setTitle("RENAME").setView(e)
            .setPositiveButton("SAVE"){_,_->f.renameTo(File(f.parent,e.text.toString()));show(current)}
            .setNegativeButton("CANCEL",null).show()
    }

    private fun delete(f:File){
        AlertDialog.Builder(this).setTitle("DELETE?")
            .setMessage("Delete ${f.name}?")
            .setPositiveButton("DELETE"){_,_->f.deleteRecursively();show(current)}
            .setNegativeButton("CANCEL",null).show()
    }

    private fun share(f:File){
        val i=Intent(Intent.ACTION_SEND).apply{type="*/*";putExtra(Intent.EXTRA_STREAM,Uri.fromFile(f))}
        startActivity(Intent.createChooser(i,"Share"))
    }

    private fun preview(f:File){
        if(f.length()>1024*1024){ Toast.makeText(this,"File too large to preview",Toast.LENGTH_SHORT).show();return }
        try{
            val text=f.readText()
            AlertDialog.Builder(this).setTitle(f.name)
                .setMessage(text).setPositiveButton("CLOSE",null).show()
        }catch(e:Exception){
            Toast.makeText(this,"No text preview available",Toast.LENGTH_SHORT).show()
        }
    }

    private fun size(n:Long):String{
        if(n<1024)return "$n B"
        if(n<1024*1024)return "${n/1024} KB"
        return DecimalFormat("0.0").format(n/1024.0/1024.0)+" MB"
    }
}
