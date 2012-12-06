<?php
// 60062446 박정실
// create at 2012/12/05
// modify at 2012/12/06

// 책에 관련된 이미지를 업로드하는 역할
$db_hostname = 'localhost';
$db_database = 'schoolradio';
$db_username = 'schoolradio';
$db_password = 'wjdtlf123';
$DOCUMENT_ROOT = $_SERVER["DOCUMENT_ROOT"];
$target_path = "$DOCUMENT_ROOT/test/";

$connect = mysql_connect($db_hostname, $db_username, $db_password);
mysql_selectdb($db_database);
mysql_query("set names utf8");

$img_name = $_FILES['uploadedfile']['name'];
if(isset($_GET['dir_name'])) $dir_name = $_GET['dir_name'];
$tmp_img = explode("." ,$dir_name); 

// 이미지를 DB에 등록한다.
$query = "insert into book_image(path, name) values('$tmp_img[0]', '$img_name');";
$result = mysql_query($query);

// 이미지를 업로드한다.
$target_path = $target_path. $tmp_img[0] . "/" . basename($img_name);
if(move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $target_path)) {
	echo $img_name;
} else {
	echo "";
}

?>
