<?php
// 60062446 박정실
// create at 2012/12/02
// modify at 2012/12/06

// 책을 찾는 역할
$db_hostname = 'localhost';
$db_database = 'schoolradio';
$db_username = 'schoolradio';
$db_password = 'wjdtlf123';
$DOCUMENT_ROOT = $_SERVER["DOCUMENT_ROOT"];

$connect = mysql_connect($db_hostname, $db_username, $db_password);
mysql_selectdb($db_database);
mysql_query("set names utf8");

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
	if(isset($_GET['latitude'])) $latitude = $_GET['latitude'];
	if(isset($_GET['longitude'])) $longitude = $_GET['longitude'];
	
	// 검색 대상이되는 범위 지정
	$upper_latitude = $latitude + 10000;
	$lower_latitude = $latitude - 10000;
	$upper_longitude = $longitude + 10000;
	$lower_longitude = $longitude - 10000;
	
	$query = "select * from books where latitude <= $upper_latitude && latitude >= $lower_latitude " . 
			"&& longitude <= $upper_longitude && longitude >= $lower_longitude;";

	$result = mysql_query($query);
	 
	$xmlcode = "<?xml version = \"1.0\" encoding = \"utf-8\"?>\n";
	
	// 결과로 받아온 정보를 xmlcode에 추가시킨다.
	while($row = mysql_fetch_array($result)) {
		$result_id = $row['id'];
		$result_origin_name = $row['origin_name'];
		$result_actual_name = $row['actual_name'];
		$result_latitude = $row['latitude'];
		$result_longitude = $row['longitude'];
	 
		$xmlcode .= "<node>\n";
		$xmlcode .= "<id>$result_id</id>\n";
		$xmlcode .= "<origin_name>$result_origin_name</origin_name>\n";
		$xmlcode .= "<actual_name>$result_actual_name</actual_name>\n";
		$xmlcode .= "<latitude>$result_latitude</latitude>\n";
		$xmlcode .= "<longitude>$result_longitude</longitude>\n";
		$xmlcode .= "</node>\n";
	}
	 
	// xmlcode를 실제로 쓴다.
	$dir = "$DOCUMENT_ROOT/test";
	$filename = $dir."/searchresult.xml";
	file_put_contents($filename, $xmlcode);
	
	}

?>