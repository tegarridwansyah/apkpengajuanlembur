<?php
require_once ('models/ModelKaryawan.php');

$api_object = new Karyawan;

$action = isset($_GET['action']) ? $_GET['action'] : $_POST['action'];

switch($action){

    case 'auto_increment':
        $data = $api_object->auto_increment();
    break;

    case 'auto':
        $data = $api_object->auto();
    break;

    case 'login':
        $data = $api_object->login($_POST["username"], $_POST["pass"]);
    break;

    case 'fetch_datapegawai':
        $data = $api_object->fetch_datapegawai();
    break;

    case 'insert_pegawai':
        $data = $api_object->insert_pegawai($_POST["nip"], $_POST["nama"], 
                $_POST["alamat"], $_POST["no_telp"], $_POST["id_divisi"], $_POST["gaji"]);
    break;

    case 'fetch_singlepegawai':
        $data = $api_object->fetch_singlepegawai($_POST["nip"]);
    break;

    case 'update_pegawai':
        $data = $api_object->update_pegawai($_POST["nip"], $_POST["nama"], $_POST["alamat"],
        $_POST["no_telp"], $_POST["id_divisi"], $_POST["gaji"]);
    break;

    case 'delete_pegawai':
        $data = $api_object->delete_pegawai($_GET["nip"]);
    break;

    default:
        $data= array(
            "result" => "",
            "pesan"  => "error"  
        );
    break;
}

echo json_encode($data);

?>