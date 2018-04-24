# Instalação Cluster Flink

1. Máquina 1 (trbflkdsh01): 
1.1. Hadoop HDFS (NameNode, SecondaryNameNode, DataNode)
1.2. Zookeeper
1.3. Flink (JobManager, TaskManager)

2. Máquinas 2 e 3 (trbflkdsh02, trbflkdsh03):
2.1. Hadoop HDFS (DataNode)
2.2. Flink (JobManager, TaskManager)

*Atenção:* Todos os comandos deverão ser executados nas 3 máquinas, ao menos que esteja indicado uma máquina específica. 
Substituir os nomes dos hosts (neste exemplo estão sendo usados os hosts de homologação trbflkdsh01, trbflkdsh01 e trbflkdsh01)  



----

### Pontos de montagem (LV)

1. Hadoop HDFS
1.1. Instalação: /opt/hadoop
1.2. Logs (registros): /trb/hadoop/logs
1.3. DataNode: /trb/hadoop/datanode
1.4. NameNode: /trb/hadoop/namenode
1.5. Diretório de troca: /trb/hadoop/tmp

2. Zookeeper
2.1. Instalação: /opt/zookeeper
2.2. Logs (registros): /trb/zookeeper/log 
2.3. DataDir: /trb/zookeeper/datadir

3. Flink
3.1. Instalação: /opt/flink
3.2. Logs (registros): /trb/flink/log
3.3. Diretório de JARs: /trb/flink/jars
3.4. Diretório de troca: /trb/flink/tmp


### Dimensionamento diretórios (100GB)

- /trb/hadoop [30G]
- /trb/hadoop/datanode (12.5GB)
- /trb/hadoop/namenode (12.5GB)
- /trb/hadoop/logs (5GB)
- /trb/zookeeper [30GB]
- /trb/zookeeper/datadir (25GB)
- /trb/zookeeper/log (5GB)
- /trb/flink [30GB]
- /trb/flink/jars (5GB)
- /trb/flink/log (5GB)
- /trb/flink/tmp (20GB)
- /tmp (10GB)

### Criação usuários

1. Cria grupos

```
  # groupadd hadoop
  # groupadd flink
  # groupadd zookeeper
```

2. Cria usuários

```
  # useradd -g hadoop -d /home/hadoop hadoop
  # useradd -g flink  -d /home/flink flink
  # useradd -g zookeeper -d /home/flink zookeeper
```

3. Altera senha dos usuários

```
  # passwd hadoop
  # passwd flink
  # passwd zookeeper
```

### Configuração de SSH

1. Configura acesso SSH passwordless a todas as máquinas (sem senha)

```
  # su - hadoop
  $ ssh-keygen -t rsa
  $ ssh-copy-id -i ~/.ssh/id_rsa.pub hadoop@trbflkdsh01
  $ ssh-copy-id -i ~/.ssh/id_rsa.pub hadoop@trbflkdsh02
  $ ssh-copy-id -i ~/.ssh/id_rsa.pub hadoop@trbflkdsh03
```

```
  # su - flink
  $ ssh-keygen -t rsa
  $ ssh-copy-id -i ~/.ssh/id_rsa.pub flink@trbflkdsh01
  $ ssh-copy-id -i ~/.ssh/id_rsa.pub flink@trbflkdsh02
  $ ssh-copy-id -i ~/.ssh/id_rsa.pub flink@trbflkdsh03
```

2. Edita /etc/ssh/sshd_config

2.1. Adiciona os grupos `hadoop`, `flink` e `zookeeper` ao AllowGroups:

```
  AllowGroups suporte root trb.it.spt hadoop flink zookeeper
```

3. Reinicia serviço sshd

```
  # service sshd restart
```

### Configuração Java

1. Instalação do Java

```
  # yum install wget
  # yum install java-1.8.0
```

2. Adiciona ao final do arquivo /etc/bashrc:

```
  export JAVA_HOME=/usr/lib/jvm/jre-1.8.0
```

### Configuração hadoop 

1. Instalação do hadoop

```
  # wget http://ftp.unicamp.br/pub/apache/hadoop/common/hadoop-2.7.4/hadoop-2.7.4.tar.gz
  # tar xzf hadoop-2.7.4.tar.gz
  # mv /hadoop-2.7.4 /opt/hadoop
  # mkdir -p /trb/hadoop/datanode
  # mkdir -p /trb/hadoop/namenode
  # chown hadoop.hadoop -R /trb/hadoop /opt/hadoop
```

2. Adiciona linhas ao final do arquivo /home/hadoop/.bashrc

```
  export HADOOP_PREFIX=/opt/hadoop
  export HADOOP_HOME=$HADOOP_PREFIX
  export HADOOP_COMMON_HOME=$HADOOP_PREFIX
  export HADOOP_CONF_DIR=$HADOOP_PREFIX/etc/hadoop
  export HADOOP_HDFS_HOME=$HADOOP_PREFIX
  export HADOOP_MAPRED_HOME=$HADOOP_PREFIX
  export HADOOP_YARN_HOME=$HADOOP_PREFIX
  export HADOOP_OPTS="$HADOOP_OPTS -Djava.library.path=$HADOOP_HOME/lib/native"
  export PATH=$PATH:$HADOOP_PREFIX/sbin:$HADOOP_PREFIX/bin
```

3. Edita /opt/hadoop/etc/hadoop/core-site.xml

