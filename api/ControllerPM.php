<?php
    
require_once ('models/ModelPM.php');
date_default_timezone_set('Asia/Jakarta');

$api_object = new PM;

$action = isset($_GET['action']) ? $_GET['action'] : $_POST['action'];

switch($action){

    case 'auto_incrementPM':
        $data = $api_object->auto_incrementPM();
    break;

    case 'fetch_datapm':
        $data = $api_object->fetch_datapm();
    break;

    case 'insert_pm':
        $data = $api_object->insert_pm($_POST["id_pm"], $_POST["nama_pm"]);
    break;

    case 'fetch_singlepm':
        $data = $api_object->fetch_singlepm($_POST["id_pm"]);
    break;

    case 'update_pm':
        $data = $api_object->update_pm($_POST["id_pm"], $_POST["nama_pm"]);
    break;

    case 'delete_pm':
        $data = $api_object->delete_pm($_GET["id_pm"]);
    break;

    case 'search_id':
        $data = $api_object->search_id($_GET["nama_pm"], $_GET["nama_divisi"]);
    break;

    default:
    $data= array(
        "status" => "error",
        "pesan"  => "Terjadi kesalahan, silahkan cek kembali",
        "time"   => date('H:i:s d-m-y')
    );
    break;
}

echo json_encode($data);
?>