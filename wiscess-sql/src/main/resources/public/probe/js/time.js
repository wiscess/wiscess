/*
 * Licensed under the GPL License. You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE.
 */
﻿var day="";
var month="";
var ampm="";
var ampmhour="";
var myweekday="";
var year="";
mydate=new Date();
myweekday=mydate.getDay();
mymonth=mydate.getMonth()+1;
myday= mydate.getDate();
myyear= mydate.getYear();
var h=mydate.getHours();
var m=mydate.getMinutes();
var s=mydate.getSeconds();
year=(myyear > 200) ? myyear : 1900 + myyear;
if(myweekday == 0)
weekday=" 星期日 ";
else if(myweekday == 1)
weekday=" 星期一 ";
else if(myweekday == 2)
weekday=" 星期二 ";
else if(myweekday == 3)
weekday=" 星期三 ";
else if(myweekday == 4)
weekday=" 星期四 ";
else if(myweekday == 5)
weekday=" 星期五 ";
else if(myweekday == 6)
weekday=" 星期六 ";
document.write(year+"年"+mymonth+"月"+myday+"日 "+weekday+" "+h+":"+m+":"+s);