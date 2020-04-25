//标记选择音频文件的方式
let focusElement = '';
//上传的背景音乐的路径
let backgroundMusicPath = '';
//上传的方言配音的路径
let dialectPath = '';
//剪辑的开始时间
let cutStart = 0;
//剪辑的结束时间
let cutEnd = 0;
//是否已经开始录音
let recordingStart = false;

window.onload = () => {
    //视频响应事件绑定
    document.getElementById('video').ontimeupdate = () =>
        handleTimeUpdate('video', 'bofang');
    document.getElementById('recordingVideo').ontimeupdate = () =>
        handleTimeUpdate('recordingVideo', 'recordingPlay');
    //绘制视频帧当做剪辑背景
    document.getElementById('video').onloadeddata = handleVideoLoadData;
    //初始化滑块，用于剪辑范围的选取
    $('.js-range-slider').ionRangeSlider({
        skin: 'big',
        type: 'double',
        min: 0,
        max: 60,
        from: 0,
        to: 60,
        step: 0.1,
        hide_min_max: true,
        onChange: handleCutDurationChange
    });
    //绑定其余响应函数
    $('#bofang').click(() => handlePlay('video'));
    $('#recordingPlay').click(() => handlePlay('recordingVideo'));
    $('#recordingCancel').click(() => handleRecordingCancel());
    $('#recordingCheck').click(() => handleRecordingCheck());
    $('#render').click(() => handleRender());
    $('#cutDuration').focus(() => $('#cutDuration').blur());
    $('#backToEdit').click(() => document.getElementById('outputVideo').src = '');
    $('#addBackgroundMusic').focus(() => focusElement = 'addBackgroundMusic');
    $('#addBackgroundMusic').click(() => mui('#popover').popover('toggle'));
    $('#addDialect').focus(() => focusElement = 'addDialect');
    $('#addDialect').click(() => mui('#popover').popover('toggle'));
    $('#selectVideoFromLocal').click(() => android.selectFile(1));
    $('#takeNewVideo').click(() => android.selectFile(2));
    $('#selectAudioFromLocal').click(() => handleSelectAudioFromLocal());
    $('#recordNewAudio').click(() => handleRecordNewAudio());
}

//从本地选择音频文件，区分背景音乐和配音
function handleSelectAudioFromLocal() {
    mui('#popover').popover('hide');
    if (focusElement == 'addBackgroundMusic') {
        android.selectFile(3);
    } else if (focusElement == 'addDialect') {
        android.selectFile(5);
    }
}

//录制新的音频，唤醒录音模块
function handleRecordNewAudio() {
    mui('#popover').popover('hide');
    $('#recording').modal('show');
}

//视频加载完成后初始化剪辑模块
function handleVideoLoadData() {
    let video = document.getElementById("recordingVideo");
    let frames = document.getElementById('frames');
    let frameNumber = 10;

    video.currentTime = 0;
    frames.innerHTML = null;
    for (let i = 1; i <= frameNumber; i++) {
        setTimeout(() => {
            let canvas = document.createElement('canvas');
            let context = canvas.getContext("2d");

            canvas.style.height = '8vh';
            canvas.style.width = 100 / frameNumber + '%';
            frames.appendChild(canvas);
            context.drawImage(video, 0, 0, canvas.width, canvas.height);
            video.currentTime = (i != 10 ? video.duration / frameNumber * i : 0);
        }, i * 100);
    }
}

//动态更新剪辑时间
function handleCutDurationChange(data) {
    let cutDuration = document.getElementById('cutDuration');

    cutStart = Math.round(data.from * 10) / 10;
    cutEnd = Math.round(data.to * 10) / 10;
    cutDuration.value = Math.round((cutEnd - cutStart) * 10) / 10 + ' s';
}

//根据视频的播放状态更新内容
function handleTimeUpdate(videoId, buttonId) {
    let video = document.getElementById(videoId);
    let play = document.getElementById(buttonId);

    if (video.paused) {
        play.setAttribute('xlink:href', '#icon-bofang');
    } else if (videoId == 'video') {
        play.setAttribute('xlink:href', '#icon-zanting');
    } else if (videoId == 'recordingVideo') {
        play.setAttribute('xlink:href', '#icon-tingzhi');
        $('#recordingBar').css('width', video.currentTime / video.duration * 100 + '%');
        $('#recordingTimeLeft').html(Math.round(video.duration - video.currentTime));
    }
}

//调用后端视频渲染功能
function handleRender() {
    let video = document.getElementById('video');
    let isMuted = document.getElementById('muted').classList.contains('mui-active');
    let isSaveToLocal = document.getElementById('localSave').classList.contains('mui-active');
    let localSaveName = document.getElementById('localSaveName').value + '.mp4';

    //注意参数传递和后端保持一致
    android.renderVideo(video.src, backgroundMusicPath, dialectPath,
        cutStart, cutEnd, isMuted, isSaveToLocal, localSaveName);

    //开始渲染时暂停原始视频播放
    if (video.src != '' && !video.paused) {
        handlePlay('video');
    }
}

//处理不同模块的视频播放事件
function handlePlay(id) {
    let video = document.getElementById(id);

    //切换播放和暂停状态
    video.paused ? video.play() : video.pause();
    //如果是录音模块的控件，则关联到后端录制音频
    if (id == 'recordingVideo') {
        if (recordingStart) {
            android.stopRecord(-1);
        } else {
            android.startRecord();
            recordingStart = true;
        }
    }
}

//取消录音
function handleRecordingCancel() {
    $('#recording').modal('hide');
    recordingStart = false;
    android.stopRecord(0);
}

//录音完成调用后端生成相关文件
function handleRecordingCheck() {
    $('#recording').modal('hide');
    recordingStart = false;
    if (focusElement == 'addBackgroundMusic') {
        android.stopRecord(1);
    } else if (focusElement == 'addDialect') {
        android.stopRecord(2);
    }
}

//=========================以下函数为安卓调用JS=========================

function addVideo(filePath) {
    let video = document.getElementById('video');
    let recordingVideo = document.getElementById('recordingVideo');

    video.src = filePath;
    recordingVideo.src = filePath;
    recordingVideo.oncanplaythrough = () => {
        let my_range = $('.js-range-slider').data('ionRangeSlider');
        let cutDuration = document.getElementById('cutDuration');

        cutEnd = Math.round(video.duration * 10) / 10;
        my_range.update({
            min: 0,
            max: cutEnd,
            from: 0,
            to: cutEnd,
        });

        my_range.reset();
        cutDuration.value = Math.round(video.duration * 10) / 10 + ' s';
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

//后端的错误信息传递到前端
function alertError(message) {
    mui.alert(message, '提示');
}