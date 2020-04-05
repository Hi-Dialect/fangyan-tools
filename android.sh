#!/bin/bash  

echo "选择要执行的命令: "
echo "1. 移动 h5 内容至 assets 目录下"
echo "2. 启动 android studio"
echo "3. 传输 apk 至云服务器"

read -p "请输入序号: " number

#if条件语句内前后需要空格
if [ $number -eq 1 ]
then 
    sudo cp -r  index.html     android/app/src/main/assets
    sudo cp -r  frame.css      android/app/src/main/assets
    sudo cp -r  video.js       android/app/src/main/assets
    sudo cp -r  jquery.min.js  android/app/src/main/assets
    sudo cp -r  popper.min.js  android/app/src/main/assets
    sudo cp -r  bootstrap      android/app/src/main/assets
    sudo cp -r  mui-master     android/app/src/main/assets
    sudo cp -r  range-slider   android/app/src/main/assets
elif [ $number -eq 2 ]
then
    sh /home/yuwen/AndroidStudio/bin/studio.sh &
elif [ $number -eq 3 ]
then
    scp android/app/build/outputs/apk/debug/app-debug.apk root@49.235.190.178:/var/www/html/file
else
    echo "输入有误"
fi