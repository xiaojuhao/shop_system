CREATE TABLE `accounts` (
  `accountId` int(11) NOT NULL AUTO_INCREMENT,
  `accountUser` varchar(50) NOT NULL,
  `accountNickName` varchar(225) NOT NULL,
  `accountPass` varchar(32) NOT NULL,
  `accountFather` varchar(50) DEFAULT NULL,
  `accountRight` text,
  `removeLimit` double DEFAULT NULL,
  `isDefault` int(1) NOT NULL,
  `creatTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`accountId`),
  UNIQUE KEY `accountUser` (`accountUser`)
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;



INSERT INTO xjh.accounts (accountId, accountUser, accountNickName, accountPass, accountFather, accountRight, removeLimit, isDefault, creatTime) VALUES(1, 'root', '管理员', '63a9f0ea7bb98050796b649e85481845', NULL, NULL, NULL, 1, 1496927066755);
INSERT INTO xjh.accounts (accountId, accountUser, accountNickName, accountPass, accountFather, accountRight, removeLimit, isDefault, creatTime) VALUES(6, 'localServerIn', 'localServerIn', '63a9f0ea7bb98050796b649e85481845', NULL, NULL, NULL, 0, 1501223965516);
INSERT INTO xjh.accounts (accountId, accountUser, accountNickName, accountPass, accountFather, accountRight, removeLimit, isDefault, creatTime) VALUES(7, 'jhy', '店长1', '63a9f0ea7bb98050796b649e85481845', 'root', 'WyJ1aS5NYWluSkZyYW1lLmpNZW51SXRlbTFBY3Rpb25QZXJmb3JtZWQoKSIsInVpLk1haW5KRnJhbWUuak1lbnVJdGVtMkFjdGlvblBlcmZvcm1lZCgpIiwidWkuTWFpbkpGcmFtZS5qTWVudUl0ZW0zQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5hY2NvdW50LlNvbkFjY291bnRNYW5hZ2VKRGlhbG9nLmpCdXR0b24xQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5hY2NvdW50LlNvbkFjY291bnRNYW5hZ2VKRGlhbG9nLmpCdXR0b24yQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5hY2NvdW50LlNvbkFjY291bnRNYW5hZ2VKRGlhbG9nLmpCdXR0b24zQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5hY2NvdW50LlNvbkFjY291bnRNYW5hZ2VKRGlhbG9nLmpCdXR0b240QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5NYWluSkZyYW1lLmpNZW51SXRlbThBY3Rpb25QZXJmb3JtZWQoKSIsInVpLk1haW5KRnJhbWUuak1lbnVJdGVtN0FjdGlvblBlcmZvcm1lZCgpIiwidWkuTWFpbkpGcmFtZS5qTWVudUl0ZW0xMEFjdGlvblBlcmZvcm1lZCgpIiwidWkuTWFpbkpGcmFtZS5qTWVudUl0ZW0xNUFjdGlvblBlcmZvcm1lZCgpIiwidWkuTWFpbkpGcmFtZS5qTWVudUl0ZW0yNUFjdGlvblBlcmZvcm1lZCgpIiwidWkuTWFpbkpGcmFtZS5qTWVudUl0ZW0yNkFjdGlvblBlcmZvcm1lZCgpIiwidWkuTWFpbkpGcmFtZS5qTWVudUl0ZW0xNEFjdGlvblBlcmZvcm1lZCgpIiwidWkuTWFpbkpGcmFtZS5qTWVudUl0ZW0xM0FjdGlvblBlcmZvcm1lZCgpIiwidWkuTWFpbkpGcmFtZS5qTWVudUl0ZW01QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5NYWluSkZyYW1lLmpNZW51SXRlbTExQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5NYWluSkZyYW1lLmpNZW51SXRlbTI0QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5NYWluSkZyYW1lLmpNZW51SXRlbTE2QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5jYXJkUGFja2FnZS5DYXNoQ291cG9uTWFuYWdlSkRpYWxvZy5qQnV0dG9uMUFjdGlvblBlcmZvcm1lZCgpIiwidWkuY2FyZFBhY2thZ2UuQ2FzaENvdXBvbk1hbmFnZUpEaWFsb2cuakJ1dHRvbjJBY3Rpb25QZXJmb3JtZWQoKSIsInVpLmNhcmRQYWNrYWdlLkNhc2hDb3Vwb25NYW5hZ2VKRGlhbG9nLmpCdXR0b244QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5NYWluSkZyYW1lLmpNZW51SXRlbTE3QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5NYWluSkZyYW1lLmpNZW51SXRlbTE4QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5NYWluSkZyYW1lLmpNZW51SXRlbTE5QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5NYWluSkZyYW1lLmpNZW51SXRlbTIwQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5NYWluSkZyYW1lLmpNZW51SXRlbTI3QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5NYWluSkZyYW1lLmpNZW51SXRlbTIxQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5NYWluSkZyYW1lLmpNZW51SXRlbTIyQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5NYWluSkZyYW1lLmpNZW51SXRlbTIzQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5kZXZpY2UuRGV2aWNlTWFuYWdlSkRpYWxvZy5qQnV0dG9uMUFjdGlvblBlcmZvcm1lZCgpIiwidWkuZGV2aWNlLkRldmljZU1hbmFnZUpEaWFsb2cuakJ1dHRvbjJBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza0J1dHRvbi5vcGVuRnJlZUpEaWxvZygpIiwidWkubWFpbi5EZXNrQnV0dG9uLm9wZW5Vc2VkSkRpbG9nKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25PcmRlckRpc2hlc0FjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uU2VuZERpc2hlc0FjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUmV0dXJuRGlzaGVzQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b24xVGFza0FjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uQ2hhbmdlRGVza0FjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uU21hbGxDaGFuZ2VBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvbkRpc2NvdW50QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b24xTWFuYWdlckRpc2NvdW50QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25SZWZ1bmRBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblByaW50QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25QYXlBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvbkNsb3NlRGVza0FjdGlvblBlcmZvcm1lZCgpIl0=', 0.0, 0, 1501388353167);
INSERT INTO xjh.accounts (accountId, accountUser, accountNickName, accountPass, accountFather, accountRight, removeLimit, isDefault, creatTime) VALUES(8, 'dc1', '点菜1', '63a9f0ea7bb98050796b649e85481845', 'root', 'WyJ1aS5tYWluLkRlc2tCdXR0b24ub3BlbkZyZWVKRGlsb2coKSIsInVpLm1haW4uRGVza0J1dHRvbi5vcGVuVXNlZEpEaWxvZygpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uT3JkZXJEaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNlbmREaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblJldHVybkRpc2hlc0FjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMVRhc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvbkNoYW5nZURlc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNtYWxsQ2hhbmdlQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25EaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMU1hbmFnZXJEaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUmVmdW5kQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25QcmludEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUGF5QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25DbG9zZURlc2tBY3Rpb25QZXJmb3JtZWQoKSJd', 0.0, 0, 1501392488733);
INSERT INTO xjh.accounts (accountId, accountUser, accountNickName, accountPass, accountFather, accountRight, removeLimit, isDefault, creatTime) VALUES(9, 'dc2', '点菜2', '63a9f0ea7bb98050796b649e85481845', 'root', 'WyJ1aS5tYWluLkRlc2tCdXR0b24ub3BlbkZyZWVKRGlsb2coKSIsInVpLm1haW4uRGVza0J1dHRvbi5vcGVuVXNlZEpEaWxvZygpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uT3JkZXJEaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNlbmREaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblJldHVybkRpc2hlc0FjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMVRhc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvbkNoYW5nZURlc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNtYWxsQ2hhbmdlQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25EaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMU1hbmFnZXJEaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUmVmdW5kQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25QcmludEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUGF5QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25DbG9zZURlc2tBY3Rpb25QZXJmb3JtZWQoKSJd', 0.0, 0, 1501392523861);
INSERT INTO xjh.accounts (accountId, accountUser, accountNickName, accountPass, accountFather, accountRight, removeLimit, isDefault, creatTime) VALUES(10, 'dc3', '点菜3', '63a9f0ea7bb98050796b649e85481845', 'root', 'WyJ1aS5tYWluLkRlc2tCdXR0b24ub3BlbkZyZWVKRGlsb2coKSIsInVpLm1haW4uRGVza0J1dHRvbi5vcGVuVXNlZEpEaWxvZygpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uT3JkZXJEaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNlbmREaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblJldHVybkRpc2hlc0FjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMVRhc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvbkNoYW5nZURlc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNtYWxsQ2hhbmdlQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25EaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMU1hbmFnZXJEaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUmVmdW5kQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25QcmludEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUGF5QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25DbG9zZURlc2tBY3Rpb25QZXJmb3JtZWQoKSJd', 0.0, 0, 1501392547647);
INSERT INTO xjh.accounts (accountId, accountUser, accountNickName, accountPass, accountFather, accountRight, removeLimit, isDefault, creatTime) VALUES(11, 'dc4', '点餐4', '63a9f0ea7bb98050796b649e85481845', 'root', 'WyJ1aS5tYWluLkRlc2tCdXR0b24ub3BlbkZyZWVKRGlsb2coKSIsInVpLm1haW4uRGVza0J1dHRvbi5vcGVuVXNlZEpEaWxvZygpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uT3JkZXJEaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNlbmREaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblJldHVybkRpc2hlc0FjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMVRhc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvbkNoYW5nZURlc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNtYWxsQ2hhbmdlQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25EaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMU1hbmFnZXJEaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUmVmdW5kQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25QcmludEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUGF5QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25DbG9zZURlc2tBY3Rpb25QZXJmb3JtZWQoKSJd', 0.0, 0, 1501392575931);
INSERT INTO xjh.accounts (accountId, accountUser, accountNickName, accountPass, accountFather, accountRight, removeLimit, isDefault, creatTime) VALUES(12, 'dc5', '点餐5', '63a9f0ea7bb98050796b649e85481845', 'root', 'WyJ1aS5tYWluLkRlc2tCdXR0b24ub3BlbkZyZWVKRGlsb2coKSIsInVpLm1haW4uRGVza0J1dHRvbi5vcGVuVXNlZEpEaWxvZygpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uT3JkZXJEaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNlbmREaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblJldHVybkRpc2hlc0FjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMVRhc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvbkNoYW5nZURlc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNtYWxsQ2hhbmdlQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25EaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMU1hbmFnZXJEaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUmVmdW5kQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25QcmludEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUGF5QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25DbG9zZURlc2tBY3Rpb25QZXJmb3JtZWQoKSJd', 0.0, 0, 1501392615171);
INSERT INTO xjh.accounts (accountId, accountUser, accountNickName, accountPass, accountFather, accountRight, removeLimit, isDefault, creatTime) VALUES(13, 'dc6', '点餐6', '63a9f0ea7bb98050796b649e85481845', 'root', 'WyJ1aS5tYWluLkRlc2tCdXR0b24ub3BlbkZyZWVKRGlsb2coKSIsInVpLm1haW4uRGVza0J1dHRvbi5vcGVuVXNlZEpEaWxvZygpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uT3JkZXJEaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNlbmREaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblJldHVybkRpc2hlc0FjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMVRhc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvbkNoYW5nZURlc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNtYWxsQ2hhbmdlQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25EaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMU1hbmFnZXJEaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUmVmdW5kQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25QcmludEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUGF5QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25DbG9zZURlc2tBY3Rpb25QZXJmb3JtZWQoKSJd', 0.0, 0, 1501392642647);
INSERT INTO xjh.accounts (accountId, accountUser, accountNickName, accountPass, accountFather, accountRight, removeLimit, isDefault, creatTime) VALUES(14, 'whh', '店长2', '63a9f0ea7bb98050796b649e85481845', 'root', 'WyJ1aS5tYWluLkRlc2tCdXR0b24ub3BlbkZyZWVKRGlsb2coKSIsInVpLm1haW4uRGVza0J1dHRvbi5vcGVuVXNlZEpEaWxvZygpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uT3JkZXJEaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNlbmREaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblJldHVybkRpc2hlc0FjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMVRhc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvbkNoYW5nZURlc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNtYWxsQ2hhbmdlQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25EaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMU1hbmFnZXJEaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUmVmdW5kQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25QcmludEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUGF5QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25DbG9zZURlc2tBY3Rpb25QZXJmb3JtZWQoKSJd', 0.0, 0, 1501417444229);
INSERT INTO xjh.accounts (accountId, accountUser, accountNickName, accountPass, accountFather, accountRight, removeLimit, isDefault, creatTime) VALUES(15, 'dc0', '点餐0', '63a9f0ea7bb98050796b649e85481845', 'root', 'WyJ1aS5tYWluLkRlc2tCdXR0b24ub3BlbkZyZWVKRGlsb2coKSIsInVpLm1haW4uRGVza0J1dHRvbi5vcGVuVXNlZEpEaWxvZygpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uT3JkZXJEaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNlbmREaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblJldHVybkRpc2hlc0FjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMVRhc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvbkNoYW5nZURlc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNtYWxsQ2hhbmdlQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25EaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMU1hbmFnZXJEaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUmVmdW5kQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25QcmludEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUGF5QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25DbG9zZURlc2tBY3Rpb25QZXJmb3JtZWQoKSJd', 0.0, 0, 1501467713854);
INSERT INTO xjh.accounts (accountId, accountUser, accountNickName, accountPass, accountFather, accountRight, removeLimit, isDefault, creatTime) VALUES(16, 'lh', 'lh', '63a9f0ea7bb98050796b649e85481845', 'root', 'WyJ1aS5tYWluLkRlc2tCdXR0b24ub3BlbkZyZWVKRGlsb2coKSIsInVpLm1haW4uRGVza0J1dHRvbi5vcGVuVXNlZEpEaWxvZygpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uT3JkZXJEaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNlbmREaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblJldHVybkRpc2hlc0FjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMVRhc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvbkNoYW5nZURlc2tBY3Rpb25QZXJmb3JtZWQoKSIsInVpLm1haW4uRGVza09yZGVySW5mb0pEaWFsb2cuakJ1dHRvblNtYWxsQ2hhbmdlQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25EaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uMU1hbmFnZXJEaXNjb3VudEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUmVmdW5kQWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25QcmludEFjdGlvblBlcmZvcm1lZCgpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uUGF5QWN0aW9uUGVyZm9ybWVkKCkiLCJ1aS5tYWluLkRlc2tPcmRlckluZm9KRGlhbG9nLmpCdXR0b25DbG9zZURlc2tBY3Rpb25QZXJmb3JtZWQoKSJd', 0.0, 0, 1501554945140);
INSERT INTO xjh.accounts (accountId, accountUser, accountNickName, accountPass, accountFather, accountRight, removeLimit, isDefault, creatTime) VALUES(17, 'kt1', '开台1', '63a9f0ea7bb98050796b649e85481845', 'root', 'WyJ1aS5tYWluLkRlc2tCdXR0b24ub3BlbkZyZWVKRGlsb2coKSIsInVpLm1haW4uRGVza0J1dHRvbi5vcGVuVXNlZEpEaWxvZygpIiwidWkubWFpbi5EZXNrT3JkZXJJbmZvSkRpYWxvZy5qQnV0dG9uT3JkZXJEaXNoZXNBY3Rpb25QZXJmb3JtZWQoKSJd', 0.0, 0, 1501651116130);
