let speedRate = 5.0; //播放倍速
let forwardInterval = 0;
let rewindInterval = 0;

let backgroundMusicPath = '';
let dialectPath = '';

window.onload = () => {
    $('video').currentTime = 0.1;
    $('rewind').onclick = handleRewindClick;
    $('play').onclick = handlePlay;
    $('forward').onclick = handleForwardClick;
    //$('muted').onclick = handleMuted;
    $('videoStart').onchange = handleCutStart;
    $('videoEnd').onchange = handleCutEnd;
    $('backgroundMusicButton').onclick = () => android.command(1);
    $('dialectButton').onclick = () => android.command(2);
    $('uploadVideoFromLocal').onclick = () => android.command(3);
    $('render').onclick = handleRender;
    $('alert').innerHTML = android.hello('试试就试试');
}

function handleRender() {
    mui($('render')).button('loading');
    setTimeout(function () {
        mui($('render')).button('reset');
    }.bind(this), 2000);
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
    let play = $('play');

    video.pause();
    play.removeClassName('icon-play');
    play.addClassName('icon-pause');
    clearInterval(forwardInterval);
    clearInterval(rewindInterval);

    // 按固定节奏控制视频进度
    forwardInterval = setInterval(() => {
        video.currentTime += 0.2;
        if (video.currentTime >= video.duration) {
            play.removeClassName('icon-pause');
            play.addClassName('icon-play');
            clearInterval(forwardInterval);
        }
    }, 40);
}

function handleRewindClick() {
    let video = $('video');
    let play = $('play');

    video.pause();
    play.removeClassName('icon-play');
    play.addClassName('icon-pause');
    clearInterval(forwardInterval);
    clearInterval(rewindInterval);

    // 按固定节奏控制视频进度
    rewindInterval = setInterval(() => {
        video.currentTime -= 0.2;
        if (video.currentTime <= 0) {
            play.removeClassName('icon-pause');
            play.addClassName('icon-play');
            clearInterval(rewindInterval);
        }
    }, 40);
}

function handlePlay() {
    let video = $('video');
    let play = $('play');

    clearInterval(forwardInterval);
    clearInterval(rewindInterval);

    // 判断前一个状态是否为快进快退，如是则暂停视频播放
    if (play.hasClassName('icon-pause') && video.paused) {
        play.removeClassName('icon-pause');
        play.addClassName('icon-play');
        return;
    }

    if (video.paused) {
        video.play();
        play.removeClassName('icon-play');
        play.addClassName('icon-pause');
    } else {
        video.pause();
        play.removeClassName('icon-pause');
        play.addClassName('icon-play');
    }
}

function handleMuted() {
    if ($('muted').hasClassName('mui-active')) {
        $('video').muted = true;
    } else {
        $('video').muted = false;
    }
}

//=================================================================

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
}