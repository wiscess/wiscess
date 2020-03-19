#wiscess-filter:
##版本：v2.0
pom.xml
```pom
    <dependency>
        <groupId>com.wiscess</groupId>
        <artifactId>wiscess-filter</artifactId>
        <version>2.0</version>
    </dependency>
```
application.yml
```

```

####更新时间：2020-03-19

#####更新时间：2019-01-04

#####更新内容：
<pre>
1.调整xss过滤拦截器的顺序为-9999，确保该过滤器在security的filter之前执行，使得登录请求中获取用户名和密码时，也能受到过滤器影响，因此，security在获取用户名和密码时，需获取原始的request中的参数，否则xss过滤器会将加密后的username和password的字符串进行处理，导致解密失败。<br/>
2.调整HeaderWriterFilter的顺序为-9990，确保在security的所有处理结束之后再添加自定义的header，而不会被security默认的headerWriterFilter替换掉header中的内容。<br/>
3.JsoupUtil提供了几种清理字符串内容的方法，如：<br/>
  html(String)：替换所有不允许的字符，会将下列字符进行转义：<br/>
            &amp;amp;    -->  &amp;  -->  ＆<br/>
            &amp;apos;   -->  &apos;  -->  ’<br/>
            &amp;quot;   -->  &quot;  -->  “<br/>
            &amp;nbsp;   -->  &nbsp;<br/>
            &amp;lt;     -->  <  -->  ＜<br/>
            &amp;gt;     -->  >  -->  ＞<br/>
            &amp;times;  -->  &times;<br/>
            &amp;divide; -->  &divide;<br/>
            &amp;ensp;   -->  &ensp;<br/>
            &amp;emsp;   -->  &emsp;<br/>
            \        -->  ＼<br/>
            #        -->  ＃<br/>
   cleanName(String):清除所有标签内容，并进行html(String)处理<br/>
   cleanContent(String):对文本框内容进行处理，只保留基本标签，允许有单引号和双引号<br/>
   cleanWithImages(String):允许的便签有a,b,blockquote,br,cite,code,dd,dl,dt,em,i,li,ol,p,pre,q,small,span,strike,strong,sub,sup,u,ul,img 以及a标签的href,img标签的src,align,alt,height,width,title属性<br/>
   cleanJson(String):处理Json类型的Html标签,进行xss过滤<br/>
</pre>

#####更新时间：2018-08-08

#####更新内容：

1.适配springboot 2.0.4；




