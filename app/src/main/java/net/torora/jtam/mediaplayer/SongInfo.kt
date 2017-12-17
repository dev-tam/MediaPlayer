package net.torora.jtam.mediaplayer

/**
 * Created by jtam on 12/17/17.
 */

class SongInfo{
    var Title:String?=null
    var AuthorNAme:String?=null
    var SongURL:String?=null
    constructor(Title:String,AuthorNAme:String,SongURL:String){
        this.Title=Title
        this.AuthorNAme=AuthorNAme
        this.SongURL=SongURL
    }
}
