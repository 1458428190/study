# Docker
### 学习书籍
- 《第一本Docker书》James Turnbull著

### 安装部署
- 安装Docker：https://www.jianshu.com/p/3a4cd73e3272

- 问题
    - 部署Docker私有仓库不连通问题：https://blog.csdn.net/wangtaoking1/article/details/44180901
    - nginx访问403问题：https://blog.csdn.net/vanilla_he/article/details/79205091 (未解决)
    - 安装Redis时, Ruby版本必须>=2.2, ubuntu自带的Ruby版本过低
    - ubuntu的apt-get版本低, 如果出现找不到package, 记得更新, apt-get update -yqq
    - 安装软件时，记得带上-y, 表示默认全yes, 因为在Docker非交互式下, 出现需要输入yes会终止安装
    - 关于ubuntu安装新版本Ruby(>=2.2的), https://blog.csdn.net/zahuopuboss/article/details/53249855
        ```text
        RUN apt-get update -yqq && apt-get -yqq install software-properties-common python-software-properties
        RUN add-apt-repository ppa:brightbox/ruby-ng
        RUN apt-get update -yqq && apt-get -yqq install ruby2.3 ruby2.3-dev
        ```
    - p107页，Sinatra Web的下载地址404
    - centos7 安装redis: https://www.cnblogs.com/rslai/p/8249812.html (自身没有redis的yum源，需要yum install epel-release)
    - 安装Jenkins出现bug, 未解决 (Dockerfile内容有问题, P134)
        ```text
        FROM ubuntu:14.04
        MAINTAINER laichengfeng "15767512424@163.com"
        ENV REFRESHED_AT 2018-11-23
        
        RUN apt-get update -qq && apt-get install -qqy curl apt-transport -https
        RUN apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
        RUN echo deb https://apt.dockerproject.org/repo ubuntu-trusty main > /etc/apt/sources.list.d/docker.list
        RUN apt-get install -yqq apt-transport-https
        RUN apt-get update -qq && apt-get install -qqy iptables ca-certificates openjdk-7-jdk git-core docker-engine
        
        ENV JENKINS_HOME /opt/jenkins/data
        ENV JENKINS_MIRROR http://mirrors.jenkins-ci.org
        
        RUN mkdir -p $JENKINS_HOME/plugins
        RUN apt-get update -qq && apt-get install -yqq curl && curl -sf -o /opt/jenkins/jenkins.war -L $JENKINS_MIRROR/warstable/latest/jenkins.war
        
        RUN for plugin in chucknorris greenballs scm-api git-client git ws-clieanup ; do curl -sf -o $JENKINS_HOME/plugins/${plugin}.hpi -L $JENKINS_MIRROR/plugins/${plugin}/latest/${plugin}.hpi; done
        
        ADD ./dockerjenkins.sh /usr/local/bin/dockerjenkins.sh
        RUN chmod +x /usr/local/bin/dockerjenkins.sh
        
        VOLUME /var/lib/docker
        
        EXPOSE 8080
        
        ENTRYPOINT ["/usr/local/bin/dockerjenkins.sh"]
        ```
    - centos7安装高版本的ruby：https://www.cnblogs.com/ding2016/p/7903147.html
