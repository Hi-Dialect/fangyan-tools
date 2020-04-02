let speedRate = 5.0; //播放倍速
let forwardInterval = 0;
let rewindInterval = 0;

let backgroundMusicPath = '';
let dialectPath = '';

window.onload = () => {
    $('video').currentTime = 0.1;
    $('kuaitui').onclick = handleRewindClick;
    $('bofang').onclick = handlePlay;
    $('kuaijin').onclick = handleForwardClick;
    $('videoStart').onchange = handleCutStart;
    $('videoEnd').onchange = handleCutEnd;
    $('backgroundMusicButton').onclick = () => android.selectFile(1);
    $('dialectButton').onclick = () => android.selectFile(2);
    $('uploadVideoFromLocal').onclick = () => android.selectFile(3);
    $('render').onclick = handleRender;
    //$('alert').innerHTML = android.hello('试试就试试');
}

function handleRender() {
    mui($('render')).button('loading');
    setTimeout(function () {
        mui($('render')).button('reset');
    }.bind(this), 200);

    let startTime = $('videoStart').value;
    let endTime = $('videoEnd').value;
    let isMuted = document.getElementById('muted').classList.contains('mui-active');

    android.renderVideo($('video').src, startTime, endTime, isMuted, backgroundMusicPath, dialectPath);
}

function handleCutStart() {
    let start = $('videoStart').value;
    let end = $('videoEnd').value;

    if (end - start < 1) {
        $('videoStart').value = 0;
        mui.alert('开始时间应小于结束时间', '警告');
    }
}

function handleCutEnd() {
    let start = $('videoStart').value;
    let end = $('videoEnd').value;

    if (end - start < 1) {
        $('videoEnd').value = 60;
        mui.alert('结束时间应大于开始时间', '警告');
    }
}

function handleForwardClick() {
    let video = $('video');
    let play = $('bofang');

    video.pause();
    play.setAttribute('xlink:href', '#icon-zanting');
    clearInterval(forwardInterval);
    clearInterval(rewindInterval);

    // 按固定节奏控制视频进度
    forwardInterval = setInterval(() => {
        video.currentTime += 0.2;
        if (video.currentTime >= video.duration) {
            play.setAttribute('xlink:href', '#icon-bofang');
            clearInterval(forwardInterval);
        }
    }, 40);
}

function handleRewindClick() {
    let video = $('video');
    let play = $('bofang');

    video.pause();
    play.setAttribute('xlink:href', '#icon-zanting');
    clearInterval(forwardInterval);
    clearInterval(rewindInterval);

    // 按固定节奏控制视频进度
    rewindInterval = setInterval(() => {
        video.currentTime -= 0.2;
        if (video.currentTime <= 0) {
            play.setAttribute('xlink:href', '#icon-bofang');
            clearInterval(rewindInterval);
        }
    }, 40);
}

function handlePlay() {
    let video = $('video');
    let play = $('bofang');

    clearInterval(forwardInterval);
    clearInterval(rewindInterval);

    // 判断前一个状态是否为快进快退，如是则暂停视频播放
    if (play.getAttribute('xlink:href') == '#icon-zanting' && video.paused) {
        play.setAttribute('xlink:href', '#icon-bofang');
        return;
    }

    if (video.paused) {
        video.play();
        play.setAttribute('xlink:href', '#icon-zanting');
    } else {
        video.pause();
        play.setAttribute('xlink:href', '#icon-bofang');
    }
}

//=========================调用摄像头、录音（娄）=========================

var cmr = null;

// 初始化
mui.init();
// 扩展API加载完毕后调用onPlusReady回调函数
document.addEventListener("plusready", onPlusReady, false);

// 扩展API加载完毕，现在可以正常调用扩展API
function onPlusReady() {
    console.log("plusready");
    r = plus.audio.getRecorder();
}

// 摄像，实现本地摄像头调用并保存到本地
function videoCapture() {
    cmr = plus.camera.getCamera();//获取摄像头对象
    var res = cmr.supportedVideoResolutions[0];//分辨率
    var fmt = cmr.supportedVideoFormats[0];//文件格式
    console.log("Resolution: " + res + ", Format: " + fmt);
    cmr.startVideoCapture(function (path) {
        alert("Capture video success: " + path);
    },
        function (error) {
            alert("Capture video failed: " + error.message);
        },
        { resolution: res, format: fmt }
    );
    // 此处设置拍摄10s后自动完成
    setTimeout(stopCapture, 10000);
}

// 停止摄像
function stopCapture() {
    console.log("stopCapture");
    cmr.stopVideoCapture();
}

//开始录音，完毕后结束录音，之后自动播放
function startRecord() {
    if (!r) {
        plus.nativeUI.toast('录音设备准备中!');
        return;
    }
    r.record(
        { filename: "_doc/audio/" },
        function (recordFile) {
            console.log(recordFile);
            var player = plus.audio.createPlayer(recordFile);
            player.play();
            //利用 recordFile 结合上传知识可以完成音频文件的上传
        },
        function (e) {
            console.log("Audio record failed: " + e.message);
        }
    );
}

function stopRecord() {
    //r.stop(); 
    console.log(JSON.stringify(r));
    if (r) { r.stop(); }
}

//=========================以下函数为安卓调用JS=========================

function addBackgroundMusic(filePath) {
    let fileName = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length);

    backgroundMusicPath = filePath;
    $('backgroundMusicLabel').innerHTML = fileName;
}

function addDialect(filePath) {
    let fileName = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length);

    dialectPath = filePath;
    $('dialectLabel').innerHTML = fileName;
}

function addVideo(filePath) {
    $('video').src = filePath;
    $('video').style.display = 'block';
}

function updateRenderBar(percentage, filePath) {
    $('renderBar').style.width = percentage + '%';

    if (percentage == '0') {
        $('renderProgressLabel').innerHTML = '视频渲染中...';
        $('cancelRendering').style.display = 'block';
        $('outputVideo').style.display = 'none';
        $('renderBar').style.width = '0%';
        $('showModal').click();
    } else if (percentage == '100') {
        $('renderProgressLabel').innerHTML = '视频渲染完成';
        $('cancelRendering').style.display = 'none';
        $('outputVideo').src = filePath;
        $('outputVideo').style.display = 'block';
    }
}

function alertError(message) {
    mui.alert(message, '提示');
}