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
	if(isset($_GET['path'])) $path = $_GET['path'];

	$query = "select * from book_image where path = '$path'";

	$result = mysql_query($query);
	 
	$xmlcode = "<?xml version = \"1.0\" encoding = \"utf-8\"?>\n"; //xml���Ͽ� ����� �ڵ�
	 
	while($row = mysql_fetch_array($result)) {
		$name = $row['name'];
	 
		$xmlcode .= "<node>\n";
		$xmlcode .= "<name>$name</name>\n";
		$xmlcode .= "</node>\n"; //DB������ �޾Ƴ� name�� price���� xml���Ͽ� ����ϱ� ���� �ڵ�
	}
	 
	$dir = "$DOCUMENT_ROOT/test"; //searchresult.xml ������ ������ ���
	$filename = $dir."/search_image_result.xml";
	 
	file_put_contents($filename, $xmlcode); //xmlcode�� ������ xml���Ϸ� ���
	
	}

?>