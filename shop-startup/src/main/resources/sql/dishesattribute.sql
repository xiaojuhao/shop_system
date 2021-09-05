CREATE TABLE `dishesattribute_list` (
  `dishesAttributeId` int(11) NOT NULL AUTO_INCREMENT,
  `dishesAttributeName` varchar(100) NOT NULL,
  `dishesAttributeMarkInfo` varchar(200) DEFAULT NULL,
  `isValueRadio` int(1) NOT NULL COMMENT '1--单选 0--复选',
  `isSync` int(1) NOT NULL COMMENT '1---同步  0 --不同步',
  `dishesAttributeObj` text,
  `creatTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`dishesAttributeId`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;


INSERT INTO xjh.dishesattribute_list (dishesAttributeId, dishesAttributeName, dishesAttributeMarkInfo, isValueRadio, isSync, dishesAttributeObj, creatTime) VALUES(1, '口味', '菜的辣度', 1, 1, 'eyJpc1ZhbHVlUmFkaW8iOnRydWUsInNlbGVjdGVkQXR0cmlidXRlVmFsdWVzIjpbXSwiZGlzaGVzQXR0cmlidXRlTWFya0luZm8iOiLoj5znmoTovqPluqYiLCJkaXNoZXNBdHRyaWJ1dGVJZCI6MSwiZGlzaGVzQXR0cmlidXRlTmFtZSI6IuWPo+WRsyIsImlzU3luYyI6dHJ1ZSwiYWxsQXR0cmlidXRlVmFsdWVzIjpbeyJhdHRyaWJ1dGVWYWx1ZSI6IuS4jei+oyJ9LHsiYXR0cmlidXRlVmFsdWUiOiLlvq7ovqMifSx7ImF0dHJpYnV0ZVZhbHVlIjoi6L6jIn0seyJhdHRyaWJ1dGVWYWx1ZSI6Iui2hee6p+i+oyJ9XX0=', 1501422274264);
INSERT INTO xjh.dishesattribute_list (dishesAttributeId, dishesAttributeName, dishesAttributeMarkInfo, isValueRadio, isSync, dishesAttributeObj, creatTime) VALUES(2, '是否加葱', '', 1, 1, 'eyJpc1ZhbHVlUmFkaW8iOnRydWUsInNlbGVjdGVkQXR0cmlidXRlVmFsdWVzIjpbXSwiZGlzaGVzQXR0cmlidXRlTWFya0luZm8iOiIiLCJkaXNoZXNBdHRyaWJ1dGVJZCI6MiwiZGlzaGVzQXR0cmlidXRlTmFtZSI6IuaYr+WQpuWKoOiRsSIsImlzU3luYyI6dHJ1ZSwiYWxsQXR0cmlidXRlVmFsdWVzIjpbeyJhdHRyaWJ1dGVWYWx1ZSI6IuS4jeWKoOiRsSJ9LHsiYXR0cmlidXRlVmFsdWUiOiLliqDokbEifV19', 1501819320508);
INSERT INTO xjh.dishesattribute_list (dishesAttributeId, dishesAttributeName, dishesAttributeMarkInfo, isValueRadio, isSync, dishesAttributeObj, creatTime) VALUES(3, '做法', '', 1, 1, 'eyJpc1ZhbHVlUmFkaW8iOnRydWUsInNlbGVjdGVkQXR0cmlidXRlVmFsdWVzIjpbXSwiZGlzaGVzQXR0cmlidXRlTWFya0luZm8iOiIiLCJkaXNoZXNBdHRyaWJ1dGVJZCI6MywiZGlzaGVzQXR0cmlidXRlTmFtZSI6IuWBmuazlSIsImlzU3luYyI6dHJ1ZSwiYWxsQXR0cmlidXRlVmFsdWVzIjpbeyJhdHRyaWJ1dGVWYWx1ZSI6IuiWhOWIh++8iDjniYfvvIkifSx7ImF0dHJpYnV0ZVZhbHVlIjoi5Y6a5YiH77yINeeJh++8iSJ9XX0=', 1507189725152);
INSERT INTO xjh.dishesattribute_list (dishesAttributeId, dishesAttributeName, dishesAttributeMarkInfo, isValueRadio, isSync, dishesAttributeObj, creatTime) VALUES(4, '做法.', '', 1, 1, 'eyJpc1ZhbHVlUmFkaW8iOnRydWUsInNlbGVjdGVkQXR0cmlidXRlVmFsdWVzIjpbXSwiZGlzaGVzQXR0cmlidXRlTWFya0luZm8iOiIiLCJkaXNoZXNBdHRyaWJ1dGVJZCI6NCwiZGlzaGVzQXR0cmlidXRlTmFtZSI6IuWBmuazlS4iLCJpc1N5bmMiOnRydWUsImFsbEF0dHJpYnV0ZVZhbHVlcyI6W3siYXR0cmlidXRlVmFsdWUiOiLljrvlhoXohI8ifSx7ImF0dHJpYnV0ZVZhbHVlIjoi5LiN5Y675YaF6ISPIn1dfQ==', 1511013623532);
INSERT INTO xjh.dishesattribute_list (dishesAttributeId, dishesAttributeName, dishesAttributeMarkInfo, isValueRadio, isSync, dishesAttributeObj, creatTime) VALUES(5, '温度', '', 1, 1, 'eyJpc1ZhbHVlUmFkaW8iOnRydWUsInNlbGVjdGVkQXR0cmlidXRlVmFsdWVzIjpbXSwiZGlzaGVzQXR0cmlidXRlTWFya0luZm8iOiIiLCJkaXNoZXNBdHRyaWJ1dGVJZCI6NSwiZGlzaGVzQXR0cmlidXRlTmFtZSI6Iua4qeW6piIsImlzU3luYyI6dHJ1ZSwiYWxsQXR0cmlidXRlVmFsdWVzIjpbeyJhdHRyaWJ1dGVWYWx1ZSI6IuW4uOa4qSJ9LHsiYXR0cmlidXRlVmFsdWUiOiLlhrAifV19', 1554949980694);
