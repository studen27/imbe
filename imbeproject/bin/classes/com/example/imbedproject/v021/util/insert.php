<?php
$db_hostname = 'localhost';
$db_database = 'schoolradio';
$db_username = 'schoolradio';
$db_password = 'wjdtlf123';
$DOCUMENT_ROOT = $_SERVER["DOCUMENT_ROOT"];

$connect = mysql_connect($db_hostname, $db_username, $db_password); //DB가 있는 주소(이것은 웹서버로 직접 접속하는 것이기 때문에 루프백 주소를 써도 됨)
mysql_selectdb($db_database); //DB 선택
mysql_query("set names utf8"); //이것 또한 한글(utf8)을 지원하기 위한 것

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
	if(isset($_GET['name'])) $name = $_GET['name'];
	if(isset($_GET['latitude'])) $latitude = $_GET['latitude'];
	if(isset($_GET['longitude'])) $longitude = $_GET['longitude'];
	$qry = "insert into books(name, latitude, longitude) values('$name', $latitude, $longitude);";
	$result = mysql_query($qry);
 
	$xmlcode = "<?xml version = \"1.0\" encoding = \"utf-8\"?>\n"; //xml파일에 출력할 코드
	$xmlcode .= "<result>$result</result>\n"; //DB insert가 성공적으로 됐는지 여부를 확인하기 위해 result값을 xml로 출력시킴	
 
	$dir = "$DOCUMENT_ROOT/test"; //insertresult.xml 파일을 저장할 경로
	$filename = $dir."/insertresult.xml";
 
	file_put_contents($filename, $xmlcode); //xmlcode의 내용을 xml파일로 출력
	
	}

?>
