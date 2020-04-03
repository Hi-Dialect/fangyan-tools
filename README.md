# （｡･∀･）ﾉﾞ嗨方言工具组

### 文件集成操作步骤
* 第 0 步: 需要移动的文件都在 [android](./android) 目录或其子目录下
* 第 1 步: 合并各种名字中带有 “gradle” 的文件，它们包括外部库文件信息和一些设置
* 第 2 步: 合并 [android/app/src/main/assets/](./android/app/src/main/assets) 目录下的所有文件，它们是前端的主要代码逻辑
* 第 3 步: 合并 [android/app/src/main/java/com/example/fangyan/](./android/app/src/main/java/com/example/fangyan) 目录下的所有文件，它们是后端的主要代码逻辑

### 合并小提示
1. 考虑到可能只有工具组有混合开发的内容，建议使用本仓库的 [android](./android) 文件夹作为基准文件夹
2. 在第四步完成后，你可能会发现 Activity 的名字会有冲突，请在 AndroidStudio 中修改它的名字
3. 目前只开发了一个 Activity，你可以使用 Intent 正常跳转，或者 Webview 链接到 [file:///android_asset/index.html](./android/app/src/main/assets/index.html)

### 开发进度
* 最近更新: 集成摄像头调用功能，诸多 BUG 修复
* 计划更新: 集成录音调用功能，前端录音 UI 模块开发
* 最新安卓 APK 下载: [传送门(点我点我)](http://49.235.190.178/file/fangyan.apk) 