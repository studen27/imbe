<?php
$db_hostname = 'localhost';
$db_database = 'schoolradio';
$db_username = 'schoolradio';
$db_password = 'wjdtlf123';
$DOCUMENT_ROOT = $_SERVER["DOCUMENT_ROOT"];
$target_path = "$DOCUMENT_ROOT/test/";

$connect = mysql_connect($db_hostname, $db_username, $db_password); //DB�� �ִ� �ּ�(�̰��� �������� ���� �����ϴ� ���̱� ������ ������ �ּҸ� �ᵵ ��)
mysql_selectdb($db_database); //DB ����
mysql_query("set names utf8"); //�̰� ���� �ѱ�(utf8)�� �����ϱ� ���� ��





$img_name = $_FILES['uploadedfile']['name'];
if(isset($_GET['dir_name'])) $dir_name = $_GET['dir_name'];
$tmp_img = explode("." ,$dir_name); 

$query = "insert into book_image(path, name) values('$tmp_img[0]', '$img_name');";
$result = mysql_query($query);

$target_path = $target_path. $tmp_img[0] . "/" . basename($img_name);
if(move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $target_path)) {
	echo $img_name;
} else {
	echo "";
}

?>
