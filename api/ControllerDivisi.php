<?php
require_once ('models/ModelDivisi.php');

$api_object = new Divisi;

$action = isset($_GET['action']) ? $_GET['action'] : $_POST['action'];

switch($action){

    case 'auto_incrementdivisi':
        $data = $api_object->auto_incrementdivisi();
    break;

    case 'fetch_datadivisi':
        $data = $api_object->fetch_datadivisi();
    break;

    case 'insert_divisi':
        $data = $api_object->insert_divisi($_POST["id_divisi"], $_POST["nama_divisi"]);
    break;

    case 'fetch_singledivisi':
        $data = $api_object->fetch_singledivisi($_POST["id_divisi"]);
    break;

    case 'update_divisi':
        $data = $api_object->update_divisi($_POST["id_divisi"], $_POST["nama_divisi"]);
    break;

    case 'delete_divisi':
        $data = $api_object->delete_divisi($_GET["id_divisi"]);
    break;

    case 'search_iddivisi':
        $data = $api_object->search_iddivisi($_GET["nama_divisi"]);
    break;

    default:
        $data= array(
            "result" => "",
            "status" => "error",
            "pesan"  => "Terjadi kesalahan, silahkan cek kembali"  
        );
    break;
}

echo json_encode($data);

?>