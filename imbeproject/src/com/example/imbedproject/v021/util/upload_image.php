<?php
$db_hostname = 'localhost';
$db_database = 'schoolradio';
$db_username = 'schoolradio';
$db_password = 'wjdtlf123';
$DOCUMENT_ROOT = $_SERVER["DOCUMENT_ROOT"];
$target_path = "$DOCUMENT_ROOT/test/";

$connect = mysql_connect($db_hostname, $db_username, $db_password); //DB가 있는 주소(이것은 웹서버로 직접 접속하는 것이기 때문에 루프백 주소를 써도 됨)
mysql_selectdb($db_database); //DB 선택
mysql_query("set names utf8"); //이것 또한 한글(utf8)을 지원하기 위한 것





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
