#wiscess-common:

版本：v1.4
更新时间：2018-03-24
更新内容：
1.WebConfig中添加路径匹配模式，只允许与Controll中的路径一致，如/user，不允许访问/user.xxxx或/user/方式；
2.增加FileUtils，增加方法sendFile

# fancyBox

jQuery lightbox script for displaying images, videos and more.
Touch enabled, responsive and fully customizable.

See the [project page](http://fancyapps.com/fancybox/3/) for documentation and a demonstration.

Follow [@thefancyapps](//twitter.com/thefancyapps) for updates.


## Quick start

1\.  Add latest jQuery and fancyBox files

```html
<script src="//code.jquery.com/jquery-3.2.1.min.js"></script>

<link  href="/path/to/jquery.fancybox.min.css" rel="stylesheet">
<script src="/path/to/jquery.fancybox.min.js"></script>
```


2\.  Create links

```html
<a data-fancybox="gallery" href="big_1.jpg">
    <img src="small_1.jpg">
</a>

<a data-fancybox="gallery" href="big_2.jpg">
    <img src="small_2.jpg">
</a>
```


3\. Enjoy!


## License

fancyBox is licensed under the [GPLv3](http://choosealicense.com/licenses/gpl-3.0) license for all open source applications.
A commercial license is required for all commercial applications (including sites, themes and apps you plan to sell).

[Read more about fancyBox license](http://fancyapps.com/fancybox/#license).

## Bugs and feature requests

If you find a bug, please report it [here on Github](https://github.com/fancyapps/fancybox/issues).

Guidelines for bug reports:

1. Use the GitHub issue search — check if the issue has already been reported.
2. Check if the issue has been fixed — try to reproduce it using the latest master or development branch in the repository.
3. Isolate the problem — create a reduced test case and a live example. You can use CodePen to fork any demo found on documentation to use it as a template.

A good bug report shouldn't leave others needing to chase you up for more information.
Please try to be as detailed as possible in your report.


Feature requests are welcome. Please look for existing ones and use GitHub's "reactions" feature to vote.

Please do not use the issue tracker for personal support requests - use Stack Overflow ([fancybox-3](http://stackoverflow.com/questions/tagged/fancybox-3) tag) instead.
