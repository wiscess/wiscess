# wiscess-common:

## 版本：v1.4

更新时间：2018-03-24

更新内容：

1\.WebConfig中添加路径匹配模式，只允许与Controll中的路径一致，如/user，不允许访问/user.xxxx或/user/方式；

2\.增加FileUtils，增加方法sendFile
```html
FileUtils.sendFile(request, response, file);
```

参见[wiscess](https://github.com/wiscess/wiscess).

