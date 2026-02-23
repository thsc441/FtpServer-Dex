# FtpServer-Dex
安卓可用的Ftp远程服务器

> 我使用`Aide Pro`构建的

## 使用教程

1. 使用`Aide Pro`或`Aide`构建项目，或者前往[Releases](./releases)下载`classes.dex`
    > 使用`Aide Pro`或`Aide`构建出的需要进行处理[处理方法](### 处理方法)
2. 因为ftpserver-core需要读取资源，所有前往[Releases](./releases)下载`resource.zip`
3. 将你的`dex`文件和`resource.zip`保存在同一路径下
4. 使用以下命令启动
```bash
dalvikvm -cp 你的dex路径 Main
```
> 必须切换到你`resource.zip`所在的目录下执行，否则dex识别不到
5. 接下来就可以与快的玩耍了，默认绑定`17021`端口，使用`/`目录

### 处理方法
 1. 前往`bin/release/dex/`找到`classes.dex.zip`
 2. 解压出里面的`dex`文件并且合并到一起
 
## 注意事项
目前强制显示`.`开头隐藏文件，绑定`17021`，资源文件名称`resource.zip`，使用`anonymous`匿名用户（无需密码，高危），HOME目录为根目录，自行按需修改代码
 
可能有些文件管理器（例如mt）会显示出文件权限为`000`，实际上并不是这样，我也不知道怎么回事

添加了一个自定义Ftp命令`STOPSERVER`，需要使用`nc`连接服务器，手动登录通过后输入`STOPSERVER`即可停止服务器