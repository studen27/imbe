<?php
// 60062446 ������
// create at 2012/12/02
// modify at 2012/12/06

// å������ ���ε��ϴ� ����
$DOCUMENT_ROOT = $_SERVER["DOCUMENT_ROOT"];
$target_path = "$DOCUMENT_ROOT/test/";

// �ߺ��� �̸��� ������ ��¥���̿��Ͽ� å�� �̸��� �����Ų��.
$tmp_img = explode("." ,$_FILES['uploadedfile']['name']); 
$img_name = $tmp_img[0]."_".date('Y-m-d_H_i_s').".".$tmp_img[1];
$target_path = $target_path . basename($img_name);
if(move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $target_path)) {
	echo $img_name;
} else {
	echo "";
}

?>
