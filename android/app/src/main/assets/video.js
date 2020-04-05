let forwardInterval = 0;
let rewindInterval = 0;

let focusElement = '';
let backgroundMusicPath = '';
let dialectPath = '';

window.onload = () => {
    //jquery不支持此事件绑定
    document.getElementById('video').ontimeupdate = handleTimeUpdate;

    $('#kuaitui').click(() => handleRewind());
    $('#bofang').click(() => handlePlay());
    $('#kuaijin').click(() => handleForward());
    $('#videoStart').change(() => handleCutStart());
    $('#videoEnd').change(() => handleCutEnd());
    $('#render').click(() => handleRender());
    $('#backToEdit').click(() => document.getElementById('outputVideo').src = '');

    $('#addBackgroundMusic').focus(() => focusElement = 'addBackgroundMusic');
    $('#addDialect').focus(() => focusElement = 'addDialect');
    $('#selectVideoFromLocal').click(() => android.selectFile(1));
    $('#takeNewVideo').click(() => android.selectFile(2));
    $('#selectAudioFromLocal').click(() => handleSelectAudioFromLocal());
    $('#recordNewAudio').click(() => handleRecordNewAudio());
}

function handleSelectAudioFromLocal() {
    if (focusElement == 'addBackgroundMusic') {
        android.selectFile(3);
    } else if (focusElement == 'addDialect') {
        android.selectFile(5);
    }
}

function handleRecordNewAudio() {
    if (focusElement == 'addBackgroundMusic') {
        android.selectFile(4);
    } else if (focusElement == 'addDialect') {
        android.selectFile(6);
    }
}

function handleTimeUpdate() {
    let video = document.getElementById('video');
    let play = document.getElementById('bofang');

    if (video.paused) {
        play.setAttribute('xlink:href', '#icon-bofang');
    } else {
        play.setAttribute('xlink:href', '#icon-zanting');
    }
}

function handleRender() {
    let video = document.getElementById('video');
    let startTime = document.getElementById('videoStart').value;
    let endTime = document.getElementById('videoEnd').value;
    let isMuted = document.getElementById('muted').classList.contains('mui-active');
    let isSaveToLocal = document.getElementById('localSave').classList.contains('mui-active');
    let localSaveName = document.getElementById('localSaveName').value + '.mp4';

    //注意参数传递和后端保持一致
    android.renderVideo(video.src, backgroundMusicPath, dialectPath,
        startTime, endTime, isMuted, isSaveToLocal, localSaveName);

    //开始渲染时暂停原始视频播放
    if (video.src != '' && !video.paused) {
        handlePlay();
    }
}

function handleCutStart() {
    let start = document.getElementById('videoStart');
    let end = document.getElementById('videoEnd');

    if (end.value - start.value < 1) {
        start.value = start.min;
        mui.alert('开始时间应小于结束时间', '警告');
    }
}

function handleCutEnd() {
    let start = document.getElementById('videoStart');
    let end = document.getElementById('videoEnd');

    if (end.value - start.value < 1) {
        end.value = end.max;
        mui.alert('结束时间应大于开始时间', '警告');
    }
}

function handleForward() {
    let video = document.getElementById('video');

    video.pause();
    clearInterval(forwardInterval);
    clearInterval(rewindInterval);

    // 按固定节奏控制视频进度
    forwardInterval = setInterval(() => {
        video.currentTime += 0.2;
        if (video.currentTime >= video.duration) {
            clearInterval(forwardInterval);
        }
    }, 50);
}

function handleRewind() {
    let video = document.getElementById('video');

    video.pause();
    clearInterval(forwardInterval);
    clearInterval(rewindInterval);

    // 按固定节奏控制视频进度
    rewindInterval = setInterval(() => {
        video.currentTime -= 0.2;
        if (video.currentTime <= 0) {
            clearInterval(rewindInterval);
        }
    }, 50);
}

function handlePlay() {
    let video = document.getElementById('video');
    let play = document.getElementById('bofang');

    clearInterval(forwardInterval);
    clearInterval(rewindInterval);

    if (video.paused) {
        video.play();
        play.setAttribute('xlink:href', '#icon-zanting');
    } else {
        video.pause();
        play.setAttribute('xlink:href', '#icon-bofang');
    }
}

//=========================以下函数为安卓调用JS=========================

function addVideo(filePath) {
    let video = document.getElementById('video');
    let videoStart = document.getElementById('videoStart');
    let videoEnd = document.getElementById('videoEnd');

    video.src = filePath;
    video.style.display = 'block';
    video.currentTime = 0.1;
    video.oncanplaythrough = () => {
        videoStart.max = video.duration;
        videoEnd.max = video.duration;
        videoEnd.value = video.duration;
    }
}

function addBackgroundMusic(filePath) {
    let backgroundMusicLabel = document.getElementById('backgroundMusicLabel');
    //let fileName = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length);

    backgroundMusicPath = filePath;
    backgroundMusicLabel.innerHTML = '已上传';
}

function addDialect(filePath) {
    let dialectLabel = document.getElementById('dialectLabel');
    //let fileName = filePath.substring(filePath.lastIndexOf('/') + 1, filePath.length);

    dialectPath = filePath;
    dialectLabel.innerHTML = '已上传';
}

function updateRenderBar(percentage, filePath) {
    let renderBar = document.getElementById('renderBar');
    let renderProgressLabel = document.getElementById('renderProgressLabel');
    let cancelRendering = document.getElementById('cancelRendering');
    let backToEdit = document.getElementById('backToEdit');
    let postNews = document.getElementById('postNews');
    let outputVideo = document.getElementById('outputVideo');
    let showRenderModal = document.getElementById('showRenderModal');

    renderBar.style.width = percentage + '%';
    if (percentage == '0') {
        renderProgressLabel.innerHTML = '视频渲染中...';
        cancelRendering.style.display = 'block';
        backToEdit.style.display = 'none';
        postNews.style.display = 'none';
        outputVideo.style.display = 'none';
        showRenderModal.click();
    } else if (percentage == '100') {
        renderProgressLabel.innerHTML = '视频渲染完成';
        cancelRendering.style.display = 'none';
        backToEdit.style.display = 'block';
        postNews.style.display = 'block';
        outputVideo.style.display = 'block';
        outputVideo.src = filePath;
        outputVideo.play();
    }
}

function alertError(message) {
    mui.alert(message, '提示');
}