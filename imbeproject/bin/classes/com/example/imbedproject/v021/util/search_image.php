<?php
// 60062446 ������
// create at 2012/12/05
// modify at 2012/12/06

// å�� ���õ� �̹����� ã�� ����
$db_hostname = 'localhost';
$db_database = 'schoolradio';
$db_username = 'schoolradio';
$db_password = 'wjdtlf123';
$DOCUMENT_ROOT = $_SERVER["DOCUMENT_ROOT"];

$connect = mysql_connect($db_hostname, $db_username, $db_password);
mysql_selectdb($db_database);
mysql_query("set names utf8");

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
	if(isset($_GET['path'])) $path = $_GET['path'];

	$query = "select * from book_image where path = '$path'";
	$result = mysql_query($query);
	 
	$xmlcode = "<?xml version = \"1.0\" encoding = \"utf-8\"?>\n";
	
	// ����� �޾ƿ� ������ xmlcode�� �߰���Ų��.
	while($row = mysql_fetch_array($result)) {
		$name = $row['name'];
	 
		$xmlcode .= "<node>\n";
		$xmlcode .= "<name>$name</name>\n";
		$xmlcode .= "</node>\n";
	}
	
	// xmlcode�� ������ ����.
	$dir = "$DOCUMENT_ROOT/test";
	$filename = $dir."/search_image_result.xml";
	file_put_contents($filename, $xmlcode);
	
	}

?>