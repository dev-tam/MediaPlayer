package net.torora.jtam.mediaplayer

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.song_ticket.view.*

class MainActivity : AppCompatActivity() {

    var listSongs = ArrayList<SongInfo>()
    var adapter: MySongAdapter? = null
    var mp: MediaPlayer? = null // instance of media player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        LoadURLOnLine()
        checkUserPermission()


        var myTracking = mySongTrack()
        myTracking.start()
    }

//    fun LoadURLOnLine() {
//        listSongs.add(SongInfo("Didi", "Honey", "http://192.168.10.77:8888/public/data2/Music/Didi.mp3"))
//        listSongs.add(SongInfo("Nostalgia", "Hanine", "http://192.168.10.77:8888/public/data2/Music/HanineEl.mp3"))
//        listSongs.add(SongInfo("Yalla", "INNA", "http://192.168.10.77:8888/public/data2/Music/Yalla.mp3"))
//        listSongs.add(SongInfo("2 Steps", "Unknown", "http://192.168.10.77:8888/public/data2/Music/2steps.mp3"))
//        listSongs.add(SongInfo("Ghosttown", "Madonna", "http://192.168.10.77:8888/public/data2/Music/Ghosttown.mp3"))
//    }

    inner class MySongAdapter : BaseAdapter {
        var MyListSong = ArrayList<SongInfo>()

        constructor(MyListSong: ArrayList<SongInfo>) : super() {
            this.MyListSong = MyListSong
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val myView = layoutInflater.inflate(R.layout.song_ticket, null)
            val Song = this.MyListSong[position]
            myView.tvSongName.text = Song.Title
            myView.tvAuthor.text = Song.AuthorNAme
            myView.buPlay.setOnClickListener(View.OnClickListener {

                if (myView.buPlay.text.equals("Stop")) {
                    mp!!.stop()
                    myView.buPlay.text = "Play"
                } else {

                    mp = MediaPlayer()
                    try {
                        mp!!.setDataSource(Song.SongURL)
                        mp!!.prepare()
                        mp!!.start()
                        myView.buPlay.text = "Stop"
                        sbProgress.max = mp!!.duration
                    } catch (ex: Exception) {
                    }
                }
            })
            return myView
        }

        override fun getItem(position: Int): Any {
            return this.MyListSong[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return this.MyListSong.size
        }

    }

    inner class mySongTrack() : Thread() {
        override fun run() {
            while (true) {
                try {
                    Thread.sleep(1000)
                } catch (ex: Exception) {
                }
                runOnUiThread {
                    if (mp != null) {
                        sbProgress.progress = mp!!.currentPosition
                    }
                }
            }
        }
    }

    fun checkUserPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_CODE_ASK_PERMISSIONS)
                return
            }
        }
        LoadSong()
    }
    // get access to location permission
    val REQUEST_CODE_ASK_PERMISSIONS = 123

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_CODE_ASK_PERMISSIONS -> if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
                LoadSong()
            }else{
                // permission denied
                Toast.makeText(this, "denial",Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
    fun LoadSong(){
        val allSongsURI=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection=MediaStore.Audio.Media.IS_MUSIC+"!=0"
        val cursor=contentResolver.query(allSongsURI,null,selection,null,null)
        if (cursor!=null){
            if (cursor!!.moveToFirst()){
                do{
                    val songURL=cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val songAuthor=cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val songName=cursor!!.getString(cursor!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                    listSongs.add(SongInfo(songName, songAuthor, songURL))
                }while (cursor!!.moveToNext())
            }
        }
        cursor!!.close()
        adapter = MySongAdapter(listSongs)
        lvListSongs.adapter = adapter
    }
}
