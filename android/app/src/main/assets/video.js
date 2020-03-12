let speedRate = 5.0; //播放倍速
let forwardInterval = 0;
let rewindInterval = 0;

window.onload = () => {
    $('rewind').onclick = handleRewindClick;
    $('play').onclick = handlePlay;
    $('forward').onclick = handleForwardClick;
    $('muted').onclick = handleMuted;
    $('alert').innerHTML = android.hello('试试就试试');
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

    // 判断前一个状态是否为快进快退
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