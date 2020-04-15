//快进定时器（重复）
let forwardInterval = 0;
//快退定时器（重复）
let rewindInterval = 0;
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

window.onload = () => {
    //Jquery不支持此事件绑定
    document.getElementById('video').ontimeupdate = handleTimeUpdate;
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
        onChange: (data) => {
            let cutDuration = document.getElementById('cutDuration');

            cutStart = Math.round(data.from * 10) / 10;
            cutEnd = Math.round(data.to * 10) / 10;
            cutDuration.value = Math.round((cutEnd - cutStart) * 10) / 10 + ' s';
        }
    });

    $('#kuaitui').click(() => handleRewind());
    $('#bofang').click(() => handlePlay());
    $('#kuaijin').click(() => handleForward());
    $('#videoStart').change(() => handleCutStart());
    $('#videoEnd').change(() => handleCutEnd());
    $('#render').click(() => handleRender());
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

function handleSelectAudioFromLocal() {
    mui('#popover').popover('hide');
    if (focusElement == 'addBackgroundMusic') {
        android.selectFile(3);
    } else if (focusElement == 'addDialect') {
        android.selectFile(5);
    }
}

function handleRecordNewAudio() {
    mui('#popover').popover('hide');
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
    let isMuted = document.getElementById('muted').classList.contains('mui-active');
    let isSaveToLocal = document.getElementById('localSave').classList.contains('mui-active');
    let localSaveName = document.getElementById('localSaveName').value + '.mp4';

    //注意参数传递和后端保持一致
    android.renderVideo(video.src, backgroundMusicPath, dialectPath,
        cutStart, cutEnd, isMuted, isSaveToLocal, localSaveName);

    //开始渲染时暂停原始视频播放
    if (video.src != '' && !video.paused) {
        handlePlay();
    }
}

function handleForward() {
    let video = document.getElementById('video');

    video.pause();
    clearInterval(forwardInterval);
    clearInterval(rewindInterval);

    //按固定节奏控制视频进度
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

    //按固定节奏控制视频进度
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

    video.src = filePath;
    video.style.display = 'block';
    video.currentTime = 0.1;
    video.oncanplaythrough = () => {
        let my_range = $('.js-range-slider').data('ionRangeSlider');
        let cutDuration = document.getElementById('cutDuration');

        my_range.update({
            min: 0,
            max: Math.round(video.duration * 10) / 10,
            from: 0,
            to: Math.round(video.duration * 10) / 10,
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

function alertError(message) {
    mui.alert(message, '提示');
}