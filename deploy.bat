@echo off

:select
cd d:\githome
echo ��ѡ��Ҫ������Ŀ¼��
echo     [1]wiscess-common
echo     [2]wiscess-exporter
echo     [3]wiscess-filter
echo     [4]wiscess-importer
echo     [5]wiscess-jpa
echo     [6]wiscess-security
echo     [7]wiscess-utils
echo     [8]wiscess-wechat
echo     [0]�˳�

set selected=
set /p selected=��ѡ��

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
set /p confm=ȷ��Ҫ������Ŀ¼��%deployPath%��[r��ʾ����ѡ��q��ʾ�˳���ֱ�ӻس�����]:
if "%confm%"=="r" goto select
if "%confm%"=="R" goto select
if "%confm%"=="q" goto quit
if "%confm%"=="Q" goto quit

:run
echo ���ڷ���%deployPath%...

call mvn deploy
cd..
echo ������ɡ�
cd
echo ��ʼ�ύ%deployPath%\��git
cd mvn-repo
cd
dir com\wiscess\%deployPath%\ /ad/w/b

:setVersion
set version=
set /p version=��ѡ��汾[q:�˳�]��
if "%version%"=="q" goto select

if not exist com\wiscess\%deployPath%\%version% goto setVersion

:setM
set mark=����%deployPath% v%version%
set /p mark=��ע[�س�����]��

git add -f com\wiscess\%deployPath%\%version%
git commit -m "%mark%"
git push origin release

echo �ύgit��ɡ�
cd..
cd
goto select

:end
pause

:quit