```
  <configuration>
  <property>
      <name>fs.defaultFS</name>
      <value>hdfs://trbflkdsh01:9000/</value>
  </property>
  <property>
      <name>hadoop.tmp.dir</name>
      <value>/trb/hadoop/tmp</value>
  </property>
  <property>
      <name>hadoop.log.dir</name>
      <value>/trb/hadoop/logs</value>
  </property>
  </configuration>
```

4. Edita /opt/hadoop/etc/hadoop/hdfs-site.xml

```
  <configuration>
  <property>
    <name>dfs.replication</name>
    <value>3</value>
  </property>
  <property>
    <name>dfs.permissions</name>
    <value>false</value>
  </property>
  <property>
     <name>dfs.datanode.data.dir</name>
     <value>/trb/hadoop/datanode</value>
  </property>
  <property>
    <name>dfs.namenode.data.dir</name>
    <value>/trb/hadoop/namenode</value>
  </property>
  </configuration>
```

5. Edita /opt/hadoop/etc/hadoop/slaves 

```
  trbflkdsh01
  trbflkdsh02
  trbflkdsh03
```

6. Verifica se firewalld está parado (conflito com ipv6)

```
  # systemctl stop firewalld
```

7. Adiciona linhas ao arquivo /etc/sysctl.conf:

```
  net.ipv6.conf.all.disable_ipv6 = 1
  net.ipv6.conf.default.disable_ipv6 = 1
```

8. Formata HDFS

```
  # su - hadoop
  $ hdfs namenode -format
```

9. Inicia hadoop (apenas máquina 1) para teste

```
  # su - hadoop
  $ start-dfs.sh
```

*Atenção:* Rodar o `stop-dfs.sh` para parar o serviço.

10. Instala serviço no systemd (apenas máquina 1)

```
  # cp -rf hadoop.service /usr/lib/systemd/system/
  # systemctl enable hadoop.service
```

11. Inicia serviço no systemd (apenas máquina 1)

```
  # systemctl start hadoop.service
```

### Configuração zookeeper (Apenas máquina 1)

1. Instala ZooKeeper

```
  # wget http://ftp.unicamp.br/pub/apache/zookeeper/stable/zookeeper-3.4.10.tar.gz
  # tar xzf zookeeper-3.4.10.tar.gz
  # mv zookeeper-3.4.10 /opt/zookeeper
  # mkdir -p /trb/zookeeper/datadir
  # chown zookeeper.zookeeper -R /trb/zookeeper /opt/zookeeper
```

2. Cria arquivo /opt/zookeeper/conf/zoo.cfg

```
  tickTime=2000
  dataDir=/trb/zookeeper/datadir
  dataLogDir=/trb/zookeeper/log
  clientPort=2181
```

3. Inicia zookeeper para teste

```
  $ bin/zkServer.sh start
```

*Atenção:* Rodar o `zkServer.sh stop` para parar o serviço.

4. Instala serviço no systemd

```
  # cp -rf zookeeper.service /usr/lib/systemd/system/
  # systemctl enable zookeeper.service
```

5. Inicia serviço no systemd

```
  # systemctl start zookeeper.service
```


### Configuração Flink

1. Instala Flink

```
  # wget http://mirror.nbtelecom.com.br/apache/flink/flink-1.3.2/flink-1.3.2-bin-hadoop27-scala_2.11.tgz
  # tar xzf flink-1.3.2-bin-hadoop27-scala_2.11.tgz
  # mv flink-1.3.2 /opt/flink
  # chown flink.flink -R /opt/flink /trb/flink
```

2. Edita /opt/flink/conf/slaves

```
  trbflkdsh01
  trbflkdsh02
  trbflkdsh03
```

3. Editar /opt/flink/conf/masters

```
  trbflkdsh01:8081
  trbflkdsh02:8081
  trbflkdsh03:8081
```

4. Edita /opt/flink/conf/flink-conf.yaml

```
  env.log.dir: /trb/flink/log
  jobmanager.rpc.address: localhost
  jobmanager.rpc.port: 6123
  jobmanager.heap.mb: 1024
  jobmanager.web.tmpdir: /trb/flink/tmp
  jobmanager.web.upload.dir: /trb/flink/jars
  taskmanager.heap.mb: 1024
  taskmanager.numberOfTaskSlots: 1
  taskmanager.memory.preallocate: false
  taskmanager.tmp.dirs: /trb/flink/tmp
  taskmanager.log.path: /trb/flink/log
  parallelism.default: 1
  jobmanager.web.address: 0.0.0.0
  jobmanager.web.port: 8081
  fs.overwrite-files: true
  fs.hdfs.hadoopconf: /opt/hadoop/etc/hadoop
  high-availability: zookeeper
  high-availability.storageDir: hdfs:///flink/ha/
  high-availability.zookeeper.quorum: trbflkdsh01:2181
  zookeeper.sasl.disable: true
```

5. Inicia flink (apenas máquina 1) para teste

```
  $ /opt/flink/bin/start-cluster.sh
```

*Atenção:* Rodar o `stop-cluster.sh` para parar o serviço.

6. Instala serviço no systemd (apenas máquina 1)

```
  # cp -rf flink.service /usr/lib/systemd/system/
  # systemctl enable flink.service
  # cp -rf flinkhistory.service /usr/lib/systemd/system/
  # systemctl enable flinkhistory.service
```

7. Inicia serviço no systemd (apenas máquina 1)

```
  # systemctl start flink.service
  # systemctl start flinkhistory.service
```