CREATE TABLE `store` (
  `storeId` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(30) DEFAULT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `discount` double NOT NULL,
  `status` int(1) NOT NULL,
  `stockMode` int(1) NOT NULL,
  `ownerPassword` varchar(100) NOT NULL,
  `clerkPassword` varchar(255) DEFAULT NULL,
  `storeDishesGroupIds` text,
  `managerDishesGroupIds` text,
  `memberDishesGroupIds` text,
  `weChatDishesGroupIds` text,
  `ifUsing` int(1) NOT NULL DEFAULT '0',
  `activityType` int(1) NOT NULL DEFAULT '0',
  `fullMoney` double(10,2) NOT NULL,
  `reduceMoney` double(10,2) NOT NULL,
  PRIMARY KEY (`storeId`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;



INSERT INTO xjh.store (storeId, name, address, email, phone, discount, status, stockMode, ownerPassword, clerkPassword, storeDishesGroupIds, managerDishesGroupIds, memberDishesGroupIds, weChatDishesGroupIds, ifUsing, activityType, fullMoney, reduceMoney) VALUES(1, '上海龙之梦', '莘庄龙之梦', '111111@helo.com', '18812120001', 0.9, 1, 0, '123456', '123456', 'Wzhd', 'Wzhd', 'Wzhd', 'Wzhd', 0, 0, 0, 0);


CREATE TABLE `dishes_group_list` (
  `dishesGroupId` int(11) NOT NULL AUTO_INCREMENT,
  `dishesGroupName` varchar(50) NOT NULL,
  `dishesGroupContent` text,
  `createTime` bigint(20) NOT NULL,
  PRIMARY KEY (`dishesGroupId`),
  UNIQUE KEY `dishesGroupName` (`dishesGroupName`),
  KEY `createTime` (`createTime`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;



INSERT INTO xjh.dishes_group_list (dishesGroupId, dishesGroupName, dishesGroupContent, createTime) VALUES(8, '打折集合', 'WzUxOCw1MTksNTI2LDUyNyw1MjgsNTI5LDUzMCwzMzEsMiw0LDgsMTAsMTEsMTIsMTMsMzM1LDQxMCw0NDksMTYsMTgsMzQ3LDEyOSwxMzYsMTM3LDEzOCwxMzksMTQxLDE0MiwxNDQsMTY0LDE2Niw0NTIsNDU0LDQ1NSw0NjYsNTMxLDYwLDYyLDYzLDY2LDY4LDczLDc0LDM0NSwzODUsMzg2LDM4Nyw0MDIsNDAzLDE3NiwyMTgsMjIyLDQ5MywyMSwyNywyOSwzNDgsMzQ5LDE3OSwxODAsMTgxLDQ1MSw0NTYsNDg2LDI3NiwyNzcsMjg1LDMwLDMyLDMzLDM1LDM2LDM4LDMxNiw3NiwzNTUsMzkxLDQxOCwxODcsNTE0LDI2OCwyODMsNDMsNDUsNDksNTAsNTIsNTMsNTQsMzI4LDMzOCwzNjUsMzY2LDM2Nyw0MDUsNDExLDQxMiw0MTMsNDIxLDE4MiwxODMsMTg0LDE4NSw1MDYsNTA3LDUyNCw1MjUsMzA2LDMzMiw4MCw4MSw4NCwxNDAsNDA3LDE4OCwxOTEsNDQ3LDQ1OCw1MTAsMzksNDAsNDEsMzIyLDg2LDg3LDIzNywyMzgsMzQxLDkyLDQ4Miw0ODMsNDg0LDQ4NSw0ODgsNDg5LDQ5MCwyMzIsMzM2LDg1LDI3NCw0ODcsNTMyLDUzMyw1MzQsNTM1LDUzNiw1MzcsNTM4LDUzOSw1NDEsNTQ1LDU0Niw1NDcsNDA4LDM1NywzNTgsMjIzLDIwLDI0Miw1NDIsNTQzLDM2MSwzNjMsMzYyLDU0NCwzOTAsNTUsNTYsMTQsMTMwLDU1Niw1NTgsNTQ4LDU0OSw1NTAsNTUxLDU1Miw1NTMsNTU0LDU1NSw1NTcsODIsNzAsNzEsMzc3LDU3Niw1ODUsNTkwLDU5MSw1ODYsNTg3LDU4OSw1NzcsNTg4LDMxLDU5OSw2MDAsNjAxLDYwMiw2MDMsMTMxLDIyNl0=', 1501404048607);



