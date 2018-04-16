## FileProvider 权限
FileProvider 是 ContentProvider 的子类，以 content://Uri 代替 file://Uri 实现App间的文件的安全共享。当创建一个包含 Content URI 的 intent 时，可以通过 Intent.setFlags() 添加临时的读写权限，从而把 content URI 传递给另一个 app。

file://Uri 方式会修改文件的系统权限，这些权限会对其它任何 app 都可用，直到这些权限被手动改变为止。因此从 7.0 开始禁止通过 file://Uri 在 App 之间共享文件，这跟文件的所处位置无关，包括但不局限于通过Intent或ClipData 等方法。否则会报以下异常：
```
file:///storage/emulated/0/camera_crop.jpg exposed beyond app through ClipData.Item.getUri()
```
## FileProvider 使用
使用主要为以下步骤：
1. AndroidManifest 中定义 FileProvider 
2. res/xml 中定义对外暴露的文件夹路径
3. 生成content:// 类型的 Uri
4. 给 Uri 授予临时权限
5. 使用 Intent 传递 Uri

### 定义 FileProvider
Android 系统把文件生成 Content URI 的工作通过 FileProvider 完成了，我们只需要在 AndroidManifest.xml 文件中配置下就可以了
在 AndroidManifest.xml 中添加 provider
```
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="${applicationId}.provider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/share_path" />
</provider>
```
- android:authorities 对于每个 App 必须是唯一的，因此我们在命名时要确保唯一性
- android:exported 为 false 表示 FileProvider 对外不是公开的
- android:grantUriPermissions 为 true 表示允许临时读写文件
- meta-data 设置路径配置的 xml 文件，该文件提供了对外路径信息
### 定义路径
接下来我们要定义上一步中出现的 share_path.xml 文件
```
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <files-path path="." name="root" />
    <files-path path="images/" name="my_images" />
    <files-path path="audios/" name="my_audios" />
</paths>
```

path 属性用于指定当前代表目录下需要共享的子目录名称，name 属性用于给 path 属性所指定的子目录名称取一个别名。后续生成 content://URI 时，会使用这个别名代替真实目录名，这样做的目的是为了提高安全。各配置代表路径如下：
external-path 文件位于手机外部存储空间，对应 Environment.getExternalStorageDirectory();
files-path 文件位于 app 内部存储空间，对应 getFilesDir();
cache-path 文件位于 app 内部缓存空间，对应 getCacheDir();
external-files-path 文件位于外部存储空间，对应 Context.getExternalFilesDir(null)
external-cache-path 文件位于外部缓存空间，对应 Context.getExternalCacheDir().
external-media-path 文件位于外部媒体空间，对应 Context.getExternalMediaDirs()， API 21+

这样生成的 URI 类似如下：
```
content://com.edreamoon.stu.provider/myimages/default_image.jpg
```
### 文件生成 Content URI
通常通过File生成Uri的代码是这样：
```
Uri picUri = Uri.fromFile(new File(xxx));
```
这样生成的Uri格式为 file://xxx，前面说了这种格式 7.0 后无法在App之间共享，我们需要生成 content://xxx 类型的 Uri：
```
File imagePath = new File(Context.getFilesDir(), "images");
File newFile = new File(imagePath, "default_image.jpg");
Uri contentUri = FileProvider.getUriForFile(getContext(),  "com.erdreamoon.stu.provider", newFile);//第二个参数是定义的android:authorities属性的值
```
### Uri 授予临时权限
有两种权限 
FLAG_GRANT_READ_URI_PERMISSION：表示读取权限
FLAG_GRANT_WRITE_URI_PERMISSION：表示写入权限
有两种设置权限的办法：
- 调用 Context.grantUriPermission(package, uri, modeFlags)（package 为第三方 app 包名）这样设置的权限只有在手动调用 Context.revokeUriPermission(uri, modeFlags) 或系统重启后才会失效。
- 调用 Intent.setFlags() 来设置权限，当接收到的 activity 处于活跃状态时持续有效 , 退出时自动失效，一个 activity 获取到得 content URI 权限,这个权限会延展至所属的整个 app。
拥有授予权限的 Content URI 后，便可以通过 startActivity() 或者 setResult() 方法启动其他应用并传递授权过的 Content URI 数据。当然，也有其他方式提供服务，如果你需要一次性传递多个 URI 对象，可以使用 intent 对象提供的 setClipData() 方法，setClipData() 添加ClipData对象,可以放入1个或多个 ，每个ClipData对象都包含一个 content URI 并且 setFlags() 方法设置的权限适用于所有 Content URIs

以拍照为例
```
File imagePath = new File(Context.getFilesDir(), "images");
File newFile = new File(imagePath, "default_image.jpg");
Uri contentUri = getUriForFile(getContext(), "com.edreamoon.stu.provider", newFile);
Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);// 授予目录临时共享权限
startActivityForResult(intent, 100);
```
### 接收 Content URI
按照上面拍照案例中，系统相机 app 收到 ContentURI 后是怎么处理的哪，其实只需要按照普通ContentProvider访问即可
```
ParcelFileDescriptor inputPFD = context.getContentResolver().openFileDescriptor(returnUri, "r");
Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
...
```



