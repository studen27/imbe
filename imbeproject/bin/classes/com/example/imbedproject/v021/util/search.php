<?php
$db_hostname = 'localhost';
$db_database = 'schoolradio';
$db_username = 'schoolradio';
$db_password = 'wjdtlf123';
$DOCUMENT_ROOT = $_SERVER["DOCUMENT_ROOT"];

$connect = mysql_connect($db_hostname, $db_username, $db_password); //DB�� �ִ� �ּ�(�̰��� �������� ���� �����ϴ� ���̱� ������ ������ �ּҸ� �ᵵ ��)
mysql_selectdb($db_database); //DB ����
mysql_query("set names utf8"); //�̰� ���� �ѱ�(utf8)�� �����ϱ� ���� ��

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
	if(isset($_GET['latitude'])) $latitude = $_GET['latitude'];
	if(isset($_GET['longitude'])) $longitude = $_GET['longitude'];
	
	}
//$qry = "select * from books;";
$qry = "select * from books where latitude <= " . $latitude - 100 > " && latitude >=" .$latitude - 100 . 
								"longitude <= " . $longitude - 100 > " && longitude >=" .$longitude - 100
$result = mysql_query($qry);
 
$xmlcode = "<?xml version = \"1.0\" encoding = \"utf-8\"?>\n"; //xml���Ͽ� ����� �ڵ�
 
while($row = mysql_fetch_object($result)) {
	$result_id = $row['id'];
	$result_name = $row['name'];
	$result_latitude = $row['latitude'];
	$result_longitude = $row['longitude'];
 
    $xmlcode .= "<node>\n";
    $xmlcode .= "<id>$result_id</id>\n";
    $xmlcode .= "<name>$result_name</name>\n";
	$xmlcode .= "<latitude>$result_latitude</latitude>\n";
    $xmlcode .= "<longitude>$result_longitude</longitude>\n";
    $xmlcode .= "</node>\n"; //DB������ �޾Ƴ� name�� price���� xml���Ͽ� ����ϱ� ���� �ڵ�
}
 
$dir = "$DOCUMENT_ROOT/test"; //searchresult.xml ������ ������ ���
$filename = $dir."/searchresult.xml";
 
file_put_contents($filename, $xmlcode); //xmlcode�� ������ xml���Ϸ� ���
?>