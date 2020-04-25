#!/bin/bash  

echo "选择要执行的命令: "
echo "1. 移动 h5 内容至 assets 目录下"
echo "2. 启动 android studio"
echo "3. 传输 apk 至云服务器"
echo "4. 解锁 android 模拟器"

read -p "请输入序号: " number

#if条件语句内前后需要空格
if [ $number -eq 1 ]
then 
    sudo cp -r  tools.html     android/app/src/main/assets
    sudo cp -r  tools.css      android/app/src/main/assets
    sudo cp -r  tools.js       android/app/src/main/assets
    sudo cp -r  toolkit        android/app/src/main/assets
elif [ $number -eq 2 ]
then
    sh /home/yuwen/AndroidStudio/bin/studio.sh &
elif [ $number -eq 3 ]
then
    scp android/app/build/outputs/apk/debug/app-debug.apk root@49.235.190.178:/var/www/html/file
elif [ $number -eq 4 ]
then
    sudo chown yuwen -R /dev/kvm
else
    echo "输入有误"
fi