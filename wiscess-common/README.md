# wiscess-common:

## 版本：v3.0.5

pom.xml

```
    <dependency>
        <groupId>com.wiscess</groupId>
        <artifactId>wiscess-common</artifactId>
        <version>3.0.5</version>
    </dependency>
```
#####更新时间：2023-04-19

#####更新内容：

1.适配springboot 3.0.5；<br/>
2.java版本升级到17.0.7；<br/>
3.替换javax.servlet.\*，改为jakarta.servlet.\*;<br/>
4.集成knife4j 4.1.0。<br/>
5.META-INF/spring.factories废弃，改为META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports，每行一个配置类<br/>



#####更新时间：2018-12-29

#####更新内容：

1.适配springboot 2.1.1；<br/>
2.ThymeLeaf增加元素定义，Option和Element，其中Element可以生成各种标签，如ul、li等；<br/>
3.增加FileUtils工具类，可以对文件名进行编码，或下载文件，解决下载文件中文名乱码的情况；<br/>
4.ZipPath解决中文名的问题；

#####更新时间：2018-08-08

#####更新内容：

1.适配springboot 2.0.4；<br/>
2.使用ThymeLeaf3.0.9，更新自定义标签的处理方式；



