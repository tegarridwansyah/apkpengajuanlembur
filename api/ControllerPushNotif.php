<?php
require_once ('models/ModelPushNotif.php');

$api_object = new Notification;

$action = isset($_GET['action']) ? $_GET['action'] : $_POST['action'];

switch($action){

    case 'send':
        $data = $api_object->send($_POST["title"],$_POST["nip"], $_POST["message"]);
    break;
    
    case 'register':
        $data = $api_object->register($_POST["reg_id"], $_POST["reg_device"], $_POST["reg_device_id"], $_POST["nip"]);
    break;

    case 'getRegId':
        $data = $api_object->getRegId();
    break;

    default:
    $data = array(
        "pesan" => "gagal"
    );
    break;
}
echo json_encode($data);

?>