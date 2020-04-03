let speedRate = 5.0; //播放倍速
let forwardInterval = 0;
let rewindInterval = 0;

let backgroundMusicPath = '';
let dialectPath = '';

window.onload = () => {
    $('video').ontimeupdate = handleTimeUpdate;
    $('kuaitui').onclick = handleRewindClick;
    $('bofang').onclick = handlePlay;
    $('kuaijin').onclick = handleForwardClick;
    $('videoStart').onchange = handleCutStart;
    $('videoEnd').onchange = handleCutEnd;
    $('backgroundMusicButton').onclick = () => android.selectFile(1);
    $('dialectButton').onclick = () => android.selectFile(2);
    $('uploadVideoFromLocal').onclick = () => android.selectFile(3);
    $('takeNewVideo').onclick = () => android.selectFile(4);
    $('render').onclick = handleRender;
    $('backToEdit').onclick = () => $('outputVideo').src = '';
}

function handleTimeUpdate() {
    if ($('video').paused) {
        $('bofang').setAttribute('xlink:href', '#icon-bofang');
    } else {
        $('bofang').setAttribute('xlink:href', '#icon-zanting');
    }
}

function handleRender() {
    let startTime = $('videoStart').value;
    let endTime = $('videoEnd').value;
    let isMuted = document.getElementById('muted').classList.contains('mui-active');

    android.renderVideo($('video').src, startTime, endTime, isMuted, backgroundMusicPath, dialectPath);
    if ($('video').src != '' && !$('video').paused) {
        handlePlay();
    }
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

//=========================以下函数为安卓调用JS=========================

function addBackgroundMusic(filePath) {
    let fileName = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length);

    backgroundMusicPath = filePath;
    $('backgroundMusicLabel').innerHTML = '已上传';
}

function addDialect(filePath) {
    let fileName = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length);

    dialectPath = filePath;
    $('dialectLabel').innerHTML = '已上传';
}

function addVideo(filePath) {
    $('video').src = filePath;
    $('video').style.display = 'block';
    $('video').currentTime = 0.1;
}

function updateRenderBar(percentage, filePath) {
    $('renderBar').style.width = percentage + '%';

    if (percentage == '0') {
        $('renderProgressLabel').innerHTML = '视频渲染中...';
        $('cancelRendering').style.display = 'block';
        $('backToEdit').style.display = 'none';
        $('postNews').style.display = 'none';
        $('outputVideo').style.display = 'none';
        $('showModal').click();
    } else if (percentage == '100') {
        $('renderProgressLabel').innerHTML = '视频渲染完成';
        $('cancelRendering').style.display = 'none';
        $('backToEdit').style.display = 'block';
        $('postNews').style.display = 'block';
        $('outputVideo').style.display = 'block';
        $('outputVideo').src = filePath;
        $('outputVideo').play();
    }
}

function alertError(message) {
    mui.alert(message, '提示');
}