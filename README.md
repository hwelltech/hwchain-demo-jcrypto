## 恒为链Java版本示例

离线签名jar包在工程的lib目录中，可以选择上传到私服或者install到本地仓库的方式。

```
> java -version
java version "1.8.0_201"

> mvn -version
Apache Maven 3.5.2

> cd hwchain-demo-jcrypto\lib
> mvn install:install-file -Dfile=hwchain-jcrypto-0.0.1.jar -DpomFile=hwchain-jcrypto-0.0.1.pom
```