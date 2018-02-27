@echo off

:select
cd d:\githome
echo 请选择要发布的目录：
echo     [1]wiscess-common
echo     [2]wiscess-exporter
echo     [3]wiscess-filter
echo     [4]wiscess-importer
echo     [5]wiscess-jpa
echo     [6]wiscess-security
echo     [7]wiscess-utils
echo     [8]wiscess-wechat
echo     [0]退出

set selected=
set /p selected=请选择：

if "%selected%"=="1" goto wiscess-common
if "%selected%"=="2" goto wiscess-exporter
if "%selected%"=="3" goto wiscess-filter
if "%selected%"=="4" goto wiscess-importer
if "%selected%"=="5" goto wiscess-jpa
if "%selected%"=="6" goto wiscess-security
if "%selected%"=="7" goto wiscess-utils
if "%selected%"=="8" goto wiscess-wechat
if "%selected%"=="0" goto quit
goto select

:wiscess-common
set deployPath=wiscess-common
goto confm

:wiscess-exporter
set deployPath=wiscess-exporter
goto confm

:wiscess-filter
set deployPath=wiscess-filter
goto confm
:wiscess-importer
set deployPath=wiscess-importer
goto confm
:wiscess-jpa
set deployPath=wiscess-jpa
goto confm
:wiscess-security
set deployPath=wiscess-security
goto confm
:wiscess-utils
set deployPath=wiscess-utils
goto confm
:wiscess-wechat
set deployPath=wiscess-wechat
goto confm


:confm
if not exist %deployPath% goto select
cd %deployPath%
cd
set confm=
set /p confm=确认要发布的目录【%deployPath%】[r表示重新选择，q表示退出，直接回车继续]:
if "%confm%"=="r" goto select
if "%confm%"=="R" goto select
if "%confm%"=="q" goto quit
if "%confm%"=="Q" goto quit

:run
echo 正在发布%deployPath%...

call mvn deploy
cd..
echo 发布完成。
cd
echo 开始提交%deployPath%\到git
cd mvn-repo
cd
dir com\wiscess\%deployPath%\ /ad/w/b

:setVersion
set version=
set /p version=请选择版本[q:退出]：
if "%version%"=="q" goto select

if not exist com\wiscess\%deployPath%\%version% goto setVersion

:setM
set mark=更新%deployPath% v%version%
set /p mark=备注[回车继续]：

git add -f com\wiscess\%deployPath%\%version%
git commit -m "%mark%"
git push origin release

echo 提交git完成。
cd..
cd
goto select

:end
pause

:quit
