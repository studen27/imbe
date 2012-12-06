<?php
// 60062446 박정실
// create at 2012/12/02
// modify at 2012/12/06

// 책 정보를 DB에 정복하는 역할을 함
$db_hostname = 'localhost';
$db_database = 'schoolradio';
$db_username = 'schoolradio';
$db_password = 'wjdtlf123';
$DOCUMENT_ROOT = $_SERVER["DOCUMENT_ROOT"];

$connect = mysql_connect($db_hostname, $db_username, $db_password);
mysql_selectdb($db_database);
mysql_query("set names utf8");

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
	if(isset($_GET['origin_name'])) $origin_name = $_GET['origin_name'];
	if(isset($_GET['actual_name'])) $actual_name = $_GET['actual_name'];
	if(isset($_GET['latitude'])) $latitude = $_GET['latitude'];
	if(isset($_GET['longitude'])) $longitude = $_GET['longitude'];
	
	// 받아온 정보를 데이터베이스에 등록시킨다.
	$query = "insert into books(origin_name, actual_name, latitude, longitude) values('$origin_name', '$actual_name', $latitude, $longitude);";
	$result = mysql_query($query);
	
	// xmlcode 생성, Query가 바르게 실행되었을때 result의 값은 1이된다.
	$xmlcode = "<?xml version = \"1.0\" encoding = \"utf-8\"?>\n";
	$xmlcode .= "<result>$result</result>\n";
 
	// xmlcode를 실제로 쓴다.
	$dir = "$DOCUMENT_ROOT/test";
	$filename = $dir."/insertresult.xml";
	file_put_contents($filename, $xmlcode);	
}
?>
