/*
SQLyog Community v13.0.1 (64 bit)
MySQL - 5.5.60-log : Database - db_book
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`db_book` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `db_book`;

/*Table structure for table `t_book` */

DROP TABLE IF EXISTS `t_book`;

CREATE TABLE `t_book` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type_id` bigint(20) NOT NULL COMMENT '所属类别',
  `name` varchar(100) NOT NULL COMMENT '书名',
  `author` varchar(50) DEFAULT NULL COMMENT '作者',
  `price` double DEFAULT NULL COMMENT '价格',
  `cover` varchar(200) DEFAULT NULL COMMENT '封面图',
  `summary` varchar(1000) DEFAULT NULL COMMENT '摘要',
  `stock` int(11) DEFAULT NULL COMMENT '存量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;

/*Data for the table `t_book` */

insert  into `t_book`(`id`,`type_id`,`name`,`author`,`price`,`cover`,`summary`,`stock`) values 
(1,1,'挪威的森林','村上春树',66,'http://pj7ldvis7.bkt.clouddn.com/avatar/8.jpg','这是一部动人心弦的、平缓舒雅的、略带感伤的恋爱小说。小说主人公渡边以第一人称展开他同两个女孩间的爱情纠葛。',9),
(2,1,'我只喜欢你','乔一',29.8,'http://pj7ldvis7.bkt.clouddn.com/avatar/19.jpg','他温柔地握住我的手，“但我知道，一想到能和你共度余生，我就对余生充满期待。”',8),
(3,1,'最好的我们','八月长安',55,'http://pj7ldvis7.bkt.clouddn.com/avatar/20.jpg','你还记得高中时的同桌吗？那个少年有世界上最明朗的笑容，那个女生有世界上最好看的侧影。高中三年，两个人的影子和粉笔灰交织在一起，黑白分明，在记忆里面转圈。本书以怀旧的笔触讲述了女主角耿耿和男主角余淮同桌三年的故事，耿耿余淮，这么多年一路走过的成长故事极为打动人心，整个故事里有的都是在成长过程中细碎的点点滴滴，将怀旧写到了极致，将记忆也写到了极致。',7),
(6,2,'流学的一年','白关',45,'http://pj7ldvis7.bkt.clouddn.com/avatar/1.jpg','★既然有浪迹天涯的梦想，就要去实现！与其坐在家里羡慕别人去留学，不如收拾行囊自己去“流学”！',4),
(8,2,'从你的全世界路过','张嘉佳',34.5,'http://pj7ldvis7.bkt.clouddn.com/avatar/2.jpg','《从你的全世界路过》是微博上最会写故事的人张嘉佳献给你的心动故事。',3),
(9,2,'朝花惜时','左小翎',23.6,'http://pj7ldvis7.bkt.clouddn.com/avatar/3.jpg','天真少女安可乐第一次来到大城市，在学校里结实了形形色色的新朋友：态度恶劣的后桌同学叶陵、心地善良却经常受到嫉妒和欺凌的校花文浅浅、曾经暗恋的学长白一然……还在人生第一次去夜店时遇到了极具神秘感的服务生言述。在努力适应崭新生活环境的过程中，她发现叶陵与言述之间似乎有着千丝万缕的联系……',2),
(28,3,'那些回不去的年少时光','桐华',23.8,'http://pj7ldvis7.bkt.clouddn.com/avatar/4.jpg','最值得珍藏的怀旧读物，写给年少自己的书，纪念我们共同的青春和成长',1),
(29,3,'少年巴比伦','路内',22,'http://pj7ldvis7.bkt.clouddn.com/avatar/5.jpg','上个世纪90年代的戴城，路小路在一家化工厂上班，他是个愣头青，不知道未来和生活目标在哪里。跟着一个叫“老牛逼”的师傅混，没学会半点技术。在机修班，除了拧螺丝之外什么都不会，在电工班，就只会换灯泡。除此之外，还喜欢打游戏、翻工厂的院墙，打架。当然还追女人，他与一个叫白蓝的厂医产生了爱情，最终因为白蓝考上了研究生而离开了他。',9),
(30,4,'余生，请多指教','柏林石匠',32,'http://pj7ldvis7.bkt.clouddn.com/avatar/6.jpg','曾经以为，自己这辈子都等不到了——世界这么大，我又走得这么慢，要是遇不到良人要怎么办？',8),
(35,4,'何以笙箫默','顾漫',15,'http://pj7ldvis7.bkt.clouddn.com/avatar/7.jpg','一段年少时的爱恋，牵出一生的纠缠。大学时代的赵默笙阳光灿烂，对法学系大才子何以琛一见倾心，开朗直率的她拔足倒追，终于使才气出众的他为她停留驻足。然而，不善表达的他终于使她在一次伤心之下远走他乡……',7),
(52,6,'Java编程思想','Bruce Eckel ',88,'http://pj7ldvis7.bkt.clouddn.com/avatar/8.jpg','本书赢得了全球程序员的广泛赞誉，即使是最晦涩的概念，在Bruce Eckel的文字亲和力和小而直接的编程示例面前也会化解于无形。从Java的基础语法到最高级特性（深入的面向对象概念、多线程、自动项目构建、单元测试和调试等），本书都能逐步指导你轻松掌握。',6),
(57,5,'经济学原理','[美] 曼昆 ',88,'http://pj7ldvis7.bkt.clouddn.com/avatar/10.jpg','此《经济学原理》的第3版把较多篇幅用于应用与政策，较少篇幅用于正规的经济理论。书中主要从供给与需求、企业行为与消费者选择理论、长期经济增长与短期经济波动以及宏观经济政策等角度深入浅出地剖析了经济学家们的世界观。',4),
(58,6,'浪潮之巅','[美] 吴军 ',55,'http://pj7ldvis7.bkt.clouddn.com/avatar/11.jpg','近一百多年来，总有一些公司很幸运地、有意识或无意识地站在技术革命的浪尖之上。在这十几年间，它们代表着科技的浪潮，直到下一波浪潮的来临。\n\n从一百年前算起，AT&T 公司、IBM 公司、苹果公司、英特尔公司、微软公司、思科公司、雅虎公司和Google公司都先后被幸运地推到了浪尖。虽然，它们来自不同的领域，中间有些已经衰落或正在衰落，但是它们都极度辉煌过。本书系统地介绍了这些公司成功的本质原因及科技工业一百多年的发展。',3),
(61,5,'创业维艰','本·霍洛维茨',49,'http://pj7ldvis7.bkt.clouddn.com/avatar/14.jpg','在《创业维艰》中，本·霍洛维茨从自己的创业经历讲起，以自己在硅谷近20余年的创业、管理和投资经验，对创业公司（尤其是互联网技术公司）的创立、经营、人才选拔、企业文化、销售、CEO与董事会的关系等方方面面，毫无保留地奉上自己的经验之谈。他还谈到了与比尔·坎贝尔、安迪·拉切列夫、迈克尔·奥维茨等硅谷顶级CEO和投资人的交往经历，从他们身上学到的宝贵经验，以及他和马克·安德森这对绝佳拍档为何能够一起奋斗18年还能合作得这么好。',9);

/*Table structure for table `t_reader` */

DROP TABLE IF EXISTS `t_reader`;

CREATE TABLE `t_reader` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `avatar` varchar(300) DEFAULT NULL COMMENT '头像',
  `role` varchar(10) DEFAULT NULL COMMENT '角色',
  `department` varchar(50) DEFAULT NULL COMMENT '部门',
  `join_date` date DEFAULT NULL COMMENT '加入时间',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(20) DEFAULT NULL COMMENT '电话',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

/*Data for the table `t_reader` */

insert  into `t_reader`(`id`,`name`,`avatar`,`role`,`department`,`join_date`,`email`,`mobile`) values 
(1,'闫泽华','http://pj7ldvis7.bkt.clouddn.com/avatar/1.jpg','教师','计算机与软件学院','2016-01-01','yanzh@niit.edu.cn','13976763333'),
(3,'梅拾璎','http://pj7ldvis7.bkt.clouddn.com/avatar/3.jpg','教师','电气工程学院','2016-09-01','meisy@niit.edu.cn','15100998877'),
(4,'阿栈','http://pj7ldvis7.bkt.clouddn.com/avatar/4.jpg','学生','经济管理学院','2017-10-01','az@niit.edu.cn','13987876554'),
(5,'灰土豆','http://pj7ldvis7.bkt.clouddn.com/avatar/5.jpg','学生','艺术设计学院','2018-12-04','huitd@niit.edu.cn','13144336565'),
(6,'三儿王屿','http://pj7ldvis7.bkt.clouddn.com/avatar/6.jpg','教师','计算机与软件学院','2018-12-01','sanwy@niit.edu.cn','18011113333'),
(7,'汪波','http://pj7ldvis7.bkt.clouddn.com/avatar/7.jpg','教师','计算机与软件学院','2018-12-04','wangb@niit.edu.cn','18900008989');

/*Table structure for table `t_type` */

DROP TABLE IF EXISTS `t_type`;

CREATE TABLE `t_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type_name` varchar(20) NOT NULL COMMENT '类别名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Data for the table `t_type` */

insert  into `t_type`(`id`,`type_name`) values 
(1,'文学'),
(2,'流行'),
(3,'文化'),
(4,'生活'),
(5,'经管'),
(6,'科技');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
