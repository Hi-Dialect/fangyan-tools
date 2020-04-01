# （｡･∀･）ﾉﾞ嗨方言工具组

### 文件集成操作步骤
* 第 0 步: 需要移动的文件都在 [android](./android) 目录下
* 第 1 步: 合并各种名字中带有“gradle”的文件，它们包括外部库文件信息和一些设置
* 第 2 步: 合并 [android/app/src/main/assets/] (./android/app/src/assets)目录下的所有文件，它们是前段的主要代码逻辑
* 第 4 步: 合并 [android/app/src/main/java/com/example/fangyan/] (./android/app/src/main/java/com/example/fangyan)目录下的所有文件，它们是后端的主要代码逻辑

> 在这一步完成后，你可能会发现 Activity 的名称会有冲突，请修改它的名字（建议在 AndroidStudio 中修改，不易出错）
> 目前只开发了一个 Activity，你可以使用 Intent 正常跳转
> 你也可以通过 Webview 导航，只需要连接到这个地址：file:///android_asset/index.html