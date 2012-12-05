<?php
$DOCUMENT_ROOT = $_SERVER["DOCUMENT_ROOT"];
$target_path = "$DOCUMENT_ROOT/test/";

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
	if(isset($_GET['dir_name'])) $dir_name = $_GET['dir_name'];
	$tmp_img = explode("." ,$dir_name); 
	mkdir("$tmp_img[0]", 0777);
}

?>